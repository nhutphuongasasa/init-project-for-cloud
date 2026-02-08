// package com.cloud.product_service.application.service;

// import java.io.ByteArrayOutputStream;
// import java.io.InputStream;
// import java.util.ArrayList;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import java.util.concurrent.CompletableFuture;

// import org.apache.poi.ss.usermodel.Row;
// import org.apache.poi.ss.usermodel.Sheet;
// import org.apache.poi.ss.usermodel.Workbook;
// import org.apache.poi.ss.usermodel.WorkbookFactory;
// import org.hibernate.Session;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.multipart.MultipartFile;

// import com.cloud.product_service.application.dto.ExcelCreateProductDto;
// import com.cloud.product_service.application.importer.ImportMode;
// import com.cloud.product_service.application.importer.ImportResult;
// import com.cloud.product_service.application.importer.ProductImportRow;
// import com.cloud.product_service.common.utils.jwt.JwtUtils;
// import com.cloud.product_service.domain.enums.ImportStatus;
// import com.cloud.product_service.domain.enums.ProductColumn;
// import com.cloud.product_service.domain.model.ImportJob;
// import com.cloud.product_service.domain.model.Product;
// import com.cloud.product_service.infrastructure.adapter.outbound.repository.ProductRepository;
// import com.cloud.product_service.infrastructure.utils.excel.ExcelUtils;

// import io.micrometer.common.util.StringUtils;
// import jakarta.persistence.EntityManager;
// import lombok.RequiredArgsConstructor;
// import lombok.SneakyThrows;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class ProductExcelService {

//     private final EntityManager entityManager;
//     private final ExcelUtils excelUtils;
//     private final JwtUtils jwtUtils;
//     private final ProductRepository productRepository;
//     private final ProductCommandService productCommandService;
//     private final ImportJobService importJobService;

//     private static final int BATCH_SIZE = 1000;
//     private static final int PROGRESS_INTERVAL = 50;

//     @SneakyThrows
//     public byte[] downFileTemplate() {
//         //try se tu dong close tai nguyen
//         try (
//             InputStream is = getClass().getClassLoader().getResourceAsStream("template/product_template.xlsx");
//             Workbook workbook = WorkbookFactory.create(is)
//         ) {
//             ByteArrayOutputStream out = new ByteArrayOutputStream();
//             workbook.write(out);
//             return out.toByteArray();
//         }
//     }

//     @Transactional
//     @Async("taskExecutor")
//     public CompletableFuture<ImportResult> importFromFile(MultipartFile file, ImportMode mode) {

//         ImportJob importJob = importJobService.createImportJob(file.getOriginalFilename());
//         ImportResult result = new ImportResult();
//         result.setJobId(importJob.getId());

//         Map<String, List<ProductImportRow>> groupMap = new LinkedHashMap<>();
//         Map<Integer, String> attributeOptionsMap = new LinkedHashMap<>();
//         long totalValidRows = 0;

//         try (
//             //khoi tao streaming 
//             InputStream is = file.getInputStream();
//             Workbook workbook = WorkbookFactory.create(is)
//         ) {
//             Sheet sheet = workbook.getSheetAt(0);
//             //gan cac header vao map
//             attributeOptionsMap = initAttributeOptionsMap(sheet.getRow(0));

//             //duyet cai sheet dau tien  vua lay ra
//             for (Row row : sheet) {
//                 //bo qua cai row dau tien do la header
//                 if (row.getRowNum() == 0) continue;

//                 String code = excelUtils.getStringCellValue(row, ProductColumn.PRODUCT_CODE.index())
//                     .trim().toUpperCase();
//                 if (StringUtils.isBlank(code)) continue;

//                 totalValidRows++;
//                 ProductImportRow importRow = new ProductImportRow(row.getRowNum() + 1, row);
//                 groupMap.computeIfAbsent(code, k -> new ArrayList<>()).add(importRow);
//             }
//         } catch (Exception e) {
//             log.error("Lỗi đọc file Excel", e);
//             importJobService.failJob(importJob.getId(), "Lỗi đọc file: " + e.getMessage());
//             result.addGlobalError("Không thể đọc file Excel");
//             return CompletableFuture.completedFuture(result);
//         }

//         // 3. Bắt đầu job chính thức
//         importJobService.startJob(importJob.getId(), totalValidRows);

