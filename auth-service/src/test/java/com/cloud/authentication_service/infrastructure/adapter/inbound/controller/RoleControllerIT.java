package com.cloud.authentication_service.infrastructure.adapter.inbound.controller;

import com.cloud.authentication_service.BaseIntegrationTest;
import com.cloud.auth_service.application.dto.request.UpdateRoleRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RoleControllerIT extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    // ───────────────────────────────────────────────
    // GET /roles (getAllRoles)
    // ───────────────────────────────────────────────
    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void getAllRoles_ShouldReturnList_WhenAdminAccess() throws Exception {
        mockMvc.perform(get("/roles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lấy tất cả các quyền thành công"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllRoles_ShouldReturnForbidden_WhenNormalUserAccess() throws Exception {
        mockMvc.perform(get("/roles"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllRoles_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/roles"))
                .andExpect(status().isUnauthorized());
    }

    // ───────────────────────────────────────────────
    // GET /roles/me (getMyRoles)
    // ───────────────────────────────────────────────
    @Test
    @WithMockUser(username = "testuser", roles = {"USER", "OFFICIAL_USER"})
    void getMyRoles_ShouldReturnUserRoles_WhenAuthenticated() throws Exception {
        mockMvc.perform(get("/roles/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lấy danh sách quyền của bạn thành công"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2)); // tùy thuộc dữ liệu test
    }

    @Test
    void getMyRoles_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/roles/me"))
                .andExpect(status().isUnauthorized());
    }

    // ───────────────────────────────────────────────
    // GET /roles/user/{userId}
    // ───────────────────────────────────────────────
    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void getRolesByUserId_ShouldReturnRoles_WhenAdminAccess() throws Exception {
        UUID userId = UUID.randomUUID(); // Trong thực tế nên insert user trước rồi lấy id thật

        mockMvc.perform(get("/roles/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lấy danh sách quyền của người dùng thành công"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getRolesByUserId_ShouldReturnForbidden_WhenNormalUserAccess() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(get("/roles/user/{userId}", userId))
                .andExpect(status().isForbidden());
    }

    // ───────────────────────────────────────────────
    // POST /roles/user/{userId}/approve
    // ───────────────────────────────────────────────
    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void approveUser_ShouldReturnOk_WhenValidData() throws Exception {
        UUID userId = UUID.randomUUID(); // Nên dùng user thật trong DB test

        mockMvc.perform(post("/roles/user/{userId}/approve", userId)
                        .param("roleCode", "OFFICIAL_USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Phê duyệt người dùng thành công với quyền: OFFICIAL_USER"));
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void approveUser_ShouldFail_WhenRoleCodeNotExist() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(post("/roles/user/{userId}/approve", userId)
                        .param("roleCode", "INVALID_ROLE_999"))
                .andExpect(status().isBadRequest()); // hoặc tùy logic service của bạn
    }

    // ───────────────────────────────────────────────
    // POST /roles/user/{userId}/add
    // ───────────────────────────────────────────────
    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void addRoleToUser_ShouldSuccess_WhenAdmin() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(post("/roles/user/{userId}/add", userId)
                        .param("roleCode", "MANAGER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Đã thêm quyền MANAGER cho người dùng"));
    }

    // ───────────────────────────────────────────────
    // DELETE /roles/user/{userId}/remove
    // ───────────────────────────────────────────────
    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void removeRoleFromUser_ShouldSuccess_WhenAdmin() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/roles/user/{userId}/remove", userId)
                        .param("roleCode", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Đã thu hồi quyền USER thành công"));
    }

    // ───────────────────────────────────────────────
    // PUT /roles/{roleId}
    // ───────────────────────────────────────────────
    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void updateRole_ShouldSuccess_WhenValidRequest() throws Exception {
        UUID roleId = UUID.randomUUID();

        UpdateRoleRequest request = new UpdateRoleRequest();
        // giả sử DTO có các field như name, description
        request.setName("SUPER_ADMIN_UPDATED");
        request.setDescription("Quyền quản trị cấp cao đã được cập nhật");

        mockMvc.perform(put("/roles/{roleId}", roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Cập nhật quyền thành công"));
    }

    // ───────────────────────────────────────────────
    // DELETE /roles/{roleId}
    // ───────────────────────────────────────────────
    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void deleteRole_ShouldSuccess_WhenAdmin() throws Exception {
        UUID roleId = UUID.randomUUID();

        mockMvc.perform(delete("/roles/{roleId}", roleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa quyền thành công (vô hiệu hóa)"));
    }
}