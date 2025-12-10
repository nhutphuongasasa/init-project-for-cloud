package com.cloud.product_service.application.dto;

import java.util.List;
import java.util.Map;

import com.cloud.product_service.application.importer.ImportMode;
import com.cloud.product_service.application.importer.ImportResult;
import com.cloud.product_service.application.importer.ProductImportRow;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ExcelCreateProductDto {
    private String productCode;
    private List<ProductImportRow> rows;
    private ImportMode mode;
    private ImportResult result;
    private Map<Integer, String> attributeOptionsMap;
}
