package com.cloud.vendor_service.infastructure.adapter.inbound.rest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.cloud.vendor_service.application.dto.request.VendorSearchRequest;
import com.cloud.vendor_service.application.dto.response.VendorResponse;
import com.cloud.vendor_service.application.service.VendorAdminService;
import com.cloud.vendor_service.common.response.FormResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 25/11/2025
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/admin")
public class VendorAdminController {
    private final VendorAdminService vendorAdminService;

    public VendorAdminController(VendorAdminService vendorAdminService) {
        this.vendorAdminService = vendorAdminService;
    }

    @GetMapping("/all")
    public ResponseEntity<FormResponse<Page<VendorResponse>>> getAllVendors(
            @RequestParam int page,
            @RequestParam int size) {
        return ResponseEntity.ok(
            FormResponse.<Page<VendorResponse>>builder()
                .data(vendorAdminService.getAllVendoer(page, size))
                .message("Get all vendors successfully")
                .timestamp(Instant.now())
                .build()
        );
    }

    @PostMapping("/approve/{vendorId}")
    public ResponseEntity<FormResponse<VendorResponse>> approveVendor(
            @PathVariable String vendorId) {
        return ResponseEntity.ok(
            FormResponse.<VendorResponse>builder()
                .data(vendorAdminService.approveVendor(UUID.fromString(vendorId)))
                .message("Approve vendor successfully")
                .timestamp(Instant.now())
                .build()
        );
    }

    @PostMapping("/reject/{vendorId}")
    public ResponseEntity<FormResponse<VendorResponse>> rejectVendor(
            @PathVariable String vendorId) {
        return ResponseEntity.ok(
            FormResponse.<VendorResponse>builder()
                .data(vendorAdminService.rejectVendor(UUID.fromString(vendorId)))
                .message("Reject vendor successfully")
                .timestamp(Instant.now())
                .build()
        );
    }

    @PostMapping("/suspend/{vendorId}")
    public ResponseEntity<FormResponse<VendorResponse>> suspendVendor(
            @PathVariable String vendorId) {
        return ResponseEntity.ok(
            FormResponse.<VendorResponse>builder()
                .data(vendorAdminService.suspendVendor(UUID.fromString(vendorId)))
                .message("Suspend vendor successfully")
                .timestamp(Instant.now())
                .build()
        );
    }

    @PostMapping("/ban/{vendorId}")
    public ResponseEntity<FormResponse<VendorResponse>> banVendor(
            @PathVariable String vendorId) {
        return ResponseEntity.ok(
            FormResponse.<VendorResponse>builder()
                .data(vendorAdminService.banVendor(UUID.fromString(vendorId)))
                .message("Ban vendor successfully")
                .timestamp(Instant.now())
                .build()
        );
    }

    @GetMapping("/pending")
    public ResponseEntity<FormResponse<List<VendorResponse>>> getPendingVendors() {
        return ResponseEntity.ok(
            FormResponse.<List<VendorResponse>>builder()
                .data(vendorAdminService.getPendingVendors())
                .message("Get pending vendors successfully")
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
                .data(vendorAdminService.searchVendors(params, page, size))
                .message("Search vendors successfully")
                .timestamp(Instant.now())
                .build()
        );
    }
}
