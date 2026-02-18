package com.cloud.vendor_service.application.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private VendorProfileRepository vendorProfileRepository;

    @Mock
    private VendorRepository vendorRepository;

    @Mock
    private VendorMapper vendorMapper;

    @Mock
    private SecurityHelper securityHelper;

    @Mock
    private VendorAuditLogService vendorAuditLogService;

    @InjectMocks
    private ProfileService profileService;

    private UUID vendorId;
    private Vendor vendor;
    private VendorProfile vendorProfile;

    @BeforeEach
    void setUp() {
        vendorId = UUID.randomUUID();

        vendor = Vendor.builder()
                .id(vendorId)
                .name("Old Name")
                .slug("old-slug")
                .logoUrl("old-logo.jpg")
                .description("Old desc")
                .build();

        vendorProfile = VendorProfile.builder()
                .vendor(vendor)
                .phone("0123456789")
                .taxCode("123456789")
                .email("old@example.com")
                .address("Old Address")
                .websiteUrl("https://old.com")
                .build();

        when(securityHelper.currentVendorId()).thenReturn(vendorId);
    }

    @Nested
    @DisplayName("updateBasicInfo")
    class UpdateBasicInfoTests {

        @Test
        @DisplayName("Thành công - update basic info và ghi audit log")
        void shouldUpdateBasicInfoAndAuditLogSuccessfully() {
            UpdateBasicInfoVendorRequest request = UpdateBasicInfoVendorRequest.builder()
                    .name("New Name")
                    .slug("new-slug")
                    .logoUrl("new-logo.jpg")
                    .description("New description")
                    .build();

            Vendor updatedVendor = Vendor.builder()
                    .id(vendorId)
                    .name("New Name")
                    .slug("new-slug")
                    .logoUrl("new-logo.jpg")
                    .description("New description")
                    .build();

            VendorResponse response = VendorResponse.builder()
                    .id(vendorId.toString())
                    .name("New Name")
                    .build();

            when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));
            when(vendorRepository.save(any(Vendor.class))).thenReturn(updatedVendor);
            when(vendorMapper.toResponse(updatedVendor)).thenReturn(response);

            VendorResponse result = profileService.updateBasicInfo(request);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("New Name");

            verify(vendorMapper).updateBasicInfoVendorFromDto(request, vendor);

            ArgumentCaptor<Vendor> vendorCaptor = ArgumentCaptor.forClass(Vendor.class);
            verify(vendorRepository).save(vendorCaptor.capture());
            Vendor savedVendor = vendorCaptor.getValue();
            assertThat(savedVendor.getName()).isEqualTo("New Name");

            verify(vendorAuditLogService).saveVendorAuditLog(
                    eq(vendorId),
                    eq("UPDATE_BASIC_INFO"),
                    eq(vendor),
                    eq(updatedVendor),
                    eq("Vendor basic information updated (name, slug, logo, description, etc.)")
            );
        }

        @Test
        @DisplayName("Ném exception khi vendor không tồn tại")
        void shouldThrowExceptionWhenVendorNotFound() {
            UpdateBasicInfoVendorRequest request = UpdateBasicInfoVendorRequest.builder().build();

            when(vendorRepository.findById(vendorId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> profileService.updateBasicInfo(request))
                    .isInstanceOf(VendorNotFoundException.class)
                    .hasMessageContaining(vendorId.toString());

            verifyNoInteractions(vendorMapper, vendorAuditLogService);
            verify(vendorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateProfile")
    class UpdateProfileTests {

        @Test
        @DisplayName("Thành công - update profile và ghi audit log")
        void shouldUpdateProfileAndAuditLogSuccessfully() {
            UpdateProfileVendorRequest request = UpdateProfileVendorRequest.builder()
                    .phone("0987654321")
                    .taxCode("987654321")
                    .address("New Address 123")
                    .websiteUrl("https://newwebsite.com")
                    .build();

            VendorProfile updatedProfile = VendorProfile.builder()
                    .vendor(vendor)
                    .phone("0987654321")
                    .taxCode("987654321")
                    .address("New Address 123")
                    .websiteUrl("https://newwebsite.com")
                    .email(vendorProfile.getEmail())  // giữ nguyên nếu không update
                    .build();

            VendorResponse response = VendorResponse.builder()
                    .id(vendorId.toString())
                    .name(vendor.getName())
                    .build();

            when(vendorProfileRepository.findByVendorId(vendorId)).thenReturn(Optional.of(vendorProfile));
            when(vendorProfileRepository.save(any(VendorProfile.class))).thenReturn(updatedProfile);
            when(vendorMapper.toResponse(vendor)).thenReturn(response);

            VendorResponse result = profileService.updateProfile(request);

            assertThat(result).isNotNull();

            verify(vendorMapper).updateVendorProfileFromDto(request, vendorProfile);

            ArgumentCaptor<VendorProfile> profileCaptor = ArgumentCaptor.forClass(VendorProfile.class);
            verify(vendorProfileRepository).save(profileCaptor.capture());
            VendorProfile savedProfile = profileCaptor.getValue();

            // Verify field đã update
            assertThat(savedProfile.getPhone()).isEqualTo("0987654321");
            assertThat(savedProfile.getTaxCode()).isEqualTo("987654321");
            assertThat(savedProfile.getAddress()).isEqualTo("New Address 123");

            verify(vendorAuditLogService).saveVendorAuditLog(
                    eq(vendorId),
                    eq("UPDATE_VENDOR_PROFILE"),
                    eq(vendorProfile),
                    eq(updatedProfile),
                    eq("Vendor profile updated (address, phone, tax code, website, etc.)")
            );
        }

        @Test
        @DisplayName("Ném exception khi profile không tồn tại")
        void shouldThrowExceptionWhenProfileNotFound() {
            UpdateProfileVendorRequest request = UpdateProfileVendorRequest.builder().build();

            when(vendorProfileRepository.findByVendorId(vendorId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> profileService.updateProfile(request))
                    .isInstanceOf(VendorNotFoundException.class)
                    .hasMessageContaining(vendorId.toString());

            verifyNoInteractions(vendorMapper, vendorAuditLogService);
            verify(vendorProfileRepository, never()).save(any());
        }
    }
}