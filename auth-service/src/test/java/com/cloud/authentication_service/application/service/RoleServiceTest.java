package com.cloud.authentication_service.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cloud.auth_service.application.dto.response.RoleResponse;
import com.cloud.auth_service.application.service.RoleService;
import com.cloud.auth_service.common.utils.jwt.JwtUtils;
import com.cloud.auth_service.domain.model.Role;
import com.cloud.auth_service.domain.model.User;
import com.cloud.auth_service.infrastructure.adapter.outbound.repository.RoleRepository;
import com.cloud.auth_service.infrastructure.adapter.outbound.repository.UserRepository;
import com.cloud.auth_service.infrastructure.exception.RoleNotFoundException;
import com.cloud.auth_service.infrastructure.exception.UserNotFoundException;
import com.cloud.auth_service.infrastructure.mapper.RoleMapper;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private RoleService roleService;

    private User mockUser;
    private Role mockRole;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        
        mockUser = new User();
        mockUser.setId(userId);
        mockUser.setEmail("phuongbt3232@gmail.com");
        mockUser.setRoles(new HashSet<>());

        mockRole = new Role();
        mockRole.setId(UUID.randomUUID());
        mockRole.setCode("ADMIN");
        mockRole.setName("Administrator");
    }

    @Test
    @DisplayName("Nâng cấp quyền lên Official thành công - Xóa role PENDING")
    void assignOfficialRole_Success() {
        Role pendingRole = new Role();
        pendingRole.setCode("PENDING");
        mockUser.getRoles().add(pendingRole);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(roleRepository.findByCode("ADMIN")).thenReturn(Optional.of(mockRole));

        roleService.assignOfficialRole(userId, "ADMIN");

        assertThat(mockUser.getRoles()).hasSize(1);
        assertThat(mockUser.getRoles().iterator().next().getCode()).isEqualTo("ADMIN");
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    @DisplayName("Thêm role cho User thành công")
    void addRoleToUser_Success() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(roleRepository.findByCode("ADMIN")).thenReturn(Optional.of(mockRole));

        // When
        roleService.addRoleToUser(userId, "ADMIN");

        // Then
        assertThat(mockUser.getRoles()).contains(mockRole);
        verify(userRepository).save(mockUser);
    }

    @Test
    @DisplayName("Ném ngoại lệ khi thêm role cho User không tồn tại")
    void addRoleToUser_UserNotFound() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> 
            roleService.addRoleToUser(userId, "ADMIN")
        );
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Xóa role khỏi User thành công")
    void removeRoleFromUser_Success() {
        // Given
        mockUser.getRoles().add(mockRole);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // When
        roleService.removeRoleFromUser(userId, "ADMIN");

        // Then
        assertThat(mockUser.getRoles()).isEmpty();
        verify(userRepository).save(mockUser);
    }

    @Test
    @DisplayName("Lấy danh sách quyền của User hiện tại thành công")
    void getMyRoles_Success() {
        // Given
        String email = "phuongbt3232@gmail.com";
        mockUser.getRoles().add(mockRole);
        RoleResponse response = new RoleResponse();
        // response.setCode("ADMIN");

        when(jwtUtils.getCurrentUserEmail()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(roleMapper.toRoleResponse(any(Role.class))).thenReturn(response);

        // When
        List<RoleResponse> result = roleService.getMyRoles();

        // Then
        assertThat(result).hasSize(1);
        // assertThat(result.get(0).getCode()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Xóa mềm Role (Active = false) thành công")
    void deleteRole_Success() {
        // Given
        UUID roleId = mockRole.getId();
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(mockRole));
        when(userRepository.existsByRoles_Id(roleId)).thenReturn(false);

        // When
        roleService.deleteRole(roleId);

        // Then
        assertThat(mockRole.getIsActive()).isFalse();
        verify(roleRepository).save(mockRole);
    }

    @Test
    @DisplayName("Lỗi khi xóa Role đang được User sử dụng")
    void deleteRole_InUse_ThrowsException() {
        // Given
        UUID roleId = mockRole.getId();
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(mockRole));
        when(userRepository.existsByRoles_Id(roleId)).thenReturn(true);

        // When & Then
        assertThrows(RoleNotFoundException.class, () -> 
            roleService.deleteRole(roleId)
        );
        verify(roleRepository, never()).save(any());
    }
}