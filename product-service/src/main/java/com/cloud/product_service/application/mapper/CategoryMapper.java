package com.cloud.product_service.application.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud.product_service.application.dto.request.CategoryCreateRequest;
import com.cloud.product_service.application.dto.request.CategoryUpdateRequest;
import com.cloud.product_service.application.dto.response.CategoryResponse;
import com.cloud.product_service.domain.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    Category toEntity(CategoryCreateRequest categoryCreateRequest);

    @Mapping(target = "parentId", source = "parent.id")
    CategoryResponse toResponse(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", source = "parent")
    @Mapping(target = "iconUrl", source = "categoryCreateRequest.iconUrl")
    @Mapping(target = "slug", source = "categoryCreateRequest.slug")
    @Mapping(target = "name", source = "categoryCreateRequest.name")
    Category toEntity(CategoryCreateRequest categoryCreateRequest, Category parent);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "parent", ignore = true)
    })
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Category updateCategory(@MappingTarget Category category, CategoryUpdateRequest categoryUpdateRequest);
}
