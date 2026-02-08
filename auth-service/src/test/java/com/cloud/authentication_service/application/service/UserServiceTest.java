package com.cloud.authentication_service.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.cloud.auth_service.application.dto.response.UserResponse;
import com.cloud.auth_service.application.service.RoleService;
import com.cloud.auth_service.application.service.UserService;
import com.cloud.auth_service.common.utils.jwt.JwtUtils;
import com.cloud.auth_service.domain.model.Role;
import com.cloud.auth_service.domain.model.User;
import com.cloud.auth_service.infrastructure.adapter.outbound.repository.UserRepository;
import com.cloud.auth_service.infrastructure.exception.UserNotFoundException;
import com.cloud.auth_service.infrastructure.mapper.UserMapper;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private RoleService roleService;

    @Mock
    private OidcUser oidcUser;

    @InjectMocks
    private UserService userService;

    private User mockUser;
    private String email = "test@gmail.com";

    @BeforeEach
    void setUp(){
        mockUser = User.builder()
            .id(UUID.randomUUID())
            .email(email)
            .fullName("Test User")
            .roles(Set.of(new Role()))
            .build();
    }

    @Test
    @DisplayName("Sync User - Cap nhat lastlogin neu user da ton tai")
    void syncUser_UpdateExistingUser(){
        when(oidcUser.getEmail()).thenReturn(email);
        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        userService.syncUser(oidcUser, "google");

        verify(userRepository).save(any(User.class));
        verify(roleService, never()).findByCode(anyString());
    }

    @Test
    @DisplayName("Sync User - Tao user moi neu chua ton tai ")
    void syncUser_CreateNewUser(){
        when(oidcUser.getEmail()).thenReturn(email);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(oidcUser.getAttribute("sub")).thenReturn("12345");

        Role guestRole = new Role();
        guestRole.setCode("ROLE_GUEST");

        when(roleService.findByCode("ROLE_GUEST")).thenReturn(guestRole);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        userService.syncUser(oidcUser, "google");

        verify(roleService).findByCode("ROLE_GUEST");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("SYnc user - loi khong tim thay role")
    void syncUser_RoleNotFound(){
        when(oidcUser.getEmail()).thenReturn(email);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(roleService.findByCode("ROLE_GUEST")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> userService.syncUser(oidcUser, "google"));
    }

    @Test
    @DisplayName("Lay thong tin ca nhan user")
    void getMyInfo_success(){
        when(jwtUtils.getCurrentUserEmail()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(userMapper.toUserResponse(mockUser)).thenReturn(new UserResponse());

        UserResponse result = userService.getMyInfo();

        verify(userRepository).findByEmail(email);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Lay thong tin user theo email thanh cong")
    void getUserByEmail_Success(){
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(userMapper.toUserResponse(mockUser)).thenReturn(new UserResponse());

        UserResponse result = userService.getUserByEmail(email);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Loi khi tim tim user theo email khong ton tai")
    void getUserByEmail_NotFound(){
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(email));
    }

    @Test
    @DisplayName("Lay user theo id thanh cong")
    void getUserById_Success(){
        UUID id = mockUser.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));
        when(userMapper.toUserResponse(mockUser)).thenReturn(new UserResponse());

        UserResponse result = userService.getUserById(id);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Lay user theo id khong thanh cong")
    void getUserById_Failed(){
        UUID id = mockUser.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));
        when(userMapper.toUserResponse(mockUser)).thenReturn(new UserResponse());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(id));
    }

    @Test
    @DisplayName("Kiem tra user co ton tai")
    void checkExistedUser_True() {
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThat(userService.checkExistedUser(email)).isTrue();
    }

    @Test
    @DisplayName("Cap nhat last login thanh cong")
    void updateLastLogin_Success() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        userService.updateLastLogin(email);

        verify(userRepository).save(mockUser);
    }
}
