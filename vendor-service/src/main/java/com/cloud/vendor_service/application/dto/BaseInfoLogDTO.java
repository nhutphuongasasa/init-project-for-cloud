package com.cloud.vendor_service.application.dto;

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
public class BaseInfoLogDTO {
    private UUID vendorId;
    private UUID performedBy;
    private String action;
}
