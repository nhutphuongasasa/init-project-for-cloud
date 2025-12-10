package com.cloud.product_service.application.exception.custom;

import com.cloud.product_service.common.exception.ForbiddenException;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 26/11/2025
 */
public class VendorNotOwnedException extends ForbiddenException{
    public VendorNotOwnedException(String message){
        super(message);
    }
}
