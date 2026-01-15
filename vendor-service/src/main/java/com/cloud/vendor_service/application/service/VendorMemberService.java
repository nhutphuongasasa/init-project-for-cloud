package com.cloud.vendor_service.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cloud.vendor_service.application.dto.response.VendorMemberResponse;
import com.cloud.vendor_service.domain.enums.VendorMemberRole;

// import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhutphuong
 * @version 1
 * @since 2026/1/15 20:41h
 */
@Slf4j
@Service
// @RequiredArgsConstructor
public class VendorMemberService {
    //se lay vendorid tu header con jwt se la cua user 
        
    public void InviteMember(String email, VendorMemberRole role){}

    public void AcceptInviteFromOrganization(){}

    public void changeMemberRole(){}

    public void addMemberRole(UUID memberId, String additionalRole){}

    public void removeMemberRole(UUID memberId, String removalRole){}

    List<VendorMemberResponse> getMyMembers(){
        return null;
    }

}