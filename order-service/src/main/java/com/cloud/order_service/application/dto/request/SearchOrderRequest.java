package com.cloud.order_service.application.dto.request;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@ToString
public class SearchOrderRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}
