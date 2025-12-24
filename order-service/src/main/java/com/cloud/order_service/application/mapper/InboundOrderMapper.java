package com.cloud.order_service.application.mapper;

import org.mapstruct.*;

import com.cloud.order_service.application.dto.request.CreateInboundRequest;
import com.cloud.order_service.application.dto.request.InboundItemRequest;
import com.cloud.order_service.application.dto.response.InboundItemResponse;
import com.cloud.order_service.application.dto.response.InboundOrderResponse;
import com.cloud.order_service.application.dto.response.InboundSummaryResponse;
import com.cloud.order_service.domain.model.InboundOrder;
import com.cloud.order_service.domain.model.InboundOrderDetail;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InboundOrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inboundCode", ignore = true)
    @Mapping(target = "status", constant = "DRAFT")
    @Mapping(target = "createdBy", source = "currentUserId")
    @Mapping(target = "items", source = "request.items")
    @Mapping(target = "expectedAt", expression = "java(request.getExpectedAt().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())")

    InboundOrder toEntity(CreateInboundRequest request, UUID currentUserId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inboundOrder", source = "order")
    InboundOrderDetail toDetailEntity(InboundItemRequest item, InboundOrder order);

    @Mapping(target = "createdBy", expression = "java(entity.getCreatedBy())")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt())")
    InboundOrderResponse toResponse(InboundOrder entity);

    @Mapping(target = "totalItems", expression = "java(entity.getItems().stream().mapToInt(InboundOrderDetail::getQuantityExpected).sum())")
    @Mapping(target = "receivedItems", expression = "java(entity.getItems().stream().mapToInt(d -> d.getQuantityReceived() != null ? d.getQuantityReceived() : 0).sum())")
    InboundSummaryResponse toSummaryResponse(InboundOrder entity);

    List<InboundItemResponse> toItemResponseList(List<InboundOrderDetail> details);

    default InboundItemResponse toItemResponse(InboundOrderDetail detail) {
        return new InboundItemResponse(
            detail.getId(),
            detail.getProductVariantId(),
            // detail.getSku(),
            detail.getProductName(),
            detail.getQuantityExpected(),
            detail.getQuantityReceived(),
            detail.getUnitPrice(),
            detail.getNotes()
        );
    }
}