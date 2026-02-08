package com.cloud.vendor_service.infrastructure.adapter.inbound.rest;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cloud.vendor_service.application.dto.request.UpdateBasicInfoVendorRequest;
import com.cloud.vendor_service.application.dto.request.UpdateProfileVendorRequest;
import com.cloud.vendor_service.application.dto.response.VendorResponse;
import com.cloud.vendor_service.application.service.ProfileService;
import com.cloud.vendor_service.common.response.FormResponse;

import lombok.RequiredArgsConstructor;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 25/11/2025
 */
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PutMapping("/basic-info")
    public ResponseEntity<FormResponse<VendorResponse>> updateBasicInfo(
            @RequestBody UpdateBasicInfoVendorRequest request) {
        return ResponseEntity.ok(
            FormResponse.<VendorResponse>builder()
                .data(profileService.updateBasicInfo(request))
                .message("Update basic info successfully")
                .timestamp(Instant.now())
                .build()
        );
    }

    @PutMapping()
    public ResponseEntity<FormResponse<VendorResponse>> updateProfile(
            @RequestBody UpdateProfileVendorRequest request) {
        return ResponseEntity.ok(
            FormResponse.<VendorResponse>builder()
                .data(profileService.updateProfile(request))
                .message("Update profile successfully")
                .timestamp(Instant.now())
                .build()    
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDOR')")
    @GetMapping("/slug/{slug}")
    public ResponseEntity<FormResponse<VendorResponse>> getPublicVendorBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(
            FormResponse.<VendorResponse>builder()
                .data(profileService.getPublicVendorBySlug(slug))
                .message("Get public vendor by slug successfully")
                .timestamp(Instant.now())
                .build()
        );
    }
}
