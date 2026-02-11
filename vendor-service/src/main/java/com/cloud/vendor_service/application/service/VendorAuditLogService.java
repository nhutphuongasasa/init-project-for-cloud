package com.cloud.vendor_service.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cloud.vendor_service.application.dto.BaseInfoLogDTO;
import com.cloud.vendor_service.application.dto.request.VendorAuditLogRequest;
import com.cloud.vendor_service.application.mapper.VendorAuditLogMapper;
import com.cloud.vendor_service.common.utils.jwt.SecurityHelper;
import com.cloud.vendor_service.domain.model.VendorAuditLog;
import com.cloud.vendor_service.infrastructure.adapter.outbound.repository.VendorAuditLogRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhutphuong
 * @version 1
 * @since 2026/2/10 10:26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VendorAuditLogService {
    private final VendorAuditLogRepository vendorAuditLogRepository;
    private final VendorAuditLogMapper vendorAuditLogMapper;
    private final ObjectMapper objectMapper;
    private final SecurityHelper securityHelper;

    public void saveVendorAuditLog(UUID vendorId, String action, Object oldObj, Object newObj, String reason) {
        log.debug("Saving vendor audit log for vendorId: {}, action: {}", vendorId, action);

        UUID performedBy = securityHelper.currentUserId();

        JsonNode oldValue = (oldObj != null) ? objectMapper.valueToTree(oldObj) : null;
        JsonNode newValue = (newObj != null) ? objectMapper.valueToTree(newObj) : null;

        BaseInfoLogDTO baseInfoLogDTO = createBaseInfoLogDTO(vendorId, action, performedBy);
        VendorAuditLogRequest req = createVendorAuditLogRequest(baseInfoLogDTO, oldValue, newValue, reason);

        saveVendorAuditLog(req);        
    }   

    private void saveVendorAuditLog(VendorAuditLogRequest request) {
        VendorAuditLog auditLogRequest = vendorAuditLogMapper.toEntity(request);
        vendorAuditLogRepository.save(auditLogRequest);
    }

    private BaseInfoLogDTO createBaseInfoLogDTO(UUID vendorId, String action, UUID performedBy) {
        return vendorAuditLogMapper.toBaseInfoLogDTO(vendorId, action, performedBy);
    }

    private VendorAuditLogRequest createVendorAuditLogRequest(BaseInfoLogDTO baseInfoLogDTO, JsonNode oldObj, JsonNode newObj, String reason) {
        return vendorAuditLogMapper.toRequestAuditLog(baseInfoLogDTO, oldObj, newObj, reason);
    }
}
