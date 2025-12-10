package com.cloud.vendor_service.domain.model;

public enum VendorStatus {
    PENDING,    // Chờ duyệt
    ACTIVE,     // Đã duyệt, hoạt động bình thường
    SUSPENDED,  // Bị treo tạm thời
    BANNED,     // Bị cấm vĩnh viễn
    REJECTED    // Bị từ chối
}

