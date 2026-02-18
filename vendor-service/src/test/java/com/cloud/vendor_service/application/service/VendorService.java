package com.cloud.vendor_service.application.service;

import com.cloud.vendor_service.application.dto.request.CreateRequest;
import com.cloud.vendor_service.application.dto.response.VendorResponse;
import com.cloud.vendor_service.application.exception.custom.InvalidVendorStatusTransitionException;
import com.cloud.vendor_service.application.exception.custom.SlugAlreadyExistsException;
import com.cloud.vendor_service.application.exception.custom.VendorNotFoundException;
import com.cloud.vendor_service.application.mapper.VendorMapper;
import com.cloud.vendor_service.common.utils.jwt.SecurityHelper;
import com.cloud.vendor_service.domain.enums.VendorStatus;
import com.cloud.vendor_service.domain.model.Vendor;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorServiceTest {

    @Mock private VendorRepository vendorRepository;
    @Mock private VendorMapper vendorMapper;
    @Mock private VendorAuditLogService vendorAuditLogService;
    @Mock private SecurityHelper securityHelper;

    @InjectMocks private VendorService vendorService;

    private UUID vendorId;
    private UUID currentUserId;

    @BeforeEach
    void setUp() {
        vendorId = UUID.randomUUID();
        currentUserId = UUID.randomUUID();

        when(securityHelper.currentVendorId()).thenReturn(vendorId);
        when(securityHelper.currentUserEmail()).thenReturn("test@example.com");
    }

    @Nested
    @DisplayName("registerVendor")
    class RegisterVendorTests {

        @Test
        @DisplayName("Nên đăng ký vendor thành công, tạo profile và ghi audit")
        void shouldRegisterVendorSuccessfully() {
            // given
            CreateRequest request = CreateRequest.builder()
                    .slug("new-vendor-slug")
                    .name("New Vendor")
                    .logoUrl("logo.jpg")
                    .description("Desc")
                    .build();

            Vendor newVendor = Vendor.builder()
                    .id(vendorId)
                    .slug("new-vendor-slug")
                    .name("New Vendor")
                    .status(VendorStatus.PENDING)
                    .build();

            Vendor savedVendor = Vendor.builder()
                    .id(vendorId)
                    .slug("new-vendor-slug")
                    .name("New Vendor")
                    .build();

            VendorResponse response = VendorResponse.builder()
                    .id(vendorId.toString())
                    .name("New Vendor")
                    .build();

            when(vendorRepository.findBySlug("new-vendor-slug")).thenReturn(Optional.empty());
            when(vendorMapper.toVendorEntity(request)).thenReturn(newVendor);
            when(vendorRepository.save(any(Vendor.class))).thenReturn(savedVendor);
            when(vendorMapper.toResponse(savedVendor)).thenReturn(response);

            // when
            VendorResponse result = vendorService.registerVendor(request);

            // then
            assertThat(result.getName()).isEqualTo("New Vendor");

            verify(vendorRepository).findBySlug("new-vendor-slug");
            verify(vendorRepository).save(any(Vendor.class));

            verify(vendorAuditLogService).saveVendorAuditLog(
                    eq(vendorId),
                    eq("REGISTER_VENDOR"),
                    isNull(),
                    eq(savedVendor),
                    eq("New vendor registered.")
            );

            verifyNoMoreInteractions(vendorAuditLogService);
        }

        @Test
        @DisplayName("Nên ném SlugAlreadyExistsException khi slug đã tồn tại")
        void shouldThrowWhenSlugAlreadyExists() {
            CreateRequest request = CreateRequest.builder()
                    .slug("existing-slug")
                    .build();

            when(vendorRepository.findBySlug("existing-slug")).thenReturn(Optional.of(new Vendor()));

            assertThatThrownBy(() -> vendorService.registerVendor(request))
                    .isInstanceOf(SlugAlreadyExistsException.class)
                    .hasMessageContaining("existing-slug");

            verifyNoInteractions(vendorAuditLogService, vendorRepository.save(any()));
        }
    }

    @Nested
    @DisplayName("updateStatus")
    class UpdateStatusTests {

        @Test
        @DisplayName("Nên update status từ PENDING → ACTIVE và ghi audit")
        void shouldUpdateStatusFromPendingToActiveSuccessfully() {
            Vendor vendor = Vendor.builder()
                    .id(vendorId)
                    .status(VendorStatus.PENDING)
                    .build();

            Vendor updatedVendor = Vendor.builder()
                    .id(vendorId)
                    .status(VendorStatus.ACTIVE)
                    .build();

            VendorResponse response = VendorResponse.builder()
                    .id(vendorId.toString())
                    .status(VendorStatus.ACTIVE)
                    .build();

            when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));
            when(vendorRepository.save(vendor)).thenReturn(updatedVendor);
            when(vendorMapper.toResponse(updatedVendor)).thenReturn(response);

            VendorResponse result = vendorService.updateStatus(vendorId, VendorStatus.ACTIVE);

            assertThat(result.getStatus()).isEqualTo("ACTIVE");

            ArgumentCaptor<Vendor> saveCaptor = ArgumentCaptor.forClass(Vendor.class);
            verify(vendorRepository).save(saveCaptor.capture());
            assertThat(saveCaptor.getValue().getStatus()).isEqualTo(VendorStatus.ACTIVE);

            verify(vendorAuditLogService).saveVendorAuditLog(
                    eq(vendorId),
                    eq("UPDATE_VENDOR_STATUS"),
                    eq(VendorStatus.PENDING),
                    eq(VendorStatus.ACTIVE),
                    eq("Vendor status updated.")
            );
        }

        @Test
        @DisplayName("Nên ném InvalidVendorStatusTransitionException khi transition không hợp lệ")
        void shouldThrowInvalidTransition() {
            Vendor vendor = Vendor.builder()
                    .id(vendorId)
                    .status(VendorStatus.ACTIVE)
                    .build();

            when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));

            assertThatThrownBy(() -> vendorService.updateStatus(vendorId, VendorStatus.PENDING))
                    .isInstanceOf(InvalidVendorStatusTransitionException.class)
                    .hasMessageContaining("from ACTIVE to PENDING");

            verifyNoInteractions(vendorAuditLogService);
        }

        @Test
        @DisplayName("Nên ném VendorNotFoundException khi vendor không tồn tại")
        void shouldThrowWhenVendorNotFound() {
            when(vendorRepository.findById(vendorId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendorService.updateStatus(vendorId, VendorStatus.ACTIVE))
                    .isInstanceOf(VendorNotFoundException.class)
                    .hasMessageContaining(vendorId.toString());

            verifyNoInteractions(vendorAuditLogService);
        }
    }

    @Nested
    @DisplayName("hasVendor & findByVendorEntityById")
    class BasicQueryTests {

        @Test
        @DisplayName("Nên trả về true nếu vendor tồn tại")
        void shouldReturnTrueWhenVendorExists() {
            when(vendorRepository.existsById(vendorId)).thenReturn(true);

            boolean result = vendorService.hasVendor();

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Nên trả về false nếu vendor không tồn tại")
        void shouldReturnFalseWhenVendorNotExists() {
            when(vendorRepository.existsById(vendorId)).thenReturn(false);

            boolean result = vendorService.hasVendor();

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("findByVendorEntityById nên trả về vendor khi tồn tại")
        void shouldFindVendorByIdSuccessfully() {
            Vendor vendor = Vendor.builder().id(vendorId).build();

            when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));

            Vendor result = vendorService.findByVendorEntityById(vendorId);

            assertThat(result.getId()).isEqualTo(vendorId);
        }

        @Test
        @DisplayName("findByVendorEntityById nên ném exception khi không tìm thấy")
        void shouldThrowWhenVendorNotFoundInFindById() {
            when(vendorRepository.findById(vendorId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendorService.findByVendorEntityById(vendorId))
                    .isInstanceOf(VendorNotFoundException.class)
                    .hasMessageContaining(vendorId.toString());
        }
    }
}