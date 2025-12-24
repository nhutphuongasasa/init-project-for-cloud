package com.cloud.vendor_service.application.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.cloud.vendor_service.application.dto.response.VendorProfileResponse;
import com.cloud.vendor_service.application.dto.request.VendorSearchRequest;
import com.cloud.vendor_service.application.dto.response.VendorResponse;
import com.cloud.vendor_service.application.mapper.VendorMapper;
import com.cloud.vendor_service.domain.exception.custom.VendorNotFoundException;
import com.cloud.vendor_service.domain.model.Vendor;
import com.cloud.vendor_service.domain.model.VendorStatus;
import com.cloud.vendor_service.domain.service.VendorStatusManager;
import com.cloud.vendor_service.domain.specification.VendorSpecifications;
import com.cloud.vendor_service.infastructure.adapter.outbound.repository.VendorRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 24/11/2025
 */
@Service
@Slf4j
public class VendorAdminService {
    private final VendorRepository vendorRepository;
    private final VendorMapper vendorMapper;
    private final VendorStatusManager vendorStatusManager;
    
    public VendorAdminService(
        VendorRepository vendorRepository,
        VendorMapper vendorMapper
    ) {
        this.vendorRepository = vendorRepository;
        this.vendorMapper = vendorMapper;
        this.vendorStatusManager = new VendorStatusManager();
    }

    public VendorProfileResponse getVendorById(UUID vendorId){
        log.info("Fetching vendor by id={}", vendorId);
        Vendor existedVendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> {
                log.error("Vendor not found with id={}", vendorId);
                return new VendorNotFoundException(vendorId);
            });

        log.debug("Vendor found: {}", existedVendor);
        return vendorMapper.toVendorProfileResponse(existedVendor);
    }

    public Page<VendorResponse> getAllVendoer(int page, int size){
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

    public VendorResponse approveVendor(UUID vendorId){
        log.info("Approving vendor with id={}", vendorId);
        Vendor existedVendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> {
                log.error("Vendor not found with id={}", vendorId);
                return new VendorNotFoundException(vendorId);
            });

        vendorStatusManager.approveVendor(existedVendor);
        vendorRepository.save(existedVendor);

        log.debug("Vendor approved successfully: {}", existedVendor);
        return vendorMapper.toResponse(existedVendor);
    }

    // Từ chối shop
    public VendorResponse rejectVendor(UUID vendorId){
        log.info("Rejecting vendor with id={}", vendorId);
        Vendor existedVendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> {
                log.error("Vendor not found with id={}", vendorId);
                return new VendorNotFoundException(vendorId);
            });

        vendorStatusManager.rejectVendor(existedVendor);
        vendorRepository.save(existedVendor);

        log.debug("Vendor rejected successfully: {}", existedVendor);
        return vendorMapper.toResponse(existedVendor);
    }

    // Tạm khóa shop
    public VendorResponse suspendVendor(UUID vendorId){
        log.info("Suspending vendor with id={}", vendorId);
        Vendor existedVendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> {
                log.error("Vendor not found with id={}", vendorId);
                return new VendorNotFoundException(vendorId);
            });

        vendorStatusManager.suspendVendor(existedVendor);
        vendorRepository.save(existedVendor);

        log.debug("Vendor suspended successfully: {}", existedVendor);
        return vendorMapper.toResponse(existedVendor);
    }
    
    // Cấm vĩnh viễn shop
    public VendorResponse banVendor(UUID vendorId){
        log.info("Banning vendor with id={}", vendorId);
        Vendor existedVendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> {
                log.error("Vendor not found with id={}", vendorId);
                return new VendorNotFoundException(vendorId);
            });

        vendorStatusManager.banVendor(existedVendor);
        vendorRepository.save(existedVendor);

        log.debug("Vendor banned successfully: {}", existedVendor);
        return vendorMapper.toResponse(existedVendor);
    }
    
    // Lấy danh sách shop chờ duyệt
    public List<VendorResponse> getPendingVendors(){
        log.info("Fetching pending vendors");
        List<Vendor> vendors = vendorRepository.findByStatus(VendorStatus.PENDING)
            .orElse(Collections.emptyList());

        log.debug("Found {} pending vendors", vendors.size());
        return vendors.stream()
            .map(vendorMapper::toResponse)
            .collect(Collectors.toList());  
    }

    // public VendorResponse findByOwnerUserId(UUID ownerId){
    //     log.info("Finding vendor by ownerId={}", ownerId);
    //     Vendor vendor = vendorRepository.findByOwnerUserId(ownerId)
    //         .orElse(null);

    //     if (vendor == null) {
    //         log.warn("No vendor found for ownerId={}", ownerId);
    //     } else {
    //         log.debug("Vendor found: {}", vendor);
    //     }

    //     return vendorMapper.toResponse(vendor);
    // }
    
    // Tìm kiếm shop
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
