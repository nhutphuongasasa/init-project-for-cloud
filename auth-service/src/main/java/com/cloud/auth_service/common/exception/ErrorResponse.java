package com.cloud.auth_service.common.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorResponse {
    private String code;  
    private String message;
    private String path;
    private String method;
    private String timestamp;
}
