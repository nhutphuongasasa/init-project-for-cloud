package com.cloud.product_service.domain.enums;

public enum ProductColumn {
    PRODUCT_CODE(0),
    NAME(1),
    SLUG(2),
    DESCRIPTION(3),
    CATEGORY_SLUG(4),
    COLOR(5),
    SIZE(6),
    PRICE(7),
    ORIGINAL_PRICE(8),
    STOCK(9),
    WEIGHT_GRAM(10),
    ATTRIBUTES_START(11),
    ATTRIBUTES_END(15),
    IMAGE_START(16),
    IMAGE_END(20);

    private final int index;
    ProductColumn(int index) { this.index = index; }
    public int index() { return index; }
}