package com.cloud.vendor_service.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cloud.vendor_service.application.dto.response.InvitedVendorMemberResponse;
import com.cloud.vendor_service.application.dto.response.UserResponse;
import com.cloud.vendor_service.application.dto.response.VendorMemberResponse;
import com.cloud.vendor_service.application.dto.response.VendorResponse;
import com.cloud.vendor_service.application.exception.custom.UserNotFoundException;
import com.cloud.vendor_service.application.exception.custom.VendorNotFoundException;
import com.cloud.vendor_service.application.mapper.VendorMemberMapper;
import com.cloud.vendor_service.common.utils.jwt.JwtUtils;
import com.cloud.vendor_service.domain.model.Vendor;
import com.cloud.vendor_service.domain.model.VendorMember;
import com.cloud.vendor_service.infrastructure.adapter.outbound.openfeign.client.AuthClient;
import com.cloud.vendor_service.infrastructure.adapter.outbound.repository.VendorMemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhutphuong
 * @version 1
 * @since 2026/1/15 20:41h
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VendorMemberService {
    private final AuthClient authClient;
    private final JwtUtils jwtUtils;
    private final VendorMemberMapper vendorMemberMapper;
    private final VendorMemberRepository vendorMemberRepository;

    public InvitedVendorMemberResponse inviteMember(String email){
        UUID userId = checkExistedUser(email);

        UUID vendorId = jwtUtils.getCurrentVendorId().orElseThrow(
            () -> new VendorNotFoundException("Current vendor ID not found in JWT token.")
        );

        boolean isAlreadyMember = vendorMemberRepository.existsByVendorIdAndUserId(vendorId, userId);
        if (isAlreadyMember){
            log.warn("User with email {} is already a member of vendor {}", email, vendorId);
            throw new RuntimeException("User is already a member of the vendor.");
        }

        VendorMember vendorMember = vendorMemberMapper.toEntity(vendorId, userId); 
        vendorMemberRepository.save(vendorMember);

        log.info("Invite member successful - Email: {}, VendorID: {}", email, vendorId);
        return vendorMemberMapper.toInvitedVendorMemberResponse(vendorMember, email);
    }

    private UUID checkExistedUser(String email){
        UserResponse userResponse = authClient.getUserByEmail(email);

        //tranh su co khong mong muon
        if (userResponse == null || userResponse.getEmail() == null || userResponse.getUserId() == null){
            throw new UserNotFoundException(email);
        }
        
        return userResponse.getUserId();    
    }

    public void AcceptInviteFromOrganization(){

    }

    public void loadInvitedMembers(){

    }

    public void loadMyInvites(){

    }

    List<VendorMemberResponse> getMyMembers(){
        return null;
    }

}