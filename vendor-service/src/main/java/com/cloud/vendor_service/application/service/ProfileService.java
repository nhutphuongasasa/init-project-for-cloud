package com.cloud.vendor_service.application.service;

import java.security.Security;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud.vendor_service.application.dto.request.UpdateBasicInfoVendorRequest;
import com.cloud.vendor_service.application.dto.request.UpdateProfileVendorRequest;
import com.cloud.vendor_service.application.dto.response.VendorResponse;
import com.cloud.vendor_service.application.exception.custom.VendorNotFoundException;
import com.cloud.vendor_service.application.mapper.VendorMapper;
import com.cloud.vendor_service.common.utils.jwt.JwtUtils;
import com.cloud.vendor_service.common.utils.jwt.SecurityHelper;
import com.cloud.vendor_service.domain.model.Vendor;
import com.cloud.vendor_service.domain.model.VendorProfile;
import com.cloud.vendor_service.infrastructure.adapter.outbound.repository.VendorProfileRepository;
import com.cloud.vendor_service.infrastructure.adapter.outbound.repository.VendorRepository;

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
public class ProfileService {
    private final VendorProfileRepository vendorProfileRepository;
    private final VendorRepository vendorRepository;
    private final VendorMapper vendorMapper;   
    private final SecurityHelper securityHelper;

    /***
     * Update basic info of vendor
     * @param request
     * @return
     */
    @Transactional
    public VendorResponse updateBasicInfo(UpdateBasicInfoVendorRequest request){
        UUID vendorId = securityHelper.currentVendorId();

        log.debug("Updating basic info for vendorId={} with request={}", vendorId, request);

        Vendor existedVendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> {
                return new VendorNotFoundException(vendorId);
            });

        vendorMapper.updateBasicInfoVendorFromDto(request, existedVendor);
        
        vendorRepository.save(existedVendor);
        
        log.info("Basic info updated successfully for vendorId={}", vendorId);

        return vendorMapper.toResponse(existedVendor);
    }

    /***
     * Update profile of vendor
     * @param request
     * @return
     */
    public VendorResponse updateProfile(UpdateProfileVendorRequest request){
        UUID vendorId = securityHelper.currentVendorId();

        log.debug("Updating profile for vendorId={} with request={}", vendorId, request);
        
        VendorProfile existedVendorProfile = vendorProfileRepository.findByVendorId(vendorId)
            .orElseThrow(() -> {
                log.error("Vendor profile not found for vendorId={}", vendorId);
                return new VendorNotFoundException(vendorId);
            });

        vendorMapper.updateVendorProfileFromDto(request, existedVendorProfile);

        vendorProfileRepository.save(existedVendorProfile);
        
        log.info("Profile updated successfully for vendorId={}", vendorId);

        return vendorMapper.toResponse(existedVendorProfile.getVendor());
    }
}
