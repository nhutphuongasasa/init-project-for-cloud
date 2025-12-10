package com.cloud.vendor_service.application.dto.response;

import java.time.Instant;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ProfileResponse {
    private String vendorId;
    private String address;
    private String phone;
    private String email;
    private String taxCode;
    private String websiteUrl;
    private JsonNode socialLinks;
    private Instant updatedAt;
}