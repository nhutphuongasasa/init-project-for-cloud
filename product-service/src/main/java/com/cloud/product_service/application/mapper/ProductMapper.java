package com.cloud.product_service.application.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud.product_service.application.dto.request.ProductImageRequest;
import com.cloud.product_service.application.dto.request.ProductCreateRequest;
import com.cloud.product_service.application.dto.request.ProductVariantRequest;
import com.cloud.product_service.application.dto.request.updateBasicInfoProduct;
import com.cloud.product_service.application.dto.response.ProductImageResponse;
import com.cloud.product_service.application.dto.response.ProductReportResponse;
import com.cloud.product_service.application.dto.response.ProductResponse;
import com.cloud.product_service.application.dto.response.ProductVariantResponse;
import com.cloud.product_service.application.dto.response.StockSummaryDto;
import com.cloud.product_service.application.dto.response.VariantInventoryResponse;
import com.cloud.product_service.domain.model.Product;
import com.cloud.product_service.domain.model.ProductImage;
import com.cloud.product_service.domain.model.ProductVariant;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 26/11/2025
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {


    @Mapping(target = "variants", source = "variants", qualifiedByName = "variantListWithStock")
    ProductReportResponse toProductReportResponse(
        Product product,
        @Context Map<UUID, List<StockSummaryDto>> stockMap
    );

    @Named("variantWithStock")
    @Mapping(target = "inventories", ignore = true)
    VariantInventoryResponse toVariantInventoryResponse(
        ProductVariant variant,
        @Context Map<UUID, List<StockSummaryDto>> stockMap
    );

    @Named("variantListWithStock")
    default List<VariantInventoryResponse> toVariantInventoryResponseList(
            List<ProductVariant> variants,
            @Context Map<UUID, List<StockSummaryDto>> stockMap) {

        if (variants == null || variants.isEmpty()) {
            return new ArrayList<>();
        }

        List<VariantInventoryResponse> result = new ArrayList<>(variants.size());
        for (ProductVariant variant : variants) {
            VariantInventoryResponse response = toVariantInventoryResponse(variant, stockMap);
            enrichVariantWithInventory(response, variant, stockMap); // CHẮC CHẮN ĐƯỢC GỌI
            result.add(response);
        }
        return result;
    }

    // Method enrich giữ nguyên - sẽ được gọi từ trên
    @AfterMapping
    default void enrichVariantWithInventory(
            @MappingTarget VariantInventoryResponse response,
            ProductVariant source,
            @Context Map<UUID, List<StockSummaryDto>> stockMap) {

        System.out.println("=== DEBUG AfterMapping ===");
        System.out.println("Variant ID: " + source.getId());
        System.out.println("stockMap == null ? " + (stockMap == null));
        if (stockMap != null) {
            System.out.println("stockMap size: " + stockMap.size());
            System.out.println("Has key? " + stockMap.containsKey(source.getId()));
            if (stockMap.containsKey(source.getId())) {
                System.out.println("Value: " + stockMap.get(source.getId()));
            }
        }

        List<StockSummaryDto> inventories = new ArrayList<>();
        if (stockMap != null && stockMap.containsKey(source.getId())) {
            inventories = stockMap.get(source.getId());
        }
        response.setInventories(inventories);

        System.out.println("Set inventories to: " + inventories);
    }

    @Mapping(target = "vendorId", source = "vendorId")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "images", ignore = true)
    ProductVariant toProductVariant(ProductVariantRequest request, UUID vendorId, Product product);

    default List<ProductVariant> toProductVariants(List<ProductVariantRequest> requests, UUID vendorId, Product product) {
        if (requests == null) {
            return new ArrayList<>();
        }
        return IntStream.range(0, requests.size())
                .mapToObj(i -> {
                    ProductVariant variant = toProductVariant(requests.get(i), vendorId, product);
                    return variant;
                })
                .toList();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "images", ignore = true)
    void updateProductVariant(@MappingTarget ProductVariant variant, ProductVariantRequest request);
    
    // @Mapping(target = "product", ignore = true)
    @Mapping(target = "id", ignore = true)
    ProductImage toProductImage(ProductImageRequest request);
    
    // @IterableMapping(elementTargetType = ProductImage.class)
    List<ProductImage> toProductImages(List<ProductImageRequest> requests);
    
    @Mapping(target = "vendorId", source = "vendorId")
    @Mapping(target = "categoryId", source = "categoryId")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "variants", ignore = true)
    Product toProduct(ProductCreateRequest productRequest, UUID vendorId, UUID categoryId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "slug", ignore = true)
    Product duplicateProduct(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sku", ignore = true)
    ProductVariant duplicateProductVariant(ProductVariant productVariant);

    @Mapping(target = "id", ignore = true)
    // @Mapping(target = "product", ignore = true)
    ProductImage duplicateProductImage(ProductImage productImage);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Product updateBaseInfoProduct(updateBasicInfoProduct productRequest, @MappingTarget Product product);
  
    ProductImageResponse toProductImageResponse(ProductImage productImage, UUID productId);
    ProductVariantResponse toProductVariantResponse(ProductVariant productVariant);
    
    ProductResponse toProductResponse(Product product);
}