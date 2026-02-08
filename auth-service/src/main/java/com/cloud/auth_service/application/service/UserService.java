package com.cloud.auth_service.application.service;

import java.time.Instant;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtils JwtUtils;
    private final RoleService roleService;

    /**
     * Kiểm tra xem email đã tồn tại trong hệ thống hay chưa.
     *
     * @param email email cần kiểm tra
     * @return true nếu email đã tồn tại, false nếu chưa
     */
    public Boolean checkExistedUser(String email){
        return userRepository.existsByEmail(email);
    }

    /**
     * Nếu user đã tồn tại → chỉ cập nhật last login.<br>
     * Nếu chưa tồn tại → tạo user mới với role GUEST mặc định.
     * 
     * @param oidcUser thông tin user từ OAuth provider
     * @param provider tên provider (google, github, ...)
     */
    @Transactional
    public void syncUser(OidcUser oidcUser, String provider) {
        String email = oidcUser.getEmail();
        
        log.info("🔍 Starting sync for user: {}", email);
        
        if (checkExistedUser(email)) {
            log.info("User exists, updating last login: {}", email);
            updateLastLoginInternal(email);
        } else {
            log.info("➕ User not found, start creating new user: {}", email);
            createUserInternal(oidcUser, provider);
        }
        
        log.info("Sync completed for user: {}", email);
    }

    private void createUserInternal(OidcUser oidcUser, String provider) {
        String email = oidcUser.getEmail();
        String providerId = oidcUser.getAttribute("sub");
        String fullName = oidcUser.getFullName();
        String avatarUrl = oidcUser.getAttribute("picture");

        log.info("Finding GUEST role...");
        Role role = roleService.findByCode("ROLE_GUEST");
        
        if (role == null) {
            log.error("GUEST role not found in database!");
            throw new RuntimeException("GUEST role not found");
        }
        
        log.info("GUEST role found: {} (ID: {})", role.getName(), role.getId());

        User newUser = User.builder()
                .provider(provider)
                .providerId(providerId)
                .email(email)
                .fullName(fullName)
                .avatarUrl(avatarUrl)
                .emailVerified(true)
                .lastLogin(Instant.now())
                .roles(Set.of(role))
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("User created successfully with ID: {} for email: {}", savedUser.getId(), email);
    }

    private void updateLastLoginInternal(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setLastLogin(Instant.now());
            userRepository.save(user);
            log.info("Last login updated for: {}", email);
        });
    }

    // @Transactional
    // public void createUser(OidcUser oidcUser, String provider){
    //     String email = oidcUser.getEmail();
        
    //     if(checkExistedUser(email)){
    //         log.warn("User already exists: {}", email);
    //         throw new RuntimeException("User already exists: " + email);
    //     }

    //     createUserInternal(oidcUser, provider);
    // }

    
     /**
     * Lấy thông tin chi tiết của user hiện tại (dựa trên JWT).
     *
     * @return UserResponse thông tin user
     * @throws UserNotFoundException nếu không tìm thấy user
     */
    public UserResponse getMyInfo() {
        String email = JwtUtils.getCurrentUserEmail(); 
        log.info("Fetching 'My Info' for email: {}", email);

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> {
                log.error("❌ User not found for email: {}", email);
                return new UserNotFoundException();
            });

        return userMapper.toUserResponse(user);
    }

    /**
     * Lấy thông tin user theo email.
     *
     * @param email email của user
     * @return UserResponse thông tin user
     * @throws UserNotFoundException nếu không tìm thấy
     */
    public UserResponse getUserByEmail(String email){
        log.info("🔍 Finding user by email: {}", email);
        
        User existedUser = userRepository.findByEmail(email)
            .orElseThrow(() -> {
                log.error("❌ User not found for email: {}", email);
                return new UserNotFoundException();
            });

        log.info("✅ User found: {}", existedUser.getId());
        return userMapper.toUserResponse(existedUser);
    }

    /**
     * Lấy thông tin user theo ID.
     *
     * @param id UUID của user
     * @return UserResponse thông tin user
     * @throws UserNotFoundException nếu không tìm thấy
     */
    public UserResponse getUserById(UUID id) {
        log.info("Fetching user details for ID: {}", id);

        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                log.error("User not found with ID: {}", id);
                return new UserNotFoundException();
            });

        return userMapper.toUserResponse(user);
    }

    /**
     * Cập nhật thời gian đăng nhập cuối cùng cho user.
     *
     * @param email email của user
     */
    @Transactional
    public void updateLastLogin(String email) {
        log.info("Updating last login timestamp for user: {}", email);
        
        userRepository.findByEmail(email).ifPresentOrElse(
            user -> {
                user.setLastLogin(Instant.now());
                userRepository.save(user);
                log.info("Successfully updated last login for user: {}", email);
            },
            () -> log.warn("Failed to update last login: User with email {} not found", email)
        );
    }

    /**
     * Lấy danh sách tất cả user theo phân trang.
     *
     * @param pageable thông tin phân trang (page, size, sort)
     * @return Page<UserResponse> trang user
     */
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Request to get a page of users with pageable: {}", pageable);
        return userRepository.findAll(pageable)
            .map(userMapper::toUserResponse);
    }
}