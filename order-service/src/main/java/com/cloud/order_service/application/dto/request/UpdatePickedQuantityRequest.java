package com.cloud.order_service.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UpdatePickedQuantityRequest {
    private int quantityPick;
}
