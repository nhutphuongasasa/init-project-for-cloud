package com.cloud.order_service.infrastructure.adapter.outbound.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cloud.order_service.domain.model.InboundOrder;

@Repository
public interface InboundOrderRepository extends JpaRepository<InboundOrder, UUID> {
    boolean existsByVendorIdAndInboundCode(UUID vendorId, String inboundCode);

    @Query("SELECT i FROM InboundOrder i LEFT JOIN FETCH i.items WHERE i.id = :id")
    Optional<InboundOrder> findByIdWithItems(@Param("id") UUID id);

    Page<InboundOrder> findAllByVendorId(UUID vendorId, Pageable pageable);
}
