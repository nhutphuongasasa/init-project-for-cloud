package com.cloud.order_service.application.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.cloud.order_service.domain.enums.InboundStatus;

import lombok.Data;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class InboundSummaryResponse {
    private UUID id;
    private String inboundCode;
    private String supplierName;
    private InboundStatus status;
    private Instant expectedAt;
    private Instant createdAt;
    private Integer totalItems;
    private Integer receivedItems;
}