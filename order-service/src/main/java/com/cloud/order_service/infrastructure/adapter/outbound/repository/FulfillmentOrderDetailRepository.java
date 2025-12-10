package com.cloud.order_service.infrastructure.adapter.outbound.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cloud.order_service.domain.model.FulfillmentOrderDetail;

@Repository
public interface FulfillmentOrderDetailRepository extends JpaRepository<FulfillmentOrderDetail, Long> {
    Optional<FulfillmentOrderDetail> findById(Long id);
     
}