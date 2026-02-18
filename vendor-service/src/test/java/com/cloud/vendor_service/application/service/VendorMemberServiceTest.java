package com.cloud.vendor_service.application.service;

import com.cloud.vendor_service.application.dto.response.UserResponse;
import com.cloud.vendor_service.application.dto.response.VendorMemberResponse;
import com.cloud.vendor_service.application.exception.custom.UserNotFoundException;
import com.cloud.vendor_service.application.mapper.VendorMemberMapper;
import com.cloud.vendor_service.common.utils.jwt.SecurityHelper;
import com.cloud.vendor_service.domain.enums.VendorMemberStatus;
import com.cloud.vendor_service.domain.model.VendorMember;
import com.cloud.vendor_service.infrastructure.adapter.outbound.openfeign.client.AuthClient;
import com.cloud.vendor_service.infrastructure.adapter.outbound.repository.VendorMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorMemberServiceTest {

    @Mock private AuthClient authClient;
    @Mock private VendorMemberMapper vendorMemberMapper;
    @Mock private VendorMemberRepository vendorMemberRepository;
    @Mock private VendorAuditLogService vendorAuditLogService;
    @Mock private SecurityHelper securityHelper;

    @InjectMocks private VendorMemberService vendorMemberService;

    private UUID vendorId;
    private UUID userId;
    private String email;

    @BeforeEach
    void setUp() {
        vendorId = UUID.randomUUID();
        userId = UUID.randomUUID();
        email = "test@example.com";

        when(securityHelper.currentVendorId()).thenReturn(vendorId);
        when(securityHelper.currentUserId()).thenReturn(userId);
    }

    @Nested
    @DisplayName("inviteMember")
    class InviteMemberTests {

        @Test
        @DisplayName("Nên mời thành công khi user tồn tại và chưa bị mời/active")
        void shouldInviteMemberSuccessfully() {
            // given
            UserResponse userResponse = UserResponse.builder()
                    .userId(userId)
                    .email(email)
                    .build();

            VendorMember member = VendorMember.builder()
                    .vendorId(vendorId)
                    .userId(userId)
                    .email(email)
                    .status(VendorMemberStatus.INVITED)
                    .build();

            VendorMemberResponse response = VendorMemberResponse.builder()
                    .email(email)
                    .status(VendorMemberStatus.INVITED.name())
                    .build();

            when(authClient.getUserByEmail(email)).thenReturn(userResponse);
            when(vendorMemberRepository.existsByUserIdAndStatus(userId, VendorMemberStatus.ACTIVE)).thenReturn(false);
            when(vendorMemberRepository.existsByVendorIdAndUserIdAndStatus(vendorId, userId, VendorMemberStatus.INVITED)).thenReturn(false);
            when(vendorMemberMapper.toEntity(vendorId, userId, email)).thenReturn(member);
            when(vendorMemberRepository.save(any(VendorMember.class))).thenReturn(member);
            when(vendorMemberMapper.toInvitedVendorMemberResponse(member)).thenReturn(response);

            // when
            VendorMemberResponse result = vendorMemberService.inviteMember(email);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(email);

            verify(authClient).getUserByEmail(email);
            verify(vendorMemberRepository).existsByUserIdAndStatus(userId, VendorMemberStatus.ACTIVE);
            verify(vendorMemberRepository).existsByVendorIdAndUserIdAndStatus(vendorId, userId, VendorMemberStatus.INVITED);
            verify(vendorMemberRepository).save(member);

            ArgumentCaptor<VendorMember> auditCaptor = ArgumentCaptor.forClass(VendorMember.class);
            verify(vendorAuditLogService).saveVendorAuditLog(
                    eq(vendorId),
                    eq("INVITE_MEMBER"),
                    isNull(),
                    auditCaptor.capture(),
                    eq("User invited to organization.")
            );
            assertThat(auditCaptor.getValue().getEmail()).isEqualTo(email);

            verifyNoMoreInteractions(vendorMemberRepository, vendorAuditLogService);
        }

        @Test
        @DisplayName("Nên ném UserNotFoundException khi user không tồn tại")
        void shouldThrowUserNotFoundWhenUserDoesNotExist() {
            when(authClient.getUserByEmail(email)).thenReturn(null);

            assertThatThrownBy(() -> vendorMemberService.inviteMember(email))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining(email);

            verifyNoInteractions(vendorMemberRepository, vendorAuditLogService);
        }

        @Test
        @DisplayName("Nên ném exception khi user đã active ở vendor khác")
        void shouldThrowWhenUserAlreadyActiveInAnotherVendor() {
            when(authClient.getUserByEmail(email)).thenReturn(UserResponse.builder().userId(userId).email(email).build());
            when(vendorMemberRepository.existsByUserIdAndStatus(userId, VendorMemberStatus.ACTIVE)).thenReturn(true);

            assertThatThrownBy(() -> vendorMemberService.inviteMember(email))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("User is already an active member of another vendor.");

            verifyNoInteractions(vendorAuditLogService);
        }

        @Test
        @DisplayName("Nên ném exception khi user đã được mời ở vendor này")
        void shouldThrowWhenUserAlreadyInvitedInThisVendor() {
            when(authClient.getUserByEmail(email)).thenReturn(UserResponse.builder().userId(userId).email(email).build());
            when(vendorMemberRepository.existsByUserIdAndStatus(userId, VendorMemberStatus.ACTIVE)).thenReturn(false);
            when(vendorMemberRepository.existsByVendorIdAndUserIdAndStatus(vendorId, userId, VendorMemberStatus.INVITED)).thenReturn(true);

            assertThatThrownBy(() -> vendorMemberService.inviteMember(email))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("This user has already been invited or is already in this vendor.");

            verifyNoInteractions(vendorAuditLogService);
        }
    }

    @Nested
    @DisplayName("AcceptInviteFromOrganization")
    class AcceptInviteTests {

        @Test
        @DisplayName("Nên chấp nhận invite thành công và cập nhật status + audit")
        void shouldAcceptInviteSuccessfully() {
            VendorMember member = VendorMember.builder()
                    .vendorId(vendorId)
                    .userId(userId)
                    .status(VendorMemberStatus.INVITED)
                    .build();

            VendorMember updatedMember = VendorMember.builder()
                    .vendorId(vendorId)
                    .userId(userId)
                    .status(VendorMemberStatus.ACTIVE)
                    .joinedAt(Instant.now())
                    .build();

            VendorMemberResponse response = VendorMemberResponse.builder()
                    .status(VendorMemberStatus.ACTIVE.name())
                    .build();

            when(vendorMemberRepository.findByVendorIdAndUserIdAndStatus(vendorId, userId, VendorMemberStatus.INVITED))
                    .thenReturn(Optional.of(member));
            when(vendorMemberRepository.save(any(VendorMember.class))).thenReturn(updatedMember);
            when(vendorMemberMapper.toInvitedVendorMemberResponse(updatedMember)).thenReturn(response);

            VendorMemberResponse result = vendorMemberService.AcceptInviteFromOrganization(vendorId);

            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo("ACTIVE");

            ArgumentCaptor<VendorMember> saveCaptor = ArgumentCaptor.forClass(VendorMember.class);
            verify(vendorMemberRepository).save(saveCaptor.capture());
            assertThat(saveCaptor.getValue().getStatus()).isEqualTo(VendorMemberStatus.ACTIVE);

            verify(vendorAuditLogService).saveVendorAuditLog(
                    eq(vendorId),
                    eq("ACCEPT_INVITE"),
                    eq(VendorMemberStatus.INVITED),
                    eq(VendorMemberStatus.ACTIVE),
                    eq("User accepted invitation from organization.")
            );
        }

        @Test
        @DisplayName("Nên ném exception khi không tìm thấy invite")
        void shouldThrowWhenNoInvitationFound() {
            when(vendorMemberRepository.findByVendorIdAndUserIdAndStatus(vendorId, userId, VendorMemberStatus.INVITED))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendorMemberService.AcceptInviteFromOrganization(vendorId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("No invitation found for this user from the specified vendor.");

            verifyNoInteractions(vendorAuditLogService);
        }
    }

    @Nested
    @DisplayName("Load invites/members")
    class LoadTests {

        @Test
        @DisplayName("Nên load danh sách invites của user hiện tại")
        void shouldLoadMyInvites() {
            List<VendorMember> invites = List.of(
                    VendorMember.builder().vendorId(UUID.randomUUID()).status(VendorMemberStatus.INVITED).build()
            );

            List<VendorMemberResponse> responses = List.of(VendorMemberResponse.builder().build());

            when(securityHelper.currentUserId()).thenReturn(userId);
            when(vendorMemberRepository.findByUserIdAndStatus(userId, VendorMemberStatus.INVITED)).thenReturn(invites);
            when(vendorMemberMapper.toVendorMemberResponses(invites)).thenReturn(responses);

            List<VendorMemberResponse> result = vendorMemberService.loadMyInvites();

            assertThat(result).hasSize(1);
            verify(vendorMemberRepository).findByUserIdAndStatus(userId, VendorMemberStatus.INVITED);
        }

        @Test
        @DisplayName("Nên load danh sách invited members của vendor hiện tại")
        void shouldLoadInvitedMembers() {
            List<VendorMember> members = List.of(VendorMember.builder().build());
            List<VendorMemberResponse> responses = List.of(VendorMemberResponse.builder().build());

            when(vendorMemberRepository.findByVendorIdAndStatus(vendorId, VendorMemberStatus.INVITED)).thenReturn(members);
            when(vendorMemberMapper.toVendorMemberResponses(members)).thenReturn(responses);

            List<VendorMemberResponse> result = vendorMemberService.loadInvitedMembers();

            assertThat(result).hasSize(1);
            verify(vendorMemberRepository).findByVendorIdAndStatus(vendorId, VendorMemberStatus.INVITED);
        }

        @Test
        @DisplayName("Nên load danh sách active members của vendor hiện tại")
        void shouldGetMyMembers() {
            List<VendorMember> members = List.of(VendorMember.builder().build());
            List<VendorMemberResponse> responses = List.of(VendorMemberResponse.builder().build());

            when(vendorMemberRepository.findByVendorIdAndStatus(vendorId, VendorMemberStatus.ACTIVE)).thenReturn(members);
            when(vendorMemberMapper.toVendorMemberResponses(members)).thenReturn(responses);

            List<VendorMemberResponse> result = vendorMemberService.getMyMembers();

            assertThat(result).hasSize(1);
            verify(vendorMemberRepository).findByVendorIdAndStatus(vendorId, VendorMemberStatus.ACTIVE);
        }
    }
}