package com.cloud.vendor_service.infastructure.adapter.inbound.rest;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.cloud.vendor_service.application.dto.request.VendorSearchRequest;
import com.cloud.vendor_service.application.dto.response.VendorProfileResponse;
import com.cloud.vendor_service.application.dto.response.VendorResponse;
import com.cloud.vendor_service.application.service.VendorService;
import com.cloud.vendor_service.common.response.FormResponse;
import com.cloud.vendor_service.domain.enums.VendorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhutphuong
 * @version 2.0
 * @created 25/11/2025
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final VendorService vendorService;

    @PatchMapping("/{vendorId}/status")
    public ResponseEntity<FormResponse<VendorResponse>> updateVendorStatus(
            @PathVariable UUID vendorId,
            @RequestParam VendorStatus status) {
        
        log.info("Admin requesting status update for Vendor ID: {} to status: {}", vendorId, status);
        
        VendorResponse response = vendorService.updateStatus(vendorId, status);
        
        return ResponseEntity.ok(
            FormResponse.<VendorResponse>builder()
                .data(response)
                .message("Vendor status updated successfully")
                .timestamp(Instant.now())
                .build()
        );
    }

    @GetMapping("/all")
    public ResponseEntity<FormResponse<Page<VendorResponse>>> getAllVendors(
            @RequestParam int page,
            @RequestParam int size) {
        return ResponseEntity.ok(
            FormResponse.<Page<VendorResponse>>builder()
                .data(vendorService.getAllVendor(page, size))
                .message("Get all vendors successfully")
                .timestamp(Instant.now())
                .build()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<FormResponse<Page<VendorResponse>>> searchVendors(
            @ModelAttribute VendorSearchRequest params,
            @RequestParam int page,
            @RequestParam int size) {
        return ResponseEntity.ok(
            FormResponse.<Page<VendorResponse>>builder()
                .data(vendorService.searchVendors(params, page, size))
                .message("Search vendors successfully")
                .timestamp(Instant.now())
                .build()
        );
    }

    @GetMapping("/{vendorId}")
    public ResponseEntity<FormResponse<VendorProfileResponse>> getVendorById(
            @PathVariable String vendorId) {
        return ResponseEntity.ok(
            FormResponse.<VendorProfileResponse>builder()
                .data(vendorService.getVendorById(UUID.fromString(vendorId)))
                .message("Get vendor by id successfully")
                .timestamp(Instant.now())
                .build()
        );
    }
}
