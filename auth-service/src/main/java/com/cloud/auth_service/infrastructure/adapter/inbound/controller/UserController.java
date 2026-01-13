package com.cloud.auth_service.infrastructure.adapter.inbound.controller;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cloud.auth_service.application.dto.response.UserResponse;
import com.cloud.auth_service.application.service.UserService;
import com.cloud.auth_service.common.response.FormResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhutphuong
 * @since 2026/1/13
 * @version 1.1
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FormResponse<UserResponse>> getMyInfo() {
        UserResponse myInfo = userService.getMyInfo();
        return ResponseEntity.ok(
            FormResponse.<UserResponse>builder()
                .data(myInfo)
                .message("Lấy thông tin cá nhân thành công")
                .timestamp(Instant.now())
                .build()
        );
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<FormResponse<UserResponse>> getUserByEmail(@RequestParam String email) {
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(
            FormResponse.<UserResponse>builder()
                .data(user)
                .message("Tìm thấy người dùng có email: " + email)
                .timestamp(Instant.now())
                .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<FormResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(
            FormResponse.<UserResponse>builder()
                .data(user)
                .message("Lấy chi tiết người dùng thành công")
                .timestamp(Instant.now())
                .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<FormResponse<Page<UserResponse>>> getAllUsers(
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(
            FormResponse.<Page<UserResponse>>builder()
                .data(users)
                .message("Lấy danh sách người dùng thành công")
                .timestamp(Instant.now())
                .build()
        );
    }
}