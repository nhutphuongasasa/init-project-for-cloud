package com.cloud.order_service.infrastructure.adapter.outbound.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cloud.order_service.domain.model.FulfillmentOrder;
import com.cloud.order_service.domain.enums.OrderStatus;

@Repository
public interface FulfillmentOrderRepository extends JpaRepository<FulfillmentOrder, UUID>, JpaSpecificationExecutor<FulfillmentOrder> {
    Page<FulfillmentOrder> findAllByVendorId(UUID vendorId, Pageable pageable);
    boolean existsByOrderCode(String orderCode);
    boolean existsByVendorIdAndOrderCodeAndStatusIn(UUID vendorId, String orderCode, List<OrderStatus> statuses);

    @Query(value = """
        SELECT DISTINCT
            fod.product_variant_id,
            fod.warehouse_id
        FROM fulfillment_order_details fod
        JOIN fulfillment_orders fo ON fo.id = fod.order_id
        WHERE fo.status IN ('APPROVED', 'PICKING', 'PICKED', 'PACKING', 'PACKED')
            AND  (fod.product_variant_id, fod.warehouse_id) IN :pairs 
    """)
    List<Object[]> findPendingBatch(@Param("pairs") List<Object[]> pairs);
}