package com.cloud.order_service.application.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PickingItemRequest{
    private Long detailId;                    // id của fulfillment_order_details
    private Integer quantityPicked;
    private String notes;
}