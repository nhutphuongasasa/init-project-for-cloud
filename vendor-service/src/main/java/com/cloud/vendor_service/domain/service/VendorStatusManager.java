package com.cloud.vendor_service.domain.service;

import com.cloud.vendor_service.domain.model.Vendor;
import com.cloud.vendor_service.infastructure.exception.custom.InvalidVendorStatusTransitionException;
import com.cloud.vendor_service.domain.enums.VendorStatus;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 24/11/2025
 */
public class VendorStatusManager {

    public void approveVendor(Vendor vendor){
        if (vendor.getStatus() != VendorStatus.PENDING) {
            throw new InvalidVendorStatusTransitionException("Vendor status is not PENDING");
        }
        vendor.setStatus(VendorStatus.ACTIVE);
    }

    public void rejectVendor(Vendor vendor){
        if (vendor.getStatus() != VendorStatus.PENDING) {
            throw new InvalidVendorStatusTransitionException("Vendor status is not PENDING");
        }
        vendor.setStatus(VendorStatus.REJECTED);
    }

    public void suspendVendor(Vendor vendor){
        if (vendor.getStatus() != VendorStatus.ACTIVE) {
            throw new InvalidVendorStatusTransitionException("Vendor status is not ACTIVE");
        }
        vendor.setStatus(VendorStatus.SUSPENDED);
    }

    public void banVendor(Vendor vendor){
        if (vendor.getStatus() != VendorStatus.ACTIVE && vendor.getStatus() != VendorStatus.SUSPENDED) {
            throw new InvalidVendorStatusTransitionException("Can't ban vendor with status " + vendor.getStatus());
        }
        vendor.setStatus(VendorStatus.BANNED);
    }
}
