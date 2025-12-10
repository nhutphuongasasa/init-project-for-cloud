package com.cloud.product_service.application.importer;

public enum ImportMode {
    CREATE_ONLY,               // Chỉ create mới (group variants nếu nhiều row)
    CREATE_AND_UPDATE_BASIC,   // Create nếu mới + update basic nếu tồn tại
    UPDATE_BASIC_ONLY          // Chỉ update basic nếu tồn tại
}
