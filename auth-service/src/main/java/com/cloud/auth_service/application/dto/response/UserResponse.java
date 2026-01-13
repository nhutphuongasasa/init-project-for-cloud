package com.cloud.auth_service.application.dto.response;

import java.time.Instant;
import java.util.Set;

import com.cloud.auth_service.domain.model.Role;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResponse {
    private String provider;     
    private String email;
    private String fullName;
    private String avatarUrl;
    private String emailVerified;
    private Instant lastLogin;
    private Set<Role> roles;
}
