package com.cloud.vendor_service.infastructure.adapter.inbound.rest;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.vendor_service.application.dto.request.CreateRequest;
import com.cloud.vendor_service.common.response.FormResponse;
import com.cloud.vendor_service.application.dto.response.VendorProfileResponse;
import com.cloud.vendor_service.application.dto.response.VendorResponse;
import com.cloud.vendor_service.application.service.VendorRegistrationService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping("/vendor-registration")
public class VendorRegistrationController {
    private final VendorRegistrationService vendorRegistrationService;

    public VendorRegistrationController(
        VendorRegistrationService vendorRegistrationService
    ) {
        this.vendorRegistrationService = vendorRegistrationService;
    }

    @PreAuthorize("hasRole('VENDOR')")
    @PostMapping("/me/register")
    public ResponseEntity<FormResponse<VendorResponse>> registerVendor(@Valid @RequestBody CreateRequest request){
        log.info("Registering vendor: {}", request);
        VendorResponse vendorResponse = vendorRegistrationService.registerVendor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(FormResponse.<VendorResponse>builder()
                .data(vendorResponse)
                .message("Register vendor successfully")
                .timestamp(Instant.now())
                .build());
    }    

    @PreAuthorize("hasRole('VENDOR')")
    @GetMapping("/me")
    public ResponseEntity<FormResponse<VendorProfileResponse>> getMyVendor(){
        log.info("Getting my vendor");
        VendorProfileResponse vendorResponse = vendorRegistrationService.getMyVendor();
        return ResponseEntity.ok(FormResponse.<VendorProfileResponse>builder()
            .data(vendorResponse)
            .message("Get my vendor successfully")
            .timestamp(Instant.now())
            .build());
    }
}
