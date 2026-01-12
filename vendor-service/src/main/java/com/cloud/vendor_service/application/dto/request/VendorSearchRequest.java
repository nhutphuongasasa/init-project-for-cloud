package com.cloud.vendor_service.application.dto.request;

import java.util.UUID;

import com.cloud.vendor_service.domain.enums.VendorStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorSearchRequest {
    private UUID id;
    private String name;
    private String slug;
    // private UUID ownerUserId;
    private String email;
    private String taxCode;
    private VendorStatus status;

    // private String sortBy;
    // private Sort.Direction sortDirection = ;
}