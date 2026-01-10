package com.cloud.product_service.domain.enums;

public enum ProductColumn {
    PRODUCT_NAME(0),
    PRODUCT_CODE(1),
    DESCRIPTION(2),
    CATEGORY(3),
    SKU(4),
    PRICE(5),
    ORIGINAL_PRICE(6),
    WEIGHT_GRAM(7),
    ATTRIBUTES_START(8),
    ATTRIBUTES_END(15),
    IMAGE_START(16),
    IMAGE_END(19);

    private final int index;
    
    ProductColumn(int index){ 
        this.index = index; 
    }

    public int index(){ 
        return index; 
    }
}