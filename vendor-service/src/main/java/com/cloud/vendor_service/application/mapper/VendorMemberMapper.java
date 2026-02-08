package com.cloud.vendor_service.application.mapper;

import org.mapstruct.Mapper;

import com.cloud.vendor_service.domain.model.VendorMember;

@Mapper(componentModel = "spring")
public interface VendorMemberMapper {
    VendorMember toEntity(VendorMember vendorMember);
}
