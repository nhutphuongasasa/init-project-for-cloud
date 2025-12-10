package com.cloud.product_service.application.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.hibernate.Session;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloud.product_service.application.dto.ExcelCreateProductDto;
import com.cloud.product_service.application.importer.ImportMode;
import com.cloud.product_service.application.importer.ImportResult;
import com.cloud.product_service.application.importer.ProductImportRow;
import com.cloud.product_service.common.utils.jwt.JwtUtils;
import com.cloud.product_service.domain.enums.ImportStatus;
import com.cloud.product_service.domain.enums.ProductColumn;
import com.cloud.product_service.domain.model.ImportJob;
import com.cloud.product_service.domain.model.Product;
import com.cloud.product_service.infrastructure.adapter.outbound.repository.ProductRepository;
import com.cloud.product_service.infrastructure.utils.excel.ExcelUtils;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductExcelService {

    private final EntityManager entityManager;
    private final ExcelUtils excelUtils;
    private final JwtUtils jwtUtils;
    private final ProductRepository productRepository;
    private final ProductCommandService productCommandService;
    private final ImportJobService importJobService;

    private static final int BATCH_SIZE = 1000;
    private static final int PROGRESS_INTERVAL = 50;

    @SneakyThrows
    public byte[] downFileTemplate() {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("template/product_template.xlsx");
             Workbook workbook = WorkbookFactory.create(is)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Transactional
    @Async("taskExecutor")
    public CompletableFuture<ImportResult> importFromFile(MultipartFile file, ImportMode mode) {
        // 1. Tạo job
        ImportJob importJob = importJobService.createImportJob(file.getOriginalFilename());
        ImportResult result = new ImportResult();
        result.setJobId(importJob.getId());

        Map<String, List<ProductImportRow>> groupMap = new LinkedHashMap<>();
        Map<Integer, String> attributeOptionsMap = new LinkedHashMap<>();
        long totalValidRows = 0;

        // 2. Đọc file + đếm dòng hợp lệ
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            attributeOptionsMap = initAttributeOptionsMap(sheet.getRow(0));

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String code = excelUtils.getStringCellValue(row, ProductColumn.PRODUCT_CODE.index())
                        .trim().toUpperCase();
                if (StringUtils.isBlank(code)) continue;

                totalValidRows++;
                ProductImportRow importRow = new ProductImportRow(row.getRowNum() + 1, row);
                groupMap.computeIfAbsent(code, k -> new ArrayList<>()).add(importRow);
            }
        } catch (Exception e) {
            log.error("Lỗi đọc file Excel", e);
            importJobService.failJob(importJob.getId(), "Lỗi đọc file: " + e.getMessage());
            result.addGlobalError("Không thể đọc file Excel");
            return CompletableFuture.completedFuture(result);
        }

        // 3. Bắt đầu job chính thức
        importJobService.startJob(importJob.getId(), totalValidRows);

        entityManager.unwrap(Session.class).setJdbcBatchSize(BATCH_SIZE);

        long processedRows = 0;
        long successRows = 0;
        long failedRows = 0;

        try {
            for (Map.Entry<String, List<ProductImportRow>> entry : groupMap.entrySet()) {
                List<ProductImportRow> rows = entry.getValue();

                try {
                    ExcelCreateProductDto excelDto = ExcelCreateProductDto.builder()
                            .productCode(entry.getKey())
                            .rows(rows)
                            .mode(mode)
                            .result(result)
                            .attributeOptionsMap(attributeOptionsMap)
                            .build();

                    processProductGroup(excelDto);

                    // Đếm thành công/thất bại theo lỗi trong row
                    long errorInGroup = rows.stream()
                            .filter(r -> r.getErrors() != null && !r.getErrors().isEmpty())
                            .count();

                    if (errorInGroup == 0) {
                        successRows += rows.size();
                    } else {
                        failedRows += rows.size();
                    }

                } catch (Exception e) {
                    failedRows += rows.size();
                    rows.forEach(r -> r.addError("Hệ thống", "Lỗi server: " + e.getMessage()));
                    result.addFailedRows(rows);
                    log.error("Lỗi xử lý sản phẩm: {}", entry.getKey(), e);
                }

                processedRows += rows.size();

                // Cập nhật tiến độ mỗi 50 dòng
                if (processedRows % PROGRESS_INTERVAL == 0 || processedRows == totalValidRows) {
                    importJobService.updateProgress(
                            importJob.getId(),
                            processedRows,
                            totalValidRows,
                            successRows,
                            failedRows
                    );
                }

                // Batch save
                if (processedRows % BATCH_SIZE == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }

            // Hoàn tất thành công
            importJobService.updateStatusImportJob(importJob.getId(), ImportStatus.COMPLETED);

        } catch (Exception e) {
            importJobService.failJob(importJob.getId(), "Lỗi xử lý: " + e.getMessage());
            log.error("Import job {} failed", importJob.getId(), e);
        } finally {
            entityManager.flush();
            entityManager.clear();
        }

        log.info("Import hoàn tất - Job {}: {}/{} thành công, {} lỗi",
                importJob.getId(), successRows, totalValidRows, failedRows);

        return CompletableFuture.completedFuture(result);
    }

    private Map<Integer, String> initAttributeOptionsMap(Row headerRow) {
        Map<Integer, String> map = new LinkedHashMap<>();
        for (int i = ProductColumn.ATTRIBUTES_START.index(); i < ProductColumn.ATTRIBUTES_END.index(); i++) {
            String value = excelUtils.getStringCellValue(headerRow, i);
            if (!StringUtils.isBlank(value)) {
                map.put(i, value.trim());
            }
        }
        return map;
    }

    private void processProductGroup(ExcelCreateProductDto excelDto) {
        Optional<Product> existingProductOpt = productRepository
                .findByProductCodeAndVendorId(excelDto.getProductCode(), jwtUtils.getCurrentUserId());
        boolean isNewProduct = existingProductOpt.isEmpty();

        if (excelDto.getMode() == ImportMode.CREATE_ONLY && !isNewProduct) {
            excelDto.getRows().forEach(r -> r.addError("Mã SP", "Đã tồn tại → chỉ tạo mới"));
            excelDto.getResult().addFailedRows(excelDto.getRows());
            return;
        }

        if (excelDto.getMode() == ImportMode.UPDATE_BASIC_ONLY && isNewProduct) {
            excelDto.getRows().forEach(r -> r.addError("Mã SP", "Chưa tồn tại → chỉ cập nhật"));
            excelDto.getResult().addFailedRows(excelDto.getRows());
            return;
        }

        if (isNewProduct) {
            productCommandService.createNewProductWithVariants(excelDto);
        } else {
            productCommandService.updateBasicInfo(existingProductOpt.get(), excelDto.getRows().get(0), excelDto.getResult());
            if (excelDto.getRows().size() > 1) {
                excelDto.getRows().subList(1, excelDto.getRows().size())
                        .forEach(r -> r.addError("Variant", "Chỉ cập nhật cơ bản → bỏ qua variant"));
                excelDto.getResult().addFailedRows(excelDto.getRows().subList(1, excelDto.getRows().size()));
            }
        }
    }
}