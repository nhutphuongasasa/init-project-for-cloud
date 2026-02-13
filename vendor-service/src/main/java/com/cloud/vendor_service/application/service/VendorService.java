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
import com.cloud.vendor_service.common.utils.jwt.SecurityHelper;
import com.cloud.vendor_service.domain.enums.VendorStatus;
import com.cloud.vendor_service.domain.model.Vendor;
import com.cloud.vendor_service.infrastructure.adapter.outbound.repository.VendorRepository;
import com.cloud.vendor_service.infrastructure.specification.VendorSpecifications;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class VendorService {
    private final VendorRepository vendorRepository;
    private final VendorMapper vendorMapper;
    private final VendorAuditLogService vendorAuditLogService;
    private final SecurityHelper securityHelper;

    @Transactional(readOnly = true)
    public Vendor findByVendorEntityById(@NonNull UUID vendorId){
        return vendorRepository.findById(vendorId)
            .orElseThrow(() -> {
                return new VendorNotFoundException("Vendor not found with id: " + vendorId);
        });
    }

    @Transactional(readOnly = true)
    public VendorProfileResponse getVendorById(@NonNull UUID vendorId){
        Vendor existedVendor = findByVendorEntityById(vendorId);

        log.debug("Vendor found: {}", existedVendor);
        return vendorMapper.toVendorProfileResponse(existedVendor);
    }

    @Transactional(readOnly = true)
    public Page<VendorResponse> getAllVendor(int page, int size){
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

    /***
     * Register a new vendor
     * @param request
     * @return
     */
    @Transactional
    public VendorResponse registerVendor(CreateRequest request){
        UUID vendorId = securityHelper.currentVendorId();
        String email = securityHelper.currentUserEmail();

        log.debug("Registering new vendor for vendorId={}, email={}, slug={}, logoUrl={}, description={}", vendorId, email, request.getSlug(), request.getLogoUrl(), request.getDescription());

        vendorRepository.findBySlug(request.getSlug())
            .ifPresent(vendor -> {
                throw new SlugAlreadyExistsException(request.getSlug());
            });

        Vendor newVendor = vendorMapper.toVendorEntity(request);

        newVendor.addNewProfile(email);
        vendorRepository.save(newVendor);

        vendorAuditLogService.saveVendorAuditLog(
            newVendor.getId(),
            "REGISTER_VENDOR",
            null,
            newVendor,
            "New vendor registered."
        );

        log.debug("Vendor registered successfully: {}", newVendor);
        return vendorMapper.toResponse(newVendor);
    }

    /***
     * Check if the current user has a vendor
     * @return
     */
    @Transactional(readOnly = true)
    public boolean hasVendor(){
        UUID vendorId = securityHelper.currentVendorId();
        return vendorRepository.existsById(vendorId);
    }

    @Transactional(readOnly = true)
    public VendorProfileResponse getMyVendor(){
        UUID vendorId = securityHelper.currentVendorId();
        Vendor vendor = findByVendorEntityById(vendorId);
        return vendorMapper.toVendorProfileResponse(vendor);
    }

    /**
     * Update vendor status with validation
     * @param targetStatus
     * @return
     */
    @Transactional
    public VendorResponse updateStatus(UUID vendorId, VendorStatus targetStatus) {
        log.debug("Updating vendor status to {}", targetStatus);
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new VendorNotFoundException("Vendor not found with id: " + vendorId));

        validateStatusTransition(vendor.getStatus(), targetStatus);

        VendorStatus oldValue = vendor.getStatus();

        vendor.setStatus(targetStatus);
        Vendor updatedVendor = vendorRepository.save(vendor);

        vendorAuditLogService.saveVendorAuditLog(
            vendorId,
            "UPDATE_VENDOR_STATUS",
            oldValue,
            updatedVendor.getStatus(),
            "Vendor status updated."
        );
        
        log.info("Vendor {} status updated successfully", vendorId);
        return vendorMapper.toResponse(updatedVendor);
    }

    /***
     * Validate status transition
     * @param currentStatus
     * @param targetStatus
     */
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
            throw new InvalidVendorStatusTransitionException(
                "Invalid status transition from %s to %s", 
                currentStatus, 
                targetStatus
            );
        }
    }

    public Page<VendorResponse> searchVendors(VendorSearchRequest params, int page, int size){  
        log.debug("Searching vendors with params={}, page={}, size={}", params, page, size);
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
