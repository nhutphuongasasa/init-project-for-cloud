package com.cloud.auth_service.infrastructure.security.components;

import com.cloud.auth_service.common.response.FormResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

/**
 * @author nhutphuong
 * @since 2026/1/13 22:09h
 * @version 1
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null) ? auth.getName() : "Anonymous";
        String roles = (auth != null) ? auth.getAuthorities().toString() : "NONE";
        
        log.warn("Access Denied: User '{}' with roles {} tried to access protected URL: {} [{}]", 
                 username, roles, request.getRequestURI(), request.getMethod());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); 

        FormResponse<Object> errorResponse = FormResponse.builder()
                .message("Bạn không có quyền thực hiện hành động này!")
                .timestamp(Instant.now())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}