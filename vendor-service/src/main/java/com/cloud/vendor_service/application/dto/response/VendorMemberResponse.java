package com.cloud.vendor_service.application.dto.response;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendorMemberResponse {
    private UUID id;        
    private UUID userId;     
    private String email;    
    private String status;   
    private Instant createdAt;
}