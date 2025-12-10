package com.cloud.vendor_service.application.dto.request;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class UpdateProfileVendorRequest {
    private String address;
    private String phone;
    // private String email;
    private String taxCode;
    private String websiteUrl;
    private JsonNode socialLinks;
}
