package com.cloud.order_service.application.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiveItemRequest{
    private Long detailId;
    private Integer quantityReceived;
    private String notes;
}