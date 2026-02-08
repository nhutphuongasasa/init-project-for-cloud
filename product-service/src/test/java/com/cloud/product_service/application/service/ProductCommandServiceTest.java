package com.cloud.product_service.application.service;

import com.cloud.product_service.application.dto.request.ProductCreateRequest;
import com.cloud.product_service.application.dto.request.ProductVariantRequest;
import com.cloud.product_service.application.dto.request.updateBasicInfoProduct;
import com.cloud.product_service.application.dto.response.ProductResponse;
import com.cloud.product_service.application.dto.response.ProductVariantResponse;
import com.cloud.product_service.application.exception.custom.ProductNotFoundException;
import com.cloud.product_service.application.exception.custom.ProductVariantNotFoundException;
import com.cloud.product_service.application.exception.custom.SlugAlreadyExistsException;
import com.cloud.product_service.application.mapper.ProductMapper;
import com.cloud.product_service.common.utils.jwt.JwtUtils;
import com.cloud.product_service.domain.enums.ProductStatus;
import com.cloud.product_service.domain.model.Category;
import com.cloud.product_service.domain.model.Product;
import com.cloud.product_service.domain.model.ProductVariant;
import com.cloud.product_service.infrastructure.adapter.outbound.repository.ProductRepository;
import com.cloud.product_service.infrastructure.adapter.outbound.repository.ProductVariantRepository;
import com.cloud.product_service.application.helper.SkuHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCommandServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private SkuHelper skuHelper;

    @Mock
    private CategoryService categoryService;

    // ExcelUtils không dùng trong các method public chính → không cần mock

    @InjectMocks
    private ProductCommandService productCommandService;

    private UUID vendorId = UUID.randomUUID();
    private UUID productId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        when(jwtUtils.getCurrentUserId()).thenReturn(vendorId);
    }

    // ───────────────────────────────────────────────
    // 1. checkAndCreateProduct
    // ───────────────────────────────────────────────

    @Test
    void checkAndCreateProduct_shouldThrowSlugAlreadyExistsException_whenSlugExists() {
        // Arrange
        ProductCreateRequest request = new ProductCreateRequest();
        request.setSlug("existing-slug");

        Product existing = new Product();
        existing.setId(UUID.randomUUID());

        when(productRepository.findBySlug("existing-slug")).thenReturn(Optional.of(existing));

        // Act & Assert
        assertThatThrownBy(() -> productCommandService.checkAndCreateProduct(request))
                .isInstanceOf(SlugAlreadyExistsException.class)
                .hasMessageContaining("existing-slug");

        verify(productRepository).findBySlug("existing-slug");
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void checkAndCreateProduct_shouldCreateSuccessfully_whenValid() {
        // Arrange
        ProductCreateRequest request = new ProductCreateRequest();
        request.setSlug("new-product");
        request.setName("New Product");
        request.setCategoryId(UUID.randomUUID());

        Product mappedProduct = Product.builder().name("New Product").slug("new-product").build();
        when(productMapper.toProduct(eq(request), eq(vendorId), any())).thenReturn(mappedProduct);

        Product savedProduct = Product.builder()
                .id(productId)
                .name("New Product")
                .slug("new-product")
                .status(ProductStatus.ACTIVE)
                .build();

        when(productRepository.findBySlug("new-product")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponse response = new ProductResponse();
        when(productMapper.toProductResponse(savedProduct)).thenReturn(response);

        // Act
        ProductResponse result = productCommandService.checkAndCreateProduct(request);

        // Assert
        assertThat(result).isSameAs(response);
        verify(productRepository).findBySlug("new-product");
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toProductResponse(savedProduct);
    }

    // ───────────────────────────────────────────────
    // 2. checkAndUpdateProduct
    // ───────────────────────────────────────────────

    @Test
    void checkAndUpdateProduct_shouldThrowNotFound_whenProductNotBelongToVendor() {
        updateBasicInfoProduct request = new updateBasicInfoProduct();

        when(productRepository.findByIdAndVendorId(productId, vendorId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productCommandService.checkAndUpdateProduct(productId, request))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void checkAndUpdateProduct_shouldUpdateSuccessfully() {
        updateBasicInfoProduct request = new updateBasicInfoProduct();

        Product product = new Product();
        product.setId(productId);

        when(productRepository.findByIdAndVendorId(productId, vendorId))
                .thenReturn(Optional.of(product));

        when(productMapper.updateBaseInfoProduct(request, product)).thenReturn(product); // giả sử mapper modify trực tiếp

        ProductResponse response = new ProductResponse();
        when(productMapper.toProductResponse(product)).thenReturn(response);

        // Act
        ProductResponse result = productCommandService.checkAndUpdateProduct(productId, request);

        // Assert
        assertThat(result).isSameAs(response);
        verify(productRepository).save(product);
    }

    // ───────────────────────────────────────────────
    // 3. deleteProduct (soft delete)
    // ───────────────────────────────────────────────

    @Test
    void deleteProduct_shouldThrowNotFound_whenNotFound() {
        when(productRepository.findByIdAndVendorId(productId, vendorId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productCommandService.deleteProduct(productId))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void deleteProduct_shouldSetInactiveSuccessfully() {
        Product product = new Product();
        product.setId(productId);

        when(productRepository.findByIdAndVendorId(productId, vendorId))
                .thenReturn(Optional.of(product));

        boolean result = productCommandService.deleteProduct(productId);

        assertThat(result).isTrue();
        assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
        verify(productRepository).save(product);
    }

    // ───────────────────────────────────────────────
    // 4. publishProduct / unPublishProduct (tương tự delete)
    // ───────────────────────────────────────────────

    @Test
    void publishProduct_shouldSetActive() {
        Product product = new Product();
        product.setId(productId);

        when(productRepository.findByIdAndVendorId(productId, vendorId))
                .thenReturn(Optional.of(product));

        productCommandService.publishProduct(productId);

        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
        verify(productRepository).save(product);
    }

    // Tương tự cho unPublishProduct → set DRAFT

    // ───────────────────────────────────────────────
    // 5. addProductVariant
    // ───────────────────────────────────────────────

    @Test
    void addProductVariant_shouldThrowNotFound_whenProductNotFound() {
        ProductVariantRequest request = new ProductVariantRequest();

        when(productRepository.findByIdAndVendorId(productId, vendorId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productCommandService.addProductVariant(productId, request))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // Bạn có thể thêm test happy path nếu muốn, nhưng logic buildVariant khá phức tạp → có thể test integration sau

    // ───────────────────────────────────────────────
    // 6. updateProductVariantBaseInfo
    // ───────────────────────────────────────────────

    @Test
    void updateProductVariantBaseInfo_shouldUpdateSuccessfully() {
        UUID variantId = UUID.randomUUID();
        ProductVariantRequest request = new ProductVariantRequest();

        ProductVariant variant = new ProductVariant();

        when(productVariantRepository.findById(variantId)).thenReturn(Optional.of(variant));

        ProductVariantResponse response = new ProductVariantResponse();
        when(productMapper.toProductVariantResponse(variant)).thenReturn(response);

        ProductVariantResponse result = productCommandService.updateProductVariantBaseInfo(variantId, request);

        assertThat(result).isSameAs(response);
        verify(productVariantRepository).save(variant);
    }

    // ───────────────────────────────────────────────
    // 7. removeProductVariant
    // ───────────────────────────────────────────────

    @Test
    void removeProductVariant_shouldRemoveAndDelete() {
        Product product = new Product();
        product.setId(productId);

        ProductVariant variant = new ProductVariant();
        UUID variantId = UUID.randomUUID();

        when(productRepository.findByIdAndVendorId(productId, vendorId))
                .thenReturn(Optional.of(product));

        when(productVariantRepository.findById(variantId)).thenReturn(Optional.of(variant));

        productCommandService.removeProductVariant(productId, variantId);

        verify(productVariantRepository).delete(variant);
        verify(productRepository).save(product); // nếu removeVariant có save
    }
}