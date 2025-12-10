package com.cloud.vendor_service.application.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud.vendor_service.application.dto.request.UpdateBasicInfoVendorRequest;
import com.cloud.vendor_service.application.dto.request.UpdateProfileVendorRequest;
import com.cloud.vendor_service.application.dto.response.ProfileResponse;
import com.cloud.vendor_service.application.dto.response.VendorResponse;
import com.cloud.vendor_service.domain.model.Vendor;
import com.cloud.vendor_service.domain.model.VendorProfile;

@Mapper(componentModel = "spring")
public interface VendorMapper {
    VendorResponse toResponse(Vendor vendor);
    // @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Vendor updateBasicInfoVendorFromDto(UpdateBasicInfoVendorRequest request, @MappingTarget Vendor vendor);
    ProfileResponse toProfileResponse(VendorProfile vendorProfile);
    // @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVendorProfileFromDto(UpdateProfileVendorRequest request, @MappingTarget VendorProfile vendorProfile);
}
