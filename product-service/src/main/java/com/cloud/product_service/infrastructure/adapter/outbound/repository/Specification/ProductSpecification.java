package com.cloud.product_service.infrastructure.adapter.outbound.repository.Specification;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.cloud.product_service.application.dto.request.ProductSearchRequest;
import com.cloud.product_service.domain.model.Product;

import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {
    public static Specification<Product> fromRequest(ProductSearchRequest request, UUID vendorId){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(request.getName() != null){
                predicates.add(cb.equal(root.get("name"), request.getName()));
            }

            if(request.getSlug() != null){
                predicates.add(cb.equal(root.get("slug"), request.getSlug()));
            }

            if(request.getCategoryId() != null){
                predicates.add(cb.equal(root.get("categoryId"), request.getCategoryId()));
            }

            if (vendorId != null) {
                predicates.add(cb.equal(root.get("vendorId"), vendorId));
            }else if(request.getVendorId() != null){
                predicates.add(cb.equal(root.get("vendorId"), request.getVendorId()));
            }

            if(request.getStatus() != null){
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if(request.getPrice() != null){
                predicates.add(cb.equal(root.get("price"), request.getPrice()));
            }

            if(request.getOriginalPrice() != null){
                predicates.add(cb.equal(root.get("originalPrice"), request.getOriginalPrice()));
            }

            // if(request.getAttributes() != null){
            //     predicates.add(cb.equal(root.get("attributes"), request.getAttributes()));
            // }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
