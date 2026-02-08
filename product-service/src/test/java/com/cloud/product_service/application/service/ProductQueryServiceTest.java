package com.cloud.product_service.application.service;

import com.cloud.product_service.application.dto.request.ProductSearchRequest;
import com.cloud.product_service.application.dto.response.ProductReportResponse;
import com.cloud.product_service.application.dto.response.ProductResponse;
import com.cloud.product_service.application.dto.response.StockSummaryDto;
import com.cloud.product_service.application.exception.custom.ProductNotFoundException;
import com.cloud.product_service.application.mapper.ProductMapper;
import com.cloud.product_service.common.utils.jwt.JwtUtils;
import com.cloud.product_service.domain.enums.ProductStatus;
import com.cloud.product_service.domain.model.Product;
import com.cloud.product_service.domain.model.ProductVariant;
import com.cloud.product_service.infrastructure.adapter.outbound.openfeign.client.InventoryClient;
import com.cloud.product_service.infrastructure.adapter.outbound.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductQueryServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private InventoryClient inventoryClient;

    @InjectMocks
    private ProductQueryService productQueryService;

    private UUID vendorId;
    private UUID productId;
    private UUID variantId1;
    private UUID variantId2;

    private Product mockProduct;
    private ProductVariant mockVariant1;
    private ProductVariant mockVariant2;
    private ProductResponse mockProductResponse;

    @BeforeEach
    void setUp() {
        vendorId = UUID.randomUUID();
        productId = UUID.randomUUID();
        variantId1 = UUID.randomUUID();
        variantId2 = UUID.randomUUID();

        when(jwtUtils.getCurrentUserId()).thenReturn(vendorId);

        mockVariant1 = ProductVariant.builder().id(variantId1).sku("SKU-001").build();
        mockVariant2 = ProductVariant.builder().id(variantId2).sku("SKU-002").build();

        mockProduct = Product.builder()
                .id(productId)
                .name("Ao thun nam")
                .vendorId(vendorId)
                .status(ProductStatus.ACTIVE)
                .variants(List.of(mockVariant1, mockVariant2))
                .build();

        mockProductResponse = ProductResponse.builder()
                .id(productId)
                .name("Ao thun nam")
                .build();
    }

    // ───────────────────────────────────────────────────────────────
    // searchVariantsByKeyword
    // ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("searchVariantsByKeyword → Tim thay ket qua")
    void searchVariantsByKeyword_Success() {
        List<Product> products = List.of(mockProduct);
        when(productRepository.searchProducts("ao thun", vendorId)).thenReturn(products);
        when(productMapper.toProductResponse(mockProduct)).thenReturn(mockProductResponse);

        List<ProductResponse> result = productQueryService.searchVariantsByKeyword("áo thun");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Ao thun nam");
        verify(productRepository).searchProducts("ao thun", vendorId);
    }

    @Test
    @DisplayName("searchVariantsByKeyword → That bai")
    void searchVariantsByKeyword_NoResult() {
        when(productRepository.searchProducts("khong co", vendorId)).thenReturn(Collections.emptyList());

        List<ProductResponse> result = productQueryService.searchVariantsByKeyword("không tồn tại");

        assertThat(result).isEmpty();
    }

    // ───────────────────────────────────────────────────────────────
    // getAllActiveProduct
    // ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllActiveProduct → thanh cong tar ve san pham")
    void getAllActiveProduct_Success() {
        Page<Product> page = new PageImpl<>(List.of(mockProduct));
        when(productRepository.findByStatus(eq(ProductStatus.ACTIVE), any(PageRequest.class)))
                .thenReturn(page);
        when(productMapper.toProductResponse(mockProduct)).thenReturn(mockProductResponse);

        Page<ProductResponse> result = productQueryService.getAllActiveProduct(0, 10);

        assertThat(result.getContent()).hasSize(1);
        verify(productRepository).findByStatus(eq(ProductStatus.ACTIVE), any(PageRequest.class));
    }

    // ───────────────────────────────────────────────────────────────
    // getAllProductsByVendorId
    // ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllProductsByVendorId → tra ve san pham active cua vendor")
    void getAllProductsByVendorId_Success() {
        UUID otherVendorId = UUID.randomUUID();
        Page<Product> page = new PageImpl<>(List.of(mockProduct));
        when(productRepository.findByVendorIdAndStatus(eq(otherVendorId), eq(ProductStatus.ACTIVE), any(PageRequest.class)))
                .thenReturn(page);
        when(productMapper.toProductResponse(mockProduct)).thenReturn(mockProductResponse);

        Page<ProductResponse> result = productQueryService.getAllProductsByVendorId(0, 10, otherVendorId);

        assertThat(result.getContent()).hasSize(1);
        verify(productRepository).findByVendorIdAndStatus(eq(otherVendorId), eq(ProductStatus.ACTIVE), any(PageRequest.class));
    }

    // ───────────────────────────────────────────────────────────────
    // getAllMyProduct
    // ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllMyProduct → tra ve tat ca san pham vendor hien tai")
    void getAllMyProduct_Success() {
        Page<Product> page = new PageImpl<>(List.of(mockProduct));
        when(productRepository.findByVendorId(eq(vendorId), any(PageRequest.class))).thenReturn(page);
        when(productMapper.toProductResponse(mockProduct)).thenReturn(mockProductResponse);

        Page<ProductResponse> result = productQueryService.getAllMyProduct(0, 20);

        assertThat(result.getContent()).hasSize(1);
        verify(productRepository).findByVendorId(eq(vendorId), any(PageRequest.class));
    }

    // ───────────────────────────────────────────────────────────────
    // getProductById (getProductDetailWithStock)
    // ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getProductById → thành công")
    void getProductById_Success_WithStock() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));

        Map<UUID, List<StockSummaryDto>> stockMap = new HashMap<>();
        stockMap.put(variantId1, List.of(new StockSummaryDto(UUID.randomUUID(), 10)));
        stockMap.put(variantId2, List.of(new StockSummaryDto(UUID.randomUUID(), 5)));

        when(inventoryClient.getStockByVariantIds(anyList())).thenReturn(stockMap);

        ProductReportResponse mapped = ProductReportResponse.builder().id(productId).build();
        when(productMapper.toProductReportResponse(mockProduct, stockMap)).thenReturn(mapped);

        ProductReportResponse result = productQueryService.getProductById(productId);

        assertThat(result.getId()).isEqualTo(productId);
        verify(inventoryClient).getStockByVariantIds(argThat(list -> list.size() == 2));
        verify(productMapper).toProductReportResponse(mockProduct, stockMap);
    }

    @Test
    @DisplayName("getProductById → that bai ")
    void getProductById_NotOwned_ThrowsException() {
        Product otherProduct = Product.builder()
                .id(productId)
                .vendorId(UUID.randomUUID())
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(otherProduct));

        assertThatThrownBy(() -> productQueryService.getProductById(productId))
                .isInstanceOf(ProductNotFoundException.class);

        verifyNoInteractions(inventoryClient);
    }

    // ───────────────────────────────────────────────────────────────
    // searchMyProduct & searchProduct
    // ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("searchMyProduct → gọi specification với vendorId")
    void searchMyProduct_CallsSpecificationWithVendorId() {
        ProductSearchRequest search = new ProductSearchRequest();
        search.setSortBy("name");
        search.setSortDirection(Sort.Direction.ASC);

        Page<Product> page = new PageImpl<>(List.of(mockProduct));
        when(productRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);
        when(productMapper.toProductResponse(mockProduct)).thenReturn(mockProductResponse);

        productQueryService.searchMyProduct(0, 10, search);

        verify(productRepository).findAll(any(Specification.class), any(PageRequest.class));
    }

    @Test
    @DisplayName("searchProduct → gọi specification không giới hạn vendor")
    void searchProduct_CallsSpecificationWithoutVendor() {
        ProductSearchRequest search = new ProductSearchRequest();

        Page<Product> page = new PageImpl<>(List.of(mockProduct));
        when(productRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        productQueryService.searchProduct(0, 10, search);

        verify(productRepository).findAll(any(Specification.class), any(PageRequest.class));
    }
}