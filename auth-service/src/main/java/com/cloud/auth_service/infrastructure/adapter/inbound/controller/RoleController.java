package com.cloud.auth_service.infrastructure.adapter.inbound.controller;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cloud.auth_service.application.dto.request.UpdateRoleRequest;
import com.cloud.auth_service.application.dto.response.RoleResponse;
import com.cloud.auth_service.application.service.RoleService;
import com.cloud.auth_service.common.response.FormResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "Role Controller")
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Lấy quyền của tôi")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FormResponse<List<RoleResponse>>> getMyRoles() {
        List<RoleResponse> roles = roleService.getMyRoles();
        return ResponseEntity.ok(
            FormResponse.<List<RoleResponse>>builder()
                .data(roles)
                .message("Lấy danh sách quyền của bạn thành công")
                .timestamp(Instant.now())
                .build()
        );
    }

    @Operation(summary = "Lấy quyền theo UserId")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<FormResponse<List<RoleResponse>>> getRolesByUserId(@PathVariable UUID userId) {
        List<RoleResponse> roles = roleService.getRolesByUserId(userId);
        return ResponseEntity.ok(
            FormResponse.<List<RoleResponse>>builder()
                .data(roles)
                .message("Lấy danh sách quyền của người dùng thành công")
                .timestamp(Instant.now())
                .build()
        );
    }

    @Operation(summary = "Lấy tất cả các quyền")
    @GetMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<FormResponse<List<RoleResponse>>> getAllRoles() {
        return ResponseEntity.ok(
            FormResponse.<List<RoleResponse>>builder()
                .data(roleService.getAllRoles())
                .message("Lấy tất cả các quyền thành công")
                .timestamp(Instant.now())
                .build()
        );
    }

    @Operation(summary = "Cập nhật quyền")
    @PutMapping("/{roleId}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<FormResponse<RoleResponse>> updateRole(
            @PathVariable UUID roleId, 
            @RequestBody UpdateRoleRequest request) {
        
        RoleResponse updatedRole = roleService.updateRole(roleId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
            FormResponse.<RoleResponse>builder()
                .data(updatedRole)
                .message("Cập nhật quyền thành công")
                .timestamp(Instant.now())
                .build()
        );
    }

    @Operation(summary = "Xóa (vô hiệu hóa) quyền")
    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<FormResponse<Void>> deleteRole(@PathVariable UUID roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.ok(
            FormResponse.<Void>builder()
                .message("Xóa quyền thành công (vô hiệu hóa)")
                .timestamp(Instant.now())
                .build()
        );
    }

    @Operation(summary = "Phê duyệt người dùng mới")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @PostMapping("/user/{userId}/approve")
    public ResponseEntity<FormResponse<Void>> approveUser(
            @PathVariable UUID userId, 
            @RequestParam String roleCode) {
        
        roleService.assignOfficialRole(userId, roleCode);
        return ResponseEntity.ok(
            FormResponse.<Void>builder()
                .message("Phê duyệt người dùng thành công với quyền: " + roleCode)
                .timestamp(Instant.now())
                .build()
        );
    }

    @Operation(summary = "Thêm quyền cho người dùng")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @PostMapping("/user/{userId}/add")
    public ResponseEntity<FormResponse<Void>> addRoleToUser(
            @PathVariable UUID userId, 
            @RequestParam String roleCode) {
        
        roleService.addRoleToUser(userId, roleCode);
        return ResponseEntity.ok(
            FormResponse.<Void>builder()
                .message("Đã thêm quyền " + roleCode + " cho người dùng")
                .timestamp(Instant.now())
                .build()
        );
    }

    @Operation(summary = "Thu hồi quyền của người dùng")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @DeleteMapping("/user/{userId}/remove")
    public ResponseEntity<FormResponse<Void>> removeRoleFromUser(
            @PathVariable UUID userId, 
            @RequestParam String roleCode) {
        
        roleService.removeRoleFromUser(userId, roleCode);
        return ResponseEntity.ok(
            FormResponse.<Void>builder()
                .message("Đã thu hồi quyền " + roleCode + " thành công")
                .timestamp(Instant.now())
                .build()
        );
    }
}