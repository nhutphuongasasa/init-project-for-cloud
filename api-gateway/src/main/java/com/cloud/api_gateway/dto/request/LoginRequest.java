package com.cloud.api_gateway.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class LoginRequest {
    private String email;
    private String password;
}
