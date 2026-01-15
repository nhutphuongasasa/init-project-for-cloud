package com.cloud.order_service.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.cloud.order_service.application.dto.request.CreateOrderItemRequest;
import com.cloud.order_service.application.dto.request.CreateOrderRequest;
import com.cloud.order_service.application.dto.response.OrderItemResponse;
import com.cloud.order_service.application.dto.response.OrderResponse;
import com.cloud.order_service.application.dto.response.OrderSummaryResponse;
import com.cloud.order_service.domain.model.FulfillmentOrder;
import com.cloud.order_service.domain.model.FulfillmentOrderDetail;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FulfillmentOrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderCode", ignore = true)
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "createdBy", source = "vendorId")
    @Mapping(target = "items", source = "request.items")
    FulfillmentOrder toEntity(CreateOrderRequest request, UUID vendorId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", source = "order")
    @Mapping(target = "quantityRequested", source = "item.quantity")
    @Mapping(target = "unitPrice", source = "item.unitPrice")
    @Mapping(target = "quantityPicked", constant = "0")
    FulfillmentOrderDetail toDetailEntity(CreateOrderItemRequest item, FulfillmentOrder order);

    OrderResponse toResponse(FulfillmentOrder entity);

    @Mapping(target = "totalItems", expression = "java(entity.getItems().stream().mapToInt(FulfillmentOrderDetail::getQuantityRequested).sum())")
    @Mapping(target = "pickedItems", expression = "java(entity.getItems().stream().mapToInt(d -> d.getQuantityPicked() != null ? d.getQuantityPicked() : 0).sum())")
    @Mapping(target = "totalAmount", expression = "java(calculateTotalAmount(entity))")
    OrderSummaryResponse toSummaryResponse(FulfillmentOrder entity);

    List<OrderItemResponse> toItemResponseList(List<FulfillmentOrderDetail> details);

    default OrderItemResponse toItemResponse(FulfillmentOrderDetail detail) {
        return new OrderItemResponse(
            detail.getId(),
            detail.getProductVariantId(),
            // detail.getSku(),
            detail.getProductName(),
            detail.getQuantityRequested(),
            detail.getQuantityPicked(),
            detail.getUnitPrice(),
            detail.getNotes()
        );
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    FulfillmentOrderDetail updateDetailFromRequest(CreateOrderItemRequest request, @MappingTarget FulfillmentOrderDetail detail);

    // ==============================
    // SUPPORT METHODS
    // ==============================
    default BigDecimal calculateTotalAmount(FulfillmentOrder entity) {
        if (entity.getItems() == null) {
            return BigDecimal.ZERO;
        }
        return entity.getItems().stream()
            .map(d -> d.getUnitPrice() != null
                    ? d.getUnitPrice().multiply(BigDecimal.valueOf(d.getQuantityRequested()))
                    : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
