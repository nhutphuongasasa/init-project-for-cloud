package com.cloud.product_service.application.service;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.cloud.product_service.application.dto.request.CategoryCreateRequest;
import com.cloud.product_service.application.dto.request.CategoryUpdateRequest;
import com.cloud.product_service.application.dto.response.CategoryResponse;
import com.cloud.product_service.application.exception.custom.CategoryAlreadyExistsException;
import com.cloud.product_service.application.exception.custom.CategoryNotFoundException;
import com.cloud.product_service.application.mapper.CategoryMapper;
import com.cloud.product_service.domain.model.Category;
import com.cloud.product_service.infrastructure.adapter.outbound.repository.CategoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
            .stream()
            .map(categoryMapper::toResponse)
            .toList();
    }

    public CategoryResponse getCategoryById(UUID categoryId) {
        return categoryMapper.toResponse(
            categoryRepository.findById(categoryId)
            .orElseThrow(() -> {
                log.warn("Category not found with id={}", categoryId);
                return new CategoryNotFoundException();
            })
        );
    }

    public CategoryResponse getCategoryBySlug(String slug) {
        return categoryMapper.toResponse(
            categoryRepository.findBySlug(slug)
            .orElseThrow(() -> {
                log.warn("Category not found with slug={}", slug);
                return new CategoryNotFoundException();
            })
        );
    }

    public Category getCategoryEntityBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
            .orElseThrow(() -> {
                log.warn("Category entity not found with slug={}", slug);
                return new CategoryNotFoundException();
            });
    }

    public CategoryResponse createCategory(CategoryCreateRequest categoryCreateRequest) {
        if(categoryRepository.existsBySlug(categoryCreateRequest.getSlug())) {
            throw new CategoryAlreadyExistsException("Category already exists");
        }

        log.debug("Creating category with data={}", categoryCreateRequest);

        Category category = categoryMapper.toEntity(categoryCreateRequest);

        if (categoryCreateRequest.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryCreateRequest.getParentId())
                .orElseThrow(() -> {
                    log.warn("Parent category not found with id={}", categoryCreateRequest.getParentId());
                    return new CategoryNotFoundException();
                });
            category.setParent(parent);
        }

        category.setIsActive(true);
        categoryRepository.save(category);

        log.info("Category created successfully with id={}", category.getId());
        
        return categoryMapper.toResponse(category);
    }

    public CategoryResponse updateCategory(UUID categoryId, CategoryUpdateRequest categoryUpdateRequest) {
        log.info("Updating category id={} with data={}", categoryId, categoryUpdateRequest);

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> {
                log.warn("Category not found for update, id={}", categoryId);
                return new CategoryNotFoundException();
            });

        category = categoryMapper.updateCategory(category, categoryUpdateRequest);
        categoryRepository.save(category);

        log.info("Category updated successfully id={}", categoryId);

        return categoryMapper.toResponse(category);
    }

    public void deleteCategory(UUID categoryId) {
        log.info("Deleting category id={}", categoryId);

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> {
                log.warn("Category not found for delete, id={}", categoryId);
                return new CategoryNotFoundException();
            });
        categoryRepository.delete(category);

        log.info("Category deleted successfully id={}", categoryId);
    }
}
