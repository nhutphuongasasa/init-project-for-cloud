package com.cloud.auth_service.application.service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud.auth_service.application.dto.response.UserResponse;
import com.cloud.auth_service.domain.model.Role;
import com.cloud.auth_service.domain.model.User;
import com.cloud.auth_service.infrastructure.adapter.outbound.repository.UserRepository;
import com.cloud.auth_service.infrastructure.exception.UserNotFoundException;
import com.cloud.auth_service.infrastructure.mapper.UserMapper;
import com.cloud.auth_service.common.utils.jwt.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhutphuong
 * @since 2026/1/12 20:05h
 * @version 1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtils JwtUtils;
    private final RoleService roleService;

    public Boolean checkExistedUser(String email){
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public void syncUser(OidcUser oidcUser, String provider) {
        String email = oidcUser.getEmail();
        
        userRepository.findByEmail(email).ifPresentOrElse(
            user -> {
                updateLastLogin(email);
            },
            () -> {
                createUser(oidcUser, provider);
            }
        );
    }

    public void createUser(OidcUser oidcUser, String provider){
        String email = oidcUser.getEmail();
        String providerId = oidcUser.getAttribute("sub");
        String fullName = oidcUser.getFullName();
        String avatarUrl = oidcUser.getAttribute("picture");

        if(checkExistedUser(email)){
            throw new RuntimeException("user areadyextsited");
        }

        Role role = roleService.findByCode("GUEST");

        userRepository.save(
            User.builder()
                .provider(provider)
                .providerId(providerId)
                .email(email)
                .fullName(fullName)
                .avatarUrl(avatarUrl)
                .emailVerified(true)
                .lastLogin(Instant.now())
                .roles(Set.of(role))
                .build()
        );

       log.info("Đã lưu user mới: {}", email);

    }

    public UserResponse getMyInfo() {
        
        String email = JwtUtils.getCurrentUserEmail(); 
            
        log.info("Fetching 'My Info' for email: {}", email);

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException());

        return userMapper.toUserResponse(user);
    }

    public UserResponse getUserByEmail(String email){
        User existedUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException());

        return userMapper.toUserResponse(existedUser);
    }

    public UserResponse getUserById(UUID id) {
        log.info("Fetching user details for ID: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                log.error("User not found with ID: {}", id);
                return new UserNotFoundException();
            });

        return userMapper.toUserResponse(user);
    }

    public User findUserEntityById(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException());
    }

    public void updateLastLogin(String email) {
        log.info("Updating last login timestamp for user: {}", email);
        
        userRepository.findByEmail(email).ifPresentOrElse(
            user -> {
                user.setLastLogin(java.time.Instant.now());
                userRepository.save(user);
                log.info("Successfully updated last login for user: {}", email);
            },
            () -> log.warn("Failed to update last login: User with email {} not found", email)
        );
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Request to get a page of users with pageable: {}", pageable);
        return userRepository.findAll(pageable)
            .map(userMapper::toUserResponse);
    }
}
