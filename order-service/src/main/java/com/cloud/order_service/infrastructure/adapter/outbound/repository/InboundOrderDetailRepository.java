package com.cloud.order_service.infrastructure.adapter.outbound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cloud.order_service.domain.model.InboundOrderDetail;

@Repository
public interface InboundOrderDetailRepository extends JpaRepository<InboundOrderDetail, Long> {
    
}
