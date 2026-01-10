package com.cloud.auth_service.common.response;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class FormResponse<T> {
    private T data;
    private String message;
    private Instant timestamp;
}