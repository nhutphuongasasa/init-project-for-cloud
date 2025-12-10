package com.cloud.product_service.application.mapper;  

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Locale.Category;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import com.cloud.product_service.application.dto.request.ProductCreateRequest;
import com.cloud.product_service.application.dto.request.ProductImageRequest;
import com.cloud.product_service.application.dto.request.ProductVariantRequest;
import com.cloud.product_service.domain.enums.ProductColumn;
import com.cloud.product_service.domain.model.Product;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 28/11/2025
 */
@Component
public class ProductExcelMapper {

    private final Map<Integer, String> headerMap = new HashMap<>();

    public void mapRowToProduct(Row row, Product product, Category category){
        if (product == null) {
            //create
        }
        //update
        

    }

    public ProductCreateRequest mapRowToCreateRequest(Row row, UUID categoryId){
        cacheHeader(row.getSheet());
        
        List<String> values = extractRowValues(row);
        
        ProductVariantRequest variantRequest = ProductVariantRequest.builder()
            .price(new BigDecimal(values.get(ProductColumn.PRICE.index())))
            .originalPrice(new BigDecimal(values.get(ProductColumn.ORIGINAL_PRICE.index())))
            .attributes(mapAttributes(row))
            .weightGram(Integer.parseInt(values.get(ProductColumn.WEIGHT_GRAM.index())))
            .build();        

        List<ProductImageRequest> images = IntStream
                .rangeClosed(ProductColumn.IMAGE_START.index(), ProductColumn.IMAGE_END.index())
                .mapToObj(values::get)
                .filter(v -> v != null && !v.isBlank())
                .map(url -> ProductImageRequest.builder()
                    .url(url)
                    .isMain(false)
                    .build())
                .toList();

        return ProductCreateRequest.builder()
            // .productImages(images)
            .productVariant(List.of(variantRequest))
            .name(values.get(ProductColumn.NAME.index()))
            .slug(values.get(ProductColumn.SLUG.index()))
            .description(values.get(ProductColumn.DESCRIPTION.index()))
            .categoryId(categoryId)
            .build();
    }

    private List<String> extractRowValues(Row row) {
        return IntStream.range(0, row.getLastCellNum())
                .mapToObj(i -> getCellValue(row.getCell(i)))
                .toList();
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return cell.toString().trim();
    }

    private JsonNode mapAttributes(Row row){
        ObjectNode attributes = new ObjectMapper().createObjectNode();
        
        for (int i = ProductColumn.ATTRIBUTES_START.index(); i <= ProductColumn.ATTRIBUTES_END.index(); i++) {
            String value = getCellValue(row.getCell(i));
            if (value != null && !value.isBlank()) {
                String key = headerMap.get(i);
                if (key != null && !key.isBlank()) {
                    attributes.put(key.trim(), value.trim());
                }
            }
        }
        
        return attributes;
    }

    private void cacheHeader(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        for (Cell cell : headerRow) {
            String headerName = getCellValue(cell);
            headerMap.put(cell.getColumnIndex(), headerName);
        }
    }
}
