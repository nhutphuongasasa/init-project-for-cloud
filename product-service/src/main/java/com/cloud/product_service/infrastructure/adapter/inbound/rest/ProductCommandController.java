package com.cloud.product_service.infrastructure.adapter.inbound.rest;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.cloud.product_service.application.dto.request.ProductCreateRequest;
import com.cloud.product_service.application.dto.request.ProductVariantRequest;
import com.cloud.product_service.application.dto.request.updateBasicInfoProduct;
import com.cloud.product_service.application.dto.response.ProductResponse;
import com.cloud.product_service.application.dto.response.ProductVariantResponse;
import com.cloud.product_service.application.service.ProductCommandService;
import com.cloud.product_service.common.response.FormResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/products-command")
@RequiredArgsConstructor
public class ProductCommandController {

    private final ProductCommandService productCommandService;

    @PostMapping
    public ResponseEntity<FormResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {

        ProductResponse response = productCommandService.checkAndCreateProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(FormResponse.<ProductResponse>builder()
                        .data(response)
                        .message("Product created successfully")
                        .timestamp(Instant.now())
                        .build());
    }

    @PutMapping("/basic-info/{id}")
    public ResponseEntity<FormResponse<ProductResponse>> updateBasicInfo(
            @PathVariable UUID id,
            @Valid @RequestBody updateBasicInfoProduct request) {

        ProductResponse response = productCommandService.checkAndUpdateProduct(id, request);

        return ResponseEntity.ok(FormResponse.<ProductResponse>builder()
                .data(response)
                .message("Product updated successfully")
                .timestamp(Instant.now())
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FormResponse<Void>> deleteProduct(
            @PathVariable UUID id) {

        productCommandService.deleteProduct(id);

        return ResponseEntity.ok(FormResponse.<Void>builder()
                .message("Product deleted successfully")
                .timestamp(Instant.now())
                .build());
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<FormResponse<Void>> publishProduct(
            @PathVariable UUID id) {

        productCommandService.publishProduct(id);

        return ResponseEntity.ok(FormResponse.<Void>builder()
                .message("Product published successfully")
                .timestamp(Instant.now())
                .build());
    }

    @PostMapping("/{id}/unpublish")
    public ResponseEntity<FormResponse<Void>> unpublishProduct(
            @PathVariable UUID id) {

        productCommandService.unPublishProduct(id);

        return ResponseEntity.ok(FormResponse.<Void>builder()
                .message("Product unpublished successfully")
                .timestamp(Instant.now())
                .build());
    }

    @PutMapping("/{id}/variant")
    public ResponseEntity<FormResponse<ProductVariantResponse>> updateProductVariantBaseInfo(
            @PathVariable UUID id,
            @Valid @RequestBody ProductVariantRequest variantRequest) {

        ProductVariantResponse response = productCommandService.updateProductVariantBaseInfo(id, variantRequest);

        return ResponseEntity.ok(FormResponse.<ProductVariantResponse>builder()
                .data(response)
                .message("Product variant updated successfully")
                .timestamp(Instant.now())
                .build());
    }

    @DeleteMapping("/{id}/variant/{variantId}")
    public ResponseEntity<FormResponse<ProductVariantResponse>> removeProductVariant(
            @PathVariable UUID id,
            @PathVariable UUID variantId) {

        productCommandService.removeProductVariant(id, variantId);

        return ResponseEntity.ok(FormResponse.<ProductVariantResponse>builder()
                .message("Product variant removed successfully")
                .timestamp(Instant.now())
                .build());
    }

    @PostMapping("/{id}/variant")
    public ResponseEntity<FormResponse<ProductVariantResponse>> addProductVariant(
            @PathVariable UUID id,
            @Valid @RequestBody ProductVariantRequest variantRequest) {

        ProductVariantResponse response = productCommandService.addProductVariant(id, variantRequest);

        return ResponseEntity.ok(FormResponse.<ProductVariantResponse>builder()
                .data(response)
                .message("Product variant added successfully")
                .timestamp(Instant.now())
                .build());
    }



//     @PostMapping("/{id}/duplicate")
//     public ResponseEntity<FormResponse<ProductResponse>> duplicateProduct(
//             @PathVariable UUID id) {

//         ProductResponse response = productCommandService.duplicateProduct(id);

//         return ResponseEntity.status(HttpStatus.CREATED)
//                 .body(FormResponse.<ProductResponse>builder()
//                         .data(response)
//                         .message("Product duplicated successfully")
//                         .timestamp(Instant.now())
//                         .build());
//     }
}