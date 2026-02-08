package com.cloud.auth_service.application.mapper;

import org.mapstruct.Mapper;

import com.cloud.auth_service.application.dto.response.UserResponse;
import com.cloud.auth_service.domain.model.User;
/**
 * @author nhutphuong
 * @since 2026/1/13 21:23h 
 * @version 1
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);
}
