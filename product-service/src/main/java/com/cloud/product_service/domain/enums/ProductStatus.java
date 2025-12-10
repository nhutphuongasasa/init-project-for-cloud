package com.cloud.product_service.domain.enums;

public enum ProductStatus {
    DRAFT,        // mới tạo
    ACTIVE,       // đang hiển thị
    INACTIVE,     // ngưng hiển thị
    BANNED,       // bị admin cấm
    SUSPENDED;    // bị khóa
}
