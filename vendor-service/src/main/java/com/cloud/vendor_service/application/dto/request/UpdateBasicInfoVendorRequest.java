package com.cloud.vendor_service.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class UpdateBasicInfoVendorRequest {
    private String name;
    private String slug;
    private String logoUrl;
    private String description;
}
