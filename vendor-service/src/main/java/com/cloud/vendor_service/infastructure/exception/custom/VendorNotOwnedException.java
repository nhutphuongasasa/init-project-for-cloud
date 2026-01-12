package com.cloud.vendor_service.infastructure.exception.custom;

import com.cloud.vendor_service.common.exception.ForbiddenException;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 24/11/2025
 */
public class VendorNotOwnedException extends ForbiddenException{
    public VendorNotOwnedException(String message){
        super(message);
    }
}
