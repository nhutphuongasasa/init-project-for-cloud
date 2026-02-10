package com.cloud.vendor_service.application.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cloud.vendor_service.application.dto.response.UserResponse;
import com.cloud.vendor_service.application.dto.response.VendorMemberResponse;
import com.cloud.vendor_service.application.exception.custom.UserNotFoundException;
import com.cloud.vendor_service.application.exception.custom.VendorNotFoundException;
import com.cloud.vendor_service.application.mapper.VendorMemberMapper;
import com.cloud.vendor_service.common.utils.jwt.JwtUtils;
import com.cloud.vendor_service.domain.enums.VendorMemberStatus;
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
    private final VendorAuditLogService vendorAuditLogService;

    /**
     * moi thanh vien vao vendor
     * @param email
     * @return
     */
    public VendorMemberResponse inviteMember(String email){
        UUID userId = checkExistedUser(email);
        UUID vendorId = getCurrentVendorId();

        validateInvitationEligibility(vendorId, userId);

        VendorMember member = vendorMemberMapper.toEntity(vendorId, userId, email); 
        vendorMemberRepository.save(member);

        log.info("Invite member successful - Email: {}, VendorID: {}", email, vendorId);

        vendorAuditLogService.saveVendorAuditLog(
            vendorId,
            "INVITE_MEMBER",
            null,
            member, 
            "User invited to organization."
        );
        
        return vendorMemberMapper.toInvitedVendorMemberResponse(member);
    }

    /**
     * dam bao moi user chi dc moi 1 lan boi 1 vendor tai 1 thoi diem
     * @param vendorId
     * @param userId
     */
    private void validateInvitationEligibility(UUID vendorId, UUID userId){
        boolean isAlreadyActiveMember = vendorMemberRepository.existsByUserIdAndStatus(userId, VendorMemberStatus.ACTIVE);
        if (isAlreadyActiveMember) {
            throw new RuntimeException("User is already an active member of another vendor.");
        }

        if (vendorMemberRepository.existsByVendorIdAndUserIdAndStatus(vendorId, userId, VendorMemberStatus.INVITED)) {
            throw new RuntimeException("This user has already been invited or is already in this vendor.");
        }
    }

    // private boolean isUserActiveMemberOfVendor(UUID vendorId, UUID userId, String email){
    //     boolean isAlreadyMember = vendorMemberRepository.existsByVendorIdAndUserIdAndStatus(vendorId, userId, VendorMemberStatus.ACTIVE);
    //     if (isAlreadyMember){
    //         throw new RuntimeException("User is already a member of the vendor.");
    //     }

    //     return false;
    // }

    /**
     * kiem tra user co ton tai khong va tar ve thong tin user do
     * @param email
     * @return
     */
    private UUID checkExistedUser(String email){
        UserResponse userResponse = authClient.getUserByEmail(email);

        //tranh su co khong mong muon
        if (userResponse == null || userResponse.getEmail() == null || userResponse.getUserId() == null){
            throw new UserNotFoundException("User not found with email: " + email);
        }
        
        return userResponse.getUserId();    
    }

    /***
     * chap nhan moi tu organization
     * @param vendorId
     */
    public VendorMemberResponse AcceptInviteFromOrganization(UUID vendorId){
        UUID userId = jwtUtils.getCurrentUserId().orElseThrow(
            () -> new UserNotFoundException("Current user ID not found in JWT token.")
        );

        VendorMember member = vendorMemberRepository.findByVendorIdAndUserIdAndStatus(vendorId, userId, VendorMemberStatus.INVITED)
            .orElseThrow(
                () -> new RuntimeException("No invitation found for this user from the specified vendor.")
            );
        
        VendorMember oldMember = member;

        member.setStatus(VendorMemberStatus.ACTIVE);
        member.setJoinedAt(Instant.now());

        vendorMemberRepository.save(member);
        
        vendorAuditLogService.saveVendorAuditLog(
            vendorId,
            "ACCEPT_INVITE",
            oldMember,
            member, 
            "User accepted invitation from organization."
        );
        
        return vendorMemberMapper.toInvitedVendorMemberResponse(member);
    }

    public List<VendorMemberResponse> loadMyInvites(){
        UUID userId = jwtUtils.getCurrentUserId().orElseThrow(
            () -> new UserNotFoundException("Current user ID not found in JWT token.")
        );

        List<VendorMember> myInvites = vendorMemberRepository.findByUserIdAndStatus(
            userId,
            VendorMemberStatus.INVITED
        );

        return vendorMemberMapper.toVendorMemberResponses(myInvites);
    }

    public List<VendorMemberResponse> loadInvitedMembers(){
        return loadMemberWithStatus(VendorMemberStatus.INVITED);
    }

    List<VendorMemberResponse> getMyMembers(){
        return loadMemberWithStatus(VendorMemberStatus.ACTIVE);
    }

    private List<VendorMemberResponse> loadMemberWithStatus(VendorMemberStatus status){
        UUID vendorId = getCurrentVendorId();

        List<VendorMember> invitedMembers = vendorMemberRepository.findByVendorIdAndStatus(
            vendorId,
            status
        );

        return vendorMemberMapper.toVendorMemberResponses(invitedMembers);
    }

    private UUID getCurrentVendorId(){
        UUID vendorId = jwtUtils.getCurrentVendorId().orElseThrow(
            () -> new VendorNotFoundException("Current vendor ID not found in JWT token.")
        );

        return vendorId;
    }

  
}