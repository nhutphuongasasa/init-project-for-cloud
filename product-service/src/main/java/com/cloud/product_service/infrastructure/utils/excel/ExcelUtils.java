package com.cloud.product_service.infrastructure.utils.excel;

import java.math.BigDecimal;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;
/**
 * @author nhutphuong
 */
@Component
public class ExcelUtils {
    public String getStringCellValue(Row row, int cellIndex){
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";
        return 
            switch(cell.getCellType()){
                case STRING -> cell.getStringCellValue().trim();
                case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
                default -> "";
            };
    }    

    public BigDecimal getBigDecimalCellValue(Row row, int idx){
        Cell cell = row.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;
        return cell.getCellType() == CellType.NUMERIC
            ? BigDecimal.valueOf(cell.getNumericCellValue())
            : new BigDecimal(cell.getStringCellValue().replaceAll("[^\\d.]", ""));
    }

    public  Integer getIntegerCellValue(Row row, int idx){
        Cell cell = row.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;
        return cell.getCellType() == CellType.NUMERIC
            ? (int) cell.getNumericCellValue()
            : Integer.valueOf(cell.getStringCellValue().replaceAll("[^\\d]", ""));
    }

    
}