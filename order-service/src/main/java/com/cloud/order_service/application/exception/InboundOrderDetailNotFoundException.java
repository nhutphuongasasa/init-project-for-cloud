package com.cloud.order_service.application.exception;

import com.cloud.order_service.common.exception.ResourceNotFoundException;

public class InboundOrderDetailNotFoundException extends ResourceNotFoundException{
    public InboundOrderDetailNotFoundException() {
        super("Chi tiết phiếu nhập không tồn tại");
    }
}
