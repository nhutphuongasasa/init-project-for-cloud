package com.cloud.vendor_service.application.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cloud.vendor_service.application.dto.response.InvitedVendorMemberResponse;
import com.cloud.vendor_service.domain.enums.VendorMemberStatus;
import com.cloud.vendor_service.domain.model.VendorMember;

/**
 * @author nhutphuong
 * @version 1
 * @since 2026/2/9 16:15h
 */
@Mapper(componentModel = "spring", imports = {VendorMemberStatus.class})
public interface VendorMemberMapper {
    @Mapping(target = "status", expression = "java(VendorMemberStatus.INVITED)")
    @Mapping(target = "joinedAt", ignore = true)
    @Mapping(target = "leftAt", ignore = true)
    VendorMember toEntity(UUID vendorId, UUID userId);

    
    InvitedVendorMemberResponse toInvitedVendorMemberResponse(VendorMember vendorMember, String email);
}
