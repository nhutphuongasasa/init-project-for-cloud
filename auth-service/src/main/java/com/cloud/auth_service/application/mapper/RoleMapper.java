package com.cloud.auth_service.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.cloud.auth_service.application.dto.request.UpdateRoleRequest;
import com.cloud.auth_service.application.dto.response.RoleResponse;
import com.cloud.auth_service.domain.model.Role;


@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse toRoleResponse(Role role);
    void updateBasicInfoRoleEntity(@MappingTarget Role role, UpdateRoleRequest request);
}
