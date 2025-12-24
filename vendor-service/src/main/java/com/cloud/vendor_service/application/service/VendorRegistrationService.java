package com.cloud.vendor_service.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cloud.vendor_service.application.dto.request.CreateRequest;
import com.cloud.vendor_service.application.dto.response.VendorProfileResponse;
import com.cloud.vendor_service.application.dto.response.VendorResponse;
import com.cloud.vendor_service.application.mapper.VendorFactory;
import com.cloud.vendor_service.application.mapper.VendorMapper;
import com.cloud.vendor_service.domain.exception.custom.SlugAlreadyExistsException;
import com.cloud.vendor_service.domain.exception.custom.VendorAlreadyExistsException;
import com.cloud.vendor_service.domain.model.Vendor;
import com.cloud.vendor_service.domain.model.VendorProfile;
import com.cloud.vendor_service.infastructure.adapter.outbound.repository.VendorRepository;
import com.cloud.vendor_service.common.utils.jwt.JwtUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 24/11/2025
 */
@Service
@Slf4j
public class VendorRegistrationService {
    private final VendorRepository vendorRepository;
    private final VendorFactory vendorFactory;
    private final VendorMapper vendorMapper;
    private final JwtUtils jwtUtils;    

    public VendorRegistrationService(
        VendorRepository vendorRepository, 
        VendorFactory vendorFactory,
        VendorMapper vendorMapper,
        JwtUtils jwtUtils
    ) {
        this.vendorRepository = vendorRepository;
        this.vendorMapper = vendorMapper;
        this.vendorFactory = vendorFactory;
        this.jwtUtils = jwtUtils;
    }

    // Đăng ký shop
    public VendorResponse registerVendor(CreateRequest request){
        UUID vendorId = UUID.fromString(jwtUtils.getCurrentUserId());
        String email = jwtUtils.getCurrentUserEmail();

        log.info("Registering new vendor for vendorId={}, email={}, slug={}, logoUrl={}, description={}", vendorId, email, request.getSlug(), request.getLogoUrl(), request.getDescription());

        vendorRepository.findBySlug(request.getSlug())
            .ifPresent(vendor -> {
                log.error("Slug already exists: {}", request.getSlug());
                throw new SlugAlreadyExistsException(request.getSlug());
            });

        // if (hasVendor()) {
        //     log.error("Vendor already exists for vendorId={}", vendorId);
        //     throw new VendorAlreadyExistsException("Vendor already exists");
        // }

        Vendor newVendor = vendorFactory.createRequestToVendor(request, vendorId);

        VendorProfile profile = VendorProfile.builder()
            .email(email)
            .build();
        
        newVendor.setProfile(profile);
        profile.setVendor(newVendor);

        vendorRepository.save(newVendor);

        log.debug("Vendor registered successfully: {}", newVendor);
        return vendorMapper.toResponse(newVendor);
    }

    // Kiểm tra user hiện tại có shop chưa
    public boolean hasVendor(){
        UUID vendorId = UUID.fromString(jwtUtils.getCurrentUserId());
        boolean exists = vendorRepository.findById(vendorId)
            .map(vendor -> vendor != null)
            .orElse(false);

        log.info("Checking if vendorId={} already has vendor: {}", vendorId, exists);
        return exists;
    }

    // Lấy danh sách shop của user hiện tại
    public VendorProfileResponse getMyVendor(){
        UUID vendorId = UUID.fromString(jwtUtils.getCurrentUserId());
        log.info("Fetching vendor for vendorId={}", vendorId);

        Vendor vendor = vendorRepository.findById(vendorId)
            .orElse(null);

        if (vendor == null) {
            log.warn("No vendor found for vendorId={}", vendorId);
        } else {
            log.debug("Vendor found for vendorId={}: {}", vendorId, vendor);
        }

        return vendorMapper.toVendorProfileResponse(vendor);
    }
}
