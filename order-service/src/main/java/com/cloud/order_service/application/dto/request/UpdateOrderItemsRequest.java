package com.cloud.order_service.application.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderItemsRequest{
    private List<CreateOrderItemRequest> items;
}