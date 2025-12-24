package com.cloud.order_service.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UpdateQuantiryReceivedRequest {
    private Integer quantityReceived;
}
