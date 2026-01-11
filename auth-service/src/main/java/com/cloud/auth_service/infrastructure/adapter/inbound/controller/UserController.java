package com.cloud.auth_service.infrastructure.adapter.inbound.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.auth_service.common.response.FormResponse;

/**
 * @nhutphuong
 * @since 2026/1/11 12h
 * @version 1
 */
@RestController
public class UserController {
    @PostMapping("/login")
    public ResponseEntity<FormResponse<Object>> login(){
        return ResponseEntity.ok(
            FormResponse.builder()
                .data(null)
                .build()
        );
    }
}