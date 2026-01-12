package com.cloud.auth_service.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud.auth_service.application.dto.response.UserResponse;
import com.cloud.auth_service.domain.model.User;
import com.cloud.auth_service.infrastructure.adapter.outbound.repository.UserRepository;
import com.cloud.auth_service.infrastructure.exception.UserNotFoundException;
import com.cloud.auth_service.infrastructure.mapper.UserMapper;

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

    public UserResponse getMyInfo() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub"); 
            
            log.info("Fetching 'My Info' for email: {}", email);

            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException());

            return userMapper.toUserResponse(user);
        }

        log.error("No JWT found in Security Context");
        throw new RuntimeException("Unauthorized");
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

    @Transactional
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
