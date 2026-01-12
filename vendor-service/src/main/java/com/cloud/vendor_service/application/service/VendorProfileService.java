package com.cloud.vendor_service.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cloud.vendor_service.infastructure.adapter.outbound.repository.VendorProfileRepository;
import com.cloud.vendor_service.infastructure.adapter.outbound.repository.VendorRepository;
import com.cloud.vendor_service.infastructure.exception.custom.VendorNotFoundException;
import com.cloud.vendor_service.application.dto.request.UpdateBasicInfoVendorRequest;
import com.cloud.vendor_service.application.dto.request.UpdateProfileVendorRequest;
import com.cloud.vendor_service.application.dto.response.VendorResponse;
import com.cloud.vendor_service.application.mapper.VendorMapper;
import com.cloud.vendor_service.domain.model.Vendor;
import com.cloud.vendor_service.domain.model.VendorProfile;
import com.cloud.vendor_service.common.utils.jwt.JwtUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 24/11/2025
 */
@Service
@Slf4j
@AllArgsConstructor
public class VendorProfileService {
    private final VendorProfileRepository vendorProfileRepository;
    private final VendorRepository vendorRepository;
    private final VendorMapper vendorMapper;    
    private final JwtUtils jwtUtils;

    // Cập nhật thông tin cơ bản
    public VendorResponse updateBasicInfo(UpdateBasicInfoVendorRequest request){
        UUID vendorId = UUID.fromString(jwtUtils.getCurrentUserId());
        log.info("Updating basic info for vendorId={}", vendorId);
        Vendor existedVendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> {
                log.error("Vendor not found with id={}", vendorId);
                return new VendorNotFoundException(vendorId);
            });

        vendorMapper.updateBasicInfoVendorFromDto(request, existedVendor);
        log.debug("Basic info updated successfully for vendorId={}", vendorId);
        
        vendorRepository.save(existedVendor);

        return vendorMapper.toResponse(existedVendor);
    }

    // Cập nhật thông tin shop
    public VendorResponse updateProfile(UpdateProfileVendorRequest request){
        UUID vendorId = UUID.fromString(jwtUtils.getCurrentUserId());
        log.info("Updating profile for vendorId={}", vendorId);
        VendorProfile existedVendorProfile = vendorProfileRepository.findByVendorId(vendorId)
            .orElseThrow(() -> {
                log.error("Vendor profile not found for vendorId={}", vendorId);
                return new VendorNotFoundException(vendorId);
            });

        vendorMapper.updateVendorProfileFromDto(request, existedVendorProfile);
        log.debug("Profile updated successfully for vendorId={}", vendorId);
        
        vendorProfileRepository.save(existedVendorProfile);

        return vendorMapper.toResponse(existedVendorProfile.getVendor());
    }

    // Lấy thông tin shop
    public VendorResponse getPublicVendorBySlug(String slug){
        log.info("Fetching public vendor by slug={}", slug);
        Vendor existedVendor = vendorRepository.findBySlug(slug)
            .orElseThrow(() -> {
                log.error("Vendor not found with slug={}", slug);
                return new VendorNotFoundException(slug);
            });

        log.debug("Vendor fetched successfully with slug={}", slug);
        return vendorMapper.toResponse(existedVendor);
    }
}
