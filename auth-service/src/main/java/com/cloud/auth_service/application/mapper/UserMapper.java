package com.cloud.auth_service.application.mapper;

import org.mapstruct.Mapper;

import com.cloud.auth_service.application.dto.response.UserResponse;
import com.cloud.auth_service.domain.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);
}
