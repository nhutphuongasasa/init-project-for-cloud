package com.cloud.auth_service.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud.auth_service.application.dto.request.UpdateRoleRequest;
import com.cloud.auth_service.application.dto.response.RoleResponse;
import com.cloud.auth_service.common.utils.jwt.JwtUtils;
import com.cloud.auth_service.domain.model.Role;
import com.cloud.auth_service.domain.model.User;
import com.cloud.auth_service.infrastructure.adapter.outbound.repository.RoleRepository;
import com.cloud.auth_service.infrastructure.adapter.outbound.repository.UserRepository;
import com.cloud.auth_service.infrastructure.exception.RoleNotFoundException;
import com.cloud.auth_service.infrastructure.exception.UserNotFoundException;
import com.cloud.auth_service.infrastructure.mapper.RoleMapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Transactional
    public void assignOfficialRole(UUID userId, String newRoleCode) {
        log.info("Approving user ID {} with role {}", userId, newRoleCode);

        User user = userService.findUserEntityById(userId);

        Role newRole = roleRepository.findByCode(newRoleCode)
                .orElseThrow(() -> new RoleNotFoundException());

        user.getRoles().removeIf(role -> 
            role.getCode().equalsIgnoreCase("PENDING") || 
            role.getCode().equalsIgnoreCase("ROLE_PENDING") ||
            role.getCode().equalsIgnoreCase("GUEST")
        );

        user.getRoles().add(newRole);

        userRepository.save(user);
        
        log.info("User {} is now officially a {}", user.getEmail(), newRoleCode);
    }

    @Transactional
    public void addRoleToUser(UUID userId, String roleCode) {
        log.info("Adding role {} to user ID: {}", roleCode, userId);

        User user = userService.findUserEntityById(userId);

        Role role = roleRepository.findByCode(roleCode)
            .orElseThrow(() -> new RoleNotFoundException());

        user.getRoles().add(role);

        userRepository.save(user);
        log.info("Successfully added role {} to user {}", roleCode, user.getEmail());
    }

    @Transactional
    public void removeRoleFromUser(UUID userId, String roleCode) {
        log.info("Removing role {} from user ID: {}", roleCode, userId);

        User user = userService.findUserEntityById(userId);

        Role roleToRemove = user.getRoles().stream()
                .filter(r -> r.getCode().equals(roleCode))
                .findFirst()
                .orElseThrow(() -> new RoleNotFoundException());

        user.getRoles().remove(roleToRemove);

        userRepository.save(user);
        log.info("Successfully removed role {} from user {}", roleCode, user.getEmail());
    }

    @Transactional
    public RoleResponse updateRole(@NonNull UUID roleId, UpdateRoleRequest request) {
        log.info("Updating role ID: {}", roleId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException());

        validateRoleCodeUniqueness(request.getCode(), roleId);

        roleMapper.updateBasicInfoRoleEntity(role, request);

        Role savedRole = roleRepository.save(role); 

        return roleMapper.toRoleResponse(savedRole);
    }

    private void validateRoleCodeUniqueness(String code, UUID currentRoleId) {
        roleRepository.findByCode(code).ifPresent(existingRole -> {
            if (currentRoleId == null || !existingRole.getId().equals(currentRoleId)) {
                log.error("Validation failed: Role code '{}' already exists", code);
                throw new RuntimeException("Mã quyền hạn '" + code + "' đã tồn tại trong hệ thống!");
            }
        });
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getMyRoles() {
        String email = jwtUtils.getCurrentUserEmail();
        log.info("Fetching roles for current logged-in user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException());

        return user.getRoles().stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getRolesByUserId(UUID userId) {
        log.info("Fetching roles for user ID: {}", userId);
        
        User user = userService.findUserEntityById(userId);

        return user.getRoles().stream()
            .map(roleMapper::toRoleResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
            .map(roleMapper::toRoleResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public Role findByCode(String code) {
        return roleRepository.findByCode(code)
            .orElseThrow(() -> new RoleNotFoundException());
    }

    @Transactional
    public void deleteRole(UUID roleId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RoleNotFoundException());

        if (userRepository.existsByRoles_Id(roleId)) {
            throw new RoleNotFoundException();
        }

        role.setIsActive(false);

        roleRepository.save(role);
        log.info("Successfully deleted role: {}", role.getCode());
    }
}
