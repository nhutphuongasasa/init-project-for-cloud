package com.cloud.vendor_service.application.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cloud.vendor_service.application.dto.BaseInfoLogDTO;
import com.cloud.vendor_service.application.dto.request.VendorAuditLogRequest;
import com.cloud.vendor_service.domain.model.VendorAuditLog;
import com.fasterxml.jackson.databind.JsonNode;

@Mapper(componentModel = "spring")
public interface VendorAuditLogMapper {
    BaseInfoLogDTO toBaseInfoLogDTO(UUID vendorId, String action, UUID performedBy);

    default VendorAuditLogRequest toRequestAuditLog(BaseInfoLogDTO baseInfoLogDTO, JsonNode oldObj, JsonNode newObj, String reason) {

        return VendorAuditLogRequest.builder()
            .baseInfo(baseInfoLogDTO)
            .oldObj(oldObj)
            .reason(reason)
            .newObj(newObj)
            .build();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "vendorId", source = "request.baseInfo.vendorId")
    @Mapping(target = "action", source = "request.baseInfo.action")
    @Mapping(target = "performedBy", source = "request.baseInfo.performedBy")
    @Mapping(target = "oldValue", source = "request.oldObj") 
    @Mapping(target = "newValue", source = "request.newObj")
    @Mapping(target = "reason", source = "request.reason")
    VendorAuditLog toEntity(VendorAuditLogRequest request);
}
