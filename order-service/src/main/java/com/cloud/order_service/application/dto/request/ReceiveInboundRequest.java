package com.cloud.order_service.application.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiveInboundRequest{
    private List<ReceiveItemRequest> items;
}