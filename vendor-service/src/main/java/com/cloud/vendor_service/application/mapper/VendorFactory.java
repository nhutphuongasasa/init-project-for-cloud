package com.cloud.vendor_service.application.mapper;

import com.cloud.vendor_service.application.dto.request.CreateRequest;
import com.cloud.vendor_service.domain.model.Vendor;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 24/11/2025
*/
@Component
public class VendorFactory {
    public Vendor createRequestToVendor(CreateRequest request, UUID vendorId){
        return Vendor.builder()
            .id(vendorId)
            .slug(request.getSlug())
            .name(request.getName())
            .description(request.getDescription())
            .logoUrl(request.getLogoUrl())
            .joinedAt(Instant.now())
            .build();
    }
}
