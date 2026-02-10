package com.cloud.vendor_service.application.dto.request;

import com.cloud.vendor_service.application.dto.BaseInfoLogDTO;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author nhutphuong
 * @version 1
 * @since 2026/2/10 10:45
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VendorAuditLogRequest {
    BaseInfoLogDTO baseInfo;
    private JsonNode oldObj;
    private JsonNode newObj;
    private String reason;
}
