package com.cloud.vendor_service.domain.exception.custom;

import com.cloud.vendor_service.common.exception.InvalidOperationException;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 24/11/2025
 */
public class VendorStatusException extends InvalidOperationException {
    public VendorStatusException(String message){
        super(message);
    }
}
