package com.cloud.product_service.infrastructure.adapter.inbound.rest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.product_service.application.dto.request.CategoryCreateRequest;
import com.cloud.product_service.application.dto.request.CategoryUpdateRequest;
import com.cloud.product_service.application.dto.response.CategoryResponse;
import com.cloud.product_service.application.service.CategoryService;
import com.cloud.product_service.common.response.FormResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<FormResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryCreateRequest request) {

        CategoryResponse response = categoryService.createCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(FormResponse.<CategoryResponse>builder()
                        .data(response)
                        .message("Category created successfully")
                        .timestamp(Instant.now())
                        .build());
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<FormResponse<CategoryResponse>> updateCategory(
            @Valid @RequestBody CategoryUpdateRequest request,
            @PathVariable UUID categoryId
        ) {

        CategoryResponse response = categoryService.updateCategory(categoryId, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(FormResponse.<CategoryResponse>builder()
                        .data(response)
                        .message("Category updated successfully")
                        .timestamp(Instant.now())
                        .build());
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<FormResponse<CategoryResponse>> deleteCategory(
            @PathVariable UUID categoryId
        ) {

        categoryService.deleteCategory(categoryId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(FormResponse.<CategoryResponse>builder()
                        .message("Category deleted successfully")
                        .timestamp(Instant.now())
                        .build());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<FormResponse<CategoryResponse>> getCategoryById(
            @PathVariable UUID categoryId
        ) {

        CategoryResponse response = categoryService.getCategoryById(categoryId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(FormResponse.<CategoryResponse>builder()
                        .data(response)
                        .message("Category fetched successfully")
                        .timestamp(Instant.now())
                        .build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<FormResponse<CategoryResponse>> getCategoryBySlug(
            @PathVariable String slug
        ) {

        CategoryResponse response = categoryService.getCategoryBySlug(slug);

        return ResponseEntity.status(HttpStatus.OK)
                .body(FormResponse.<CategoryResponse>builder()
                        .data(response)
                        .message("Category fetched successfully")
                        .timestamp(Instant.now())
                        .build());
    }
    
    @GetMapping("/all")
    public ResponseEntity<FormResponse<List<CategoryResponse>>> getAllCategories() {

        List<CategoryResponse> response = categoryService.getAllCategories();

        return ResponseEntity.status(HttpStatus.OK)
                .body(FormResponse.<List<CategoryResponse>>builder()
                        .data(response)
                        .message("Categories fetched successfully")
                        .timestamp(Instant.now())
                        .build());
    }
}
