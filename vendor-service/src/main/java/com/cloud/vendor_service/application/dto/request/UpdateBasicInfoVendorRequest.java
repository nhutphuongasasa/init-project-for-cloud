package com.cloud.vendor_service.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBasicInfoVendorRequest {
    private String name;
    private String slug;
    private String logoUrl;
    private String description;
}
