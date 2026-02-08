package com.cloud.vendor_service.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cloud.vendor_service.application.dto.response.UserResponse;
import com.cloud.vendor_service.application.dto.response.VendorMemberResponse;
import com.cloud.vendor_service.application.exception.custom.UserNotFoundException;
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
    private final VendorMemberRepository vendorMemberRepository;

    public void InviteMember(String email){
        UUID userId = checkExistedUser(email);

        // UUID vendorId = 
    }

    private UUID checkExistedUser(String email){
        UserResponse userResponse = authClient.getUserByEmail(email);

        //tranh su co khong mong muon
        if (userResponse == null || userResponse.getEmail() == null || userResponse.getUserId() == null){
            throw new UserNotFoundException(email);
        }
        
        return userResponse.getUserId();    
    }

    public void AcceptInviteFromOrganization(){}

    public void changeMemberRole(){}

    public void addMemberRole(UUID memberId, String additionalRole){}

    public void removeMemberRole(UUID memberId, String removalRole){}

    List<VendorMemberResponse> getMyMembers(){
        return null;
    }

}