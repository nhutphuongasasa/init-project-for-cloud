package com.cloud.vendor_service.infrastructure.specification;

import com.cloud.vendor_service.application.dto.request.VendorSearchRequest;
import com.cloud.vendor_service.domain.model.Vendor;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class VendorSpecifications {
    public static Specification<Vendor> buildSpecification(VendorSearchRequest request) {
        //root dai dien cho Vendor
        //query dai dien cho query
        //cb dai dien cho CriteriaBuilder(tao dieu kien)
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getName() != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%"));
            }
            if (request.getSlug() != null) {
                predicates.add(cb.equal(root.get("slug"), request.getSlug()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getEmail() != null) {
                // join với vendor_profiles
                var profileJoin = root.join("profile");
                predicates.add(cb.equal(profileJoin.get("email"), request.getEmail()));
            }
            if (request.getTaxCode() != null) {
                var profileJoin = root.join("profile");
                predicates.add(cb.equal(profileJoin.get("taxCode"), request.getTaxCode()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
