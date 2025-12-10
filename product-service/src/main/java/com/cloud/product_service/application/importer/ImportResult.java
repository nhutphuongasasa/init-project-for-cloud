package com.cloud.product_service.application.importer;

import java.util.UUID;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImportResult {
    private UUID jobId;
    private int success = 0;
    private final List<ProductImportRow> failedRows = new ArrayList<>();
    private final List<String> globalErrors = new ArrayList<>();

    public void addSuccess(int count) {
        this.success += count;
    }

    public void addFailedRow(ProductImportRow row) {
        failedRows.add(row);
    }

    public void addFailedRows(List<ProductImportRow> rows) {
        failedRows.addAll(rows);
    }

    public void addGlobalError(String message) {
        globalErrors.add(message);
    }

    public boolean hasError() {
        return !failedRows.isEmpty() || !globalErrors.isEmpty();
    }

    public int getTotalProcessed() {
        return success + failedRows.size();
    }
}