//         entityManager.unwrap(Session.class).setJdbcBatchSize(BATCH_SIZE);

//         long processedRows = 0;
//         // long successRows = 0;
//         // long failedRows = 0;

//         try {
//             //duyet qua cac phan tu product da duoc go nhom trong map
//             for (Map.Entry<String, List<ProductImportRow>> entry : groupMap.entrySet()) {
//                 List<ProductImportRow> rows = entry.getValue();

//                 try {
//                     ExcelCreateProductDto excelDto = ExcelCreateProductDto.builder()
//                             .productCode(entry.getKey())
//                             .rows(rows)
//                             .mode(mode)
//                             .result(result)
//                             .attributeOptionsMap(attributeOptionsMap)
//                             .build();

//                     processImportProductGroup(excelDto);

//                 } catch (Exception e) {
//                     rows.forEach(r -> r.addError("Hệ thống", "Lỗi server: " + e.getMessage()));
//                     result.addFailedRows(rows);
//                     log.error("Lỗi xử lý sản phẩm: {}", entry.getKey(), e);
//                 }

//                 processedRows += rows.size();

//                 // Cập nhật tiến độ mỗi 50 dòng
//                 if (processedRows % PROGRESS_INTERVAL == 0 || processedRows == totalValidRows) {
//                     importJobService.updateProgress(
//                             importJob.getId(),
//                             processedRows,
//                             totalValidRows,
//                             0,
//                             0
//                     );
//                 }

//                 // Batch save
//                 if (processedRows % BATCH_SIZE == 0) {
//                     entityManager.flush();
//                     entityManager.clear();
//                 }
//             }

//             // Hoàn tất thành công
//             importJobService.updateStatusImportJob(importJob.getId(), ImportStatus.COMPLETED);

//         } catch (Exception e) {
//             importJobService.failJob(importJob.getId(), "Lỗi xử lý: " + e.getMessage());
//             log.error("Import job {} failed", importJob.getId(), e);
//         } finally {
//             entityManager.flush();
//             entityManager.clear();
//         }

//         log.info("Import hoàn tất - Job");

//         return CompletableFuture.completedFuture(result);
//     }

//     //lay gia tri cac header vao map
//     private Map<Integer, String> initAttributeOptionsMap(Row headerRow) {
//         Map<Integer, String> map = new LinkedHashMap<>();
//         for (int i = ProductColumn.ATTRIBUTES_START.index(); i <= ProductColumn.ATTRIBUTES_END.index(); i++) {
//             String value = excelUtils.getStringCellValue(headerRow, i);
//             if (!StringUtils.isBlank(value)) {
//                 map.put(i, value.trim());
//             }
//         }
//         return map;
//     }

//     //thuc hien  import vao db theo mode
//     private void processImportProductGroup(ExcelCreateProductDto excelDto) {
//         Optional<Product> existingProduct = productRepository
//                 .findByProductCodeAndVendorId(excelDto.getProductCode(), jwtUtils.getCurrentUserId());
//         boolean isNewProduct = existingProduct.isEmpty();

//         ImportMode mode = excelDto.getMode();

//         if (mode == ImportMode.CREATE_ONLY && !isNewProduct) {
//             excelDto.getRows().forEach(r -> r.addError("Mã SP", "Đã tồn tại → chỉ tạo mới"));
//             excelDto.getResult().addFailedRows(excelDto.getRows());
//             return;
//         }

//         if (mode == ImportMode.UPDATE_BASIC_ONLY && isNewProduct) {
//             excelDto.getRows().forEach(r -> r.addError("Mã SP", "Chưa tồn tại → chỉ cập nhật"));
//             excelDto.getResult().addFailedRows(excelDto.getRows());
//             return;
//         }

//         if (isNewProduct) {
//             productCommandService.importProductFromExcel(excelDto);
//         } else {
//             //thuc hien update basic info
//             // if (excelDto.getRows().size() > 1) {
//                 // excelDto.getRows().subList(1, excelDto.getRows().size())
//                         // .forEach(r -> r.addError("Variant", "Chỉ cập nhật cơ bản → bỏ qua variant"));
//                 // excelDto.getResult().addFailedRows(excelDto.getRows().subList(1, excelDto.getRows().size()));
//             // }
//         }
//     }
// }