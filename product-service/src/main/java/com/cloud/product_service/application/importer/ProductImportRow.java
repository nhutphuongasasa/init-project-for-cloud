package com.cloud.product_service.application.importer;

import lombok.Data;
import org.apache.poi.ss.usermodel.Row;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class ProductImportRow {
    private final int rowIndex;
    private final Row row;

    private final Map<String, String> errors = new LinkedHashMap<>();

    public void addError(String field, String message) {
        errors.put(field, message);
    }

    public boolean hasError() {
        return !errors.isEmpty();
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public String getCellValue(int columnIndex) {
        var cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";
        return 
            switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue().trim();
                case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
                default -> "";
        };
    }
}