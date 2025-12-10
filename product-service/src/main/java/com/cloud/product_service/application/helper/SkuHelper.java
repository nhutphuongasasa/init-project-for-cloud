package com.cloud.product_service.application.helper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.cloud.product_service.domain.model.Product;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SkuHelper {
    public String generateSlug(String name) {
        return normalizeString(name);
    }

    public String generateSku(UUID vendorId, Product product, int variantIndex) {
        String vendorCode = getVendorCode(vendorId);
        String productCode = normalizeString(product.getProductCode());
        String variantCode = String.format("%03d", variantIndex);

        String sku = String.format("%s-%s-%s", vendorCode, productCode, variantCode);
        log.info("Generated SKU: {} for vendorId={}, productId={}", sku, vendorId, product.getId());
        return sku;
    }

    private String getVendorCode(UUID vendorId) {
        String code = vendorId.toString()
            .substring(0, 6)
            .toUpperCase()
            .replaceAll("[^A-Z0-9]", "");
        log.debug("VendorCode generated: {} from vendorId={}", code, vendorId);
        return code;
    }

    private String normalizeString(String str){
        return str.toLowerCase()
            .replaceAll("\\s+", "-")
            .replaceAll("[^a-z0-9-]", "");
    }

}
