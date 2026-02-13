package com.cloud.vendor_service.application.mapper;

import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cloud.vendor_service.application.dto.response.VendorMemberResponse;
import com.cloud.vendor_service.domain.enums.VendorMemberStatus;
import com.cloud.vendor_service.domain.model.VendorMember;

@Mapper(componentModel = "spring", imports = {VendorMemberStatus.class})
public interface VendorMemberMapper {
    @Mapping(target = "status", expression = "java(VendorMemberStatus.INVITED)")
    @Mapping(target = "joinedAt", ignore = true)
    @Mapping(target = "leftAt", ignore = true)
    VendorMember toEntity(UUID vendorId, UUID userId, String email);
    
    VendorMemberResponse toInvitedVendorMemberResponse(VendorMember vendorMember);

    List<VendorMemberResponse> toVendorMemberResponses(List<VendorMember> vendorMembers);
}
