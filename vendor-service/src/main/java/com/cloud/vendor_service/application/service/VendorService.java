package com.cloud.vendor_service.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud.vendor_service.application.dto.response.VendorProfileResponse;
import com.cloud.vendor_service.application.dto.request.CreateRequest;
import com.cloud.vendor_service.application.dto.request.VendorSearchRequest;
import com.cloud.vendor_service.application.dto.response.VendorResponse;
import com.cloud.vendor_service.application.exception.custom.InvalidVendorStatusTransitionException;
import com.cloud.vendor_service.application.exception.custom.SlugAlreadyExistsException;
import com.cloud.vendor_service.application.exception.custom.VendorNotFoundException;
import com.cloud.vendor_service.application.mapper.VendorMapper;
import com.cloud.vendor_service.common.utils.security.AuthenticatedUserProvider;
import com.cloud.vendor_service.domain.enums.VendorStatus;
import com.cloud.vendor_service.domain.model.Vendor;
import com.cloud.vendor_service.domain.model.VendorProfile;
import com.cloud.vendor_service.infrastructure.adapter.outbound.repository.VendorRepository;
import com.cloud.vendor_service.infrastructure.specification.VendorSpecifications;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhutphuong
 * @version 2.0
 * @since 24/11/2025
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VendorService {
    private final VendorRepository vendorRepository;
    private final VendorMapper vendorMapper;
    private final AuthenticatedUserProvider userProvider;

    @Transactional(readOnly = true)
    public Vendor findByVendorEntityById(@NonNull UUID vendorId){
        return vendorRepository.findById(vendorId)
            .orElseThrow(() -> {
                log.error("Vendor not found with id={}", vendorId);
                return new VendorNotFoundException(vendorId);
        });
    }

    @Transactional(readOnly = true)
    public VendorProfileResponse getVendorById(@NonNull UUID vendorId){
        log.info("Fetching vendor by id={}", vendorId);
        
        Vendor existedVendor = findByVendorEntityById(vendorId);

        log.debug("Vendor found: {}", existedVendor);
        return vendorMapper.toVendorProfileResponse(existedVendor);
    }

    @Transactional(readOnly = true)
    public Page<VendorResponse> getAllVendor(int page, int size){
        log.info("Fetching all vendors");
        Pageable pageable = PageRequest.of(
            page, 
            size,
            Sort.by(
                Sort.Direction.ASC,
                "joinedAt"
            )
        );

        Page<VendorResponse> result = vendorRepository.findAll(pageable)
            .map(vendorMapper::toResponse);

        log.debug("Search result size={}", result.getTotalElements());
        return result;
    }

    @Transactional
    public VendorResponse registerVendor(CreateRequest request){
        UUID vendorId = userProvider.getVendorIdFromAuthentication();
        String email = userProvider.getEmailFromAuthentication();

        log.info("Registering new vendor for vendorId={}, email={}, slug={}, logoUrl={}, description={}", vendorId, email, request.getSlug(), request.getLogoUrl(), request.getDescription());

        vendorRepository.findBySlug(request.getSlug())
            .ifPresent(vendor -> {
                log.error("Slug already exists: {}", request.getSlug());
                throw new SlugAlreadyExistsException(request.getSlug());
            });

        Vendor newVendor = vendorMapper.toVendorEntity(request);

        VendorProfile profile = VendorProfile.builder()
            .email(email)
            .build();
        
        newVendor.setProfile(profile);
        profile.setVendor(newVendor);

        vendorRepository.save(newVendor);

        log.debug("Vendor registered successfully: {}", newVendor);
        return vendorMapper.toResponse(newVendor);
    }

    @Transactional(readOnly = true)
    public boolean hasVendor(){
        UUID vendorId = userProvider.getVendorIdFromAuthentication();

        boolean exists = vendorRepository.existsById(vendorId);

        log.info("Checking if vendorId={} already has vendor: {}", vendorId, exists);
        return exists;
    }

    public VendorProfileResponse getMyVendor(){
        UUID vendorId = userProvider.getVendorIdFromAuthentication();

        log.info("Fetching vendor for vendorId={}", vendorId);

        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> {
                log.error("Vendor not found with id={}", vendorId);
                return new VendorNotFoundException(vendorId);
            });

        log.debug("Vendor found for vendorId={}", vendorId);

        return vendorMapper.toVendorProfileResponse(vendor);
    }

    @Transactional
    public VendorResponse updateStatus(@NonNull UUID vendorId, VendorStatus targetStatus) {
        log.info("Admin request update status for vendor: {} to {}", vendorId, targetStatus);
        
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new VendorNotFoundException(vendorId));

        validateStatusTransition(vendor.getStatus(), targetStatus);

        vendor.setStatus(targetStatus);
        Vendor updatedVendor = vendorRepository.save(vendor);
        
        log.info("Vendor {} status updated successfully to {}", vendorId, targetStatus);
        return vendorMapper.toResponse(updatedVendor);
    }

    private void validateStatusTransition(VendorStatus currentStatus, VendorStatus targetStatus) {
        boolean isValid = switch (targetStatus) {
            case ACTIVE, REJECTED -> 
                currentStatus == VendorStatus.PENDING;
            
            case SUSPENDED -> 
                currentStatus == VendorStatus.ACTIVE;
            
            case BANNED -> 
                currentStatus == VendorStatus.ACTIVE || currentStatus == VendorStatus.SUSPENDED;
            
            case PENDING -> false; 
            
            default -> false;
        };

        if (!isValid) {
            log.error("Invalid status transition from {} to {}", currentStatus, targetStatus);
            throw new InvalidVendorStatusTransitionException(
                String.format("Không thể chuyển trạng thái từ %s sang %s", currentStatus, targetStatus)
            );
        }
    }

    public Page<VendorResponse> searchVendors(VendorSearchRequest params, int page, int size){  
        log.info("Searching vendors with params={}, page={}, size={}", params, page, size);
        Pageable pageable = PageRequest.of(
            page, 
            size,
            Sort.by(
                Sort.Direction.ASC,
                "joinedAt"
            )
        );

        Specification<Vendor> spec = VendorSpecifications.buildSpecification(params);

        Page<VendorResponse> result = vendorRepository.findAll(spec, pageable)
            .map(vendorMapper::toResponse);

        log.debug("Search result size={}", result.getTotalElements());
        return result;
    }


}
