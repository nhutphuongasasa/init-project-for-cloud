package com.cloud.product_service.application.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@ToString
public class StockSummaryDto {
    private UUID warehouseId;
    private Integer quantityAvailable;
}
