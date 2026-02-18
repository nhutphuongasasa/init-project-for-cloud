package com.cloud.vendor_service.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud.vendor_service.application.dto.request.UpdateBasicInfoVendorRequest;
import com.cloud.vendor_service.application.dto.request.UpdateProfileVendorRequest;
import com.cloud.vendor_service.application.dto.response.VendorResponse;
import com.cloud.vendor_service.application.exception.custom.VendorNotFoundException;
import com.cloud.vendor_service.application.mapper.VendorMapper;
import com.cloud.vendor_service.common.utils.jwt.SecurityHelper;
import com.cloud.vendor_service.domain.model.Vendor;
import com.cloud.vendor_service.domain.model.VendorProfile;
import com.cloud.vendor_service.infrastructure.adapter.outbound.repository.VendorProfileRepository;
import com.cloud.vendor_service.infrastructure.adapter.outbound.repository.VendorRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class ProfileService {
    private final VendorProfileRepository vendorProfileRepository;
    private final VendorRepository vendorRepository;
    private final VendorMapper vendorMapper;   
    private final SecurityHelper securityHelper;
    private final VendorAuditLogService vendorAuditLogService; 

    @Transactional
    public VendorResponse updateBasicInfo(UpdateBasicInfoVendorRequest request){
        UUID vendorId = securityHelper.currentVendorId();

        log.debug("Updating basic info for vendorId={} with request={}", vendorId, request);

        Vendor existedVendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new VendorNotFoundException(vendorId));

        // Lưu trạng thái trước khi update để audit
        Vendor oldVendor = existedVendor;  // tham chiếu, nhưng Jackson sẽ serialize snapshot

        vendorMapper.updateBasicInfoVendorFromDto(request, existedVendor);
        
        Vendor updatedVendor = vendorRepository.save(existedVendor);
        
        vendorAuditLogService.saveVendorAuditLog(
            vendorId,
            "UPDATE_BASIC_INFO",
            oldVendor,                // trước khi thay đổi
            updatedVendor,            // sau khi save
            "Vendor basic information updated (name, slug, logo, description, etc.)"
        );

        log.info("Basic info updated successfully for vendorId={}", vendorId);

        return vendorMapper.toResponse(updatedVendor);
    }

    @Transactional
    public VendorResponse updateProfile(UpdateProfileVendorRequest request){
        UUID vendorId = securityHelper.currentVendorId();

        log.debug("Updating profile for vendorId={} with request={}", vendorId, request);
        
        VendorProfile existedProfile = vendorProfileRepository.findByVendorId(vendorId)
            .orElseThrow(() -> {
                log.error("Vendor profile not found for vendorId={}", vendorId);
                return new VendorNotFoundException(vendorId);
            });

        // Lưu snapshot trước khi update
        VendorProfile oldProfile = existedProfile;

        vendorMapper.updateVendorProfileFromDto(request, existedProfile);

        VendorProfile updatedProfile = vendorProfileRepository.save(existedProfile);
        
        vendorAuditLogService.saveVendorAuditLog(
            vendorId,
            "UPDATE_VENDOR_PROFILE",
            oldProfile,
            updatedProfile,
            "Vendor profile updated (legal name, tax code, bank info, address, etc.)"
        );
        
        log.info("Profile updated successfully for vendorId={}", vendorId);

        return vendorMapper.toResponse(updatedProfile.getVendor());
    }
}