package com.cloud.vendor_service.application.dto.response;

import java.time.Instant;

import com.cloud.vendor_service.domain.enums.VendorStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VendorProfileResponse {
    private String id;
    private String name;
    private String slug;
    private String logoUrl;
    private String description;
    // private String ownerUserId;
    private VendorStatus status;
    private Instant joinedAt;
    private ProfileResponse profile;
}
