package com.cloud.inventory.application.kafka.event;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FailedUpdateQuantityEvent {
    private UUID orderId;    //                
    private UUID productVariantId;//           
    private Integer requestedQuantity;       
    private Integer availableQuantity;       
    private String errorCode;   //             
    private String errorMessage;             
    private Instant timestamp;//
}
