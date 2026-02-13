package com.cloud.vendor_service.infrastructure.adapter.inbound.rest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cloud.vendor_service.application.dto.response.VendorMemberResponse;
import com.cloud.vendor_service.application.service.VendorMemberService;
import com.cloud.vendor_service.common.response.FormResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class VendorMemberController {

    private final VendorMemberService vendorMemberService;

    @PreAuthorize("@ss.hasP('vendor:manage_members')")
    @PostMapping("/invite")
    public ResponseEntity<FormResponse<VendorMemberResponse>> inviteMember(@RequestParam String email) {
        return ResponseEntity.ok(
            FormResponse.<VendorMemberResponse>builder()
                .data(vendorMemberService.inviteMember(email))
                .message("Member invitation sent successfully")
                .timestamp(Instant.now())
                .build()
        );
    }

    @PreAuthorize("@ss.hasR('GUEST') or @ss.hasR('VENDOR_STAFF')")
    @PostMapping("/accept-invite/{vendorId}")
    public ResponseEntity<FormResponse<VendorMemberResponse>> acceptInvite(@PathVariable UUID vendorId) {
        return ResponseEntity.ok(
            FormResponse.<VendorMemberResponse>builder()
                .data(vendorMemberService.AcceptInviteFromOrganization(vendorId))
                .message("Accepted invitation successfully")
                .timestamp(Instant.now())
                .build()
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my-invites")
    public ResponseEntity<FormResponse<List<VendorMemberResponse>>> getMyInvites() {
        return ResponseEntity.ok(
            FormResponse.<List<VendorMemberResponse>>builder()
                .data(vendorMemberService.loadMyInvites())
                .message("Loaded my invitations successfully")
                .timestamp(Instant.now())
                .build()
        );
    }

    @PreAuthorize("@ss.hasP('vendor:manage_members')")
    @GetMapping("/invited")
    public ResponseEntity<FormResponse<List<VendorMemberResponse>>> getInvitedMembers() {
        return ResponseEntity.ok(
            FormResponse.<List<VendorMemberResponse>>builder()
                .data(vendorMemberService.loadInvitedMembers())
                .message("Loaded invited members successfully")
                .timestamp(Instant.now())
                .build()
        );
    }

    @PreAuthorize("@ss.hasR('VENDOR_STAFF')")
    @GetMapping("/active")
    public ResponseEntity<FormResponse<List<VendorMemberResponse>>> getMyActiveMembers() {
        return ResponseEntity.ok(
            FormResponse.<List<VendorMemberResponse>>builder()
                .data(vendorMemberService.getMyMembers())
                .message("Loaded active members successfully")
                .timestamp(Instant.now())
                .build()
        );
    }
}