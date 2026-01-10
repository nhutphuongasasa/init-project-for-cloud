package com.cloud.product_service.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud.product_service.application.dto.ExcelCreateProductDto;
import com.cloud.product_service.application.dto.request.ProductCreateRequest;
import com.cloud.product_service.application.dto.request.ProductImageRequest;
import com.cloud.product_service.application.dto.request.ProductVariantRequest;
import com.cloud.product_service.application.dto.request.updateBasicInfoProduct;
import com.cloud.product_service.application.dto.response.ProductResponse;
import com.cloud.product_service.application.dto.response.ProductVariantResponse;
import com.cloud.product_service.application.exception.custom.ProductNotFoundException;
import com.cloud.product_service.application.exception.custom.ProductVariantNotFoundException;
import com.cloud.product_service.application.exception.custom.SlugAlreadyExistsException;
import com.cloud.product_service.application.importer.ImportResult;
import com.cloud.product_service.application.importer.ProductImportRow;
import com.cloud.product_service.application.mapper.ProductMapper;
import com.cloud.product_service.common.utils.jwt.JwtUtils;
import com.cloud.product_service.domain.enums.ProductColumn;
import com.cloud.product_service.domain.enums.ProductStatus;
import com.cloud.product_service.domain.model.Category;
import com.cloud.product_service.domain.model.Product;
import com.cloud.product_service.domain.model.ProductImage;
import com.cloud.product_service.domain.model.ProductVariant;
import com.cloud.product_service.infrastructure.adapter.outbound.repository.ProductRepository;
import com.cloud.product_service.infrastructure.adapter.outbound.repository.ProductVariantRepository;
import com.cloud.product_service.infrastructure.utils.excel.ExcelUtils;
import com.cloud.product_service.application.helper.SkuHelper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nhutphuong
 * @version 1.0
 * @created 26/11/2025
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final JwtUtils jwtUtils;
    private final CategoryService categoryService;
    private final ExcelUtils excelUtils;
    private final SkuHelper skuHelper;
    private final ProductVariantRepository productVariantRepository;

    public void updateBasicInfo(Product product, ProductImportRow row, ImportResult result){
        
    }


    
    public void importProductFromExcel(ExcelCreateProductDto excelDto){
        ProductImportRow first = excelDto.getRows().get(0);
        Row firstRow = first.getRow();
        ImportResult result = excelDto.getResult();

        //check category
        Category category = categoryService.getCategoryEntityBySlug(
            excelUtils.getStringCellValue(
                firstRow, 
                ProductColumn.CATEGORY.index()
            )
        );

        if (category == null){
            first.addError("category", "not exists");
            result.addFailedRows(excelDto.getRows());
            return;
        }

        UUID vendorId = jwtUtils.getCurrentUserId();

        //init product
        Product product = Product.builder()
            .name(excelUtils.getStringCellValue(firstRow, ProductColumn.PRODUCT_NAME.index()))
            .slug(skuHelper.generateSlug(excelUtils.getStringCellValue(firstRow, ProductColumn.PRODUCT_NAME.index())))
            .description(excelUtils.getStringCellValue(firstRow, ProductColumn.DESCRIPTION.index()))
            .status(ProductStatus.DRAFT)
            .productCode(excelDto.getProductCode())
            .categoryId(category.getId())
            .vendorId(vendorId)
            .build();

        int variantIndex = 0;
        //variant
        for(ProductImportRow row : excelDto.getRows()){
            Row r = row.getRow();
            //variant
            ObjectNode attributes = JsonNodeFactory.instance.objectNode();

            String sku = skuHelper.generateSku(vendorId, product, variantIndex++);

            for(int i = ProductColumn.ATTRIBUTES_START.index(); i < ProductColumn.ATTRIBUTES_END.index(); i++){
                String key = excelDto.getAttributeOptionsMap().get(i);
                String value = excelUtils.getStringCellValue(r, i);
                attributes.put(key, value);
            }

            ProductVariant productVariant = ProductVariant.builder()
                .sku(sku)
                .product(product)
                .vendorId(vendorId)
                .price(excelUtils.getBigDecimalCellValue(r, ProductColumn.PRICE.index()))
                .originalPrice(excelUtils.getBigDecimalCellValue(r, ProductColumn.ORIGINAL_PRICE.index()))
                .attributes(attributes)
                .weightGram(excelUtils.getIntegerCellValue(r, ProductColumn.WEIGHT_GRAM.index()))
                .build();

            product.getVariants().add(productVariant);

            //image
            for(int i = ProductColumn.IMAGE_START.index(); i <= ProductColumn.IMAGE_END.index(); i++){
                String image = excelUtils.getStringCellValue(r, i).trim();
                if (StringUtils.isBlank(image)) continue;

                ProductImage productImage = ProductImage.builder()
                    .url(image)
                    .isMain(false)
                    .variant(productVariant)
                    .build();
                productVariant.getImages().add(productImage);
            }
        }

        productRepository.save(product);
    }

    public ProductResponse checkAndCreateProduct(ProductCreateRequest productRequest) {
        log.info("Product request: {}", productRequest);

        if(checkSlug(productRequest.getSlug()) != null){
            throw new SlugAlreadyExistsException(productRequest.getSlug());
        }
        log.info("Check Slug is not exists");
        Product product = createProduct(productRequest);
        log.info("Product created successfully with id={}", productRequest.getSlug());
        
        return productMapper.toProductResponse(product);
    }

    private UUID checkSlug(String slug){
        return productRepository.findBySlug(slug)
            .map(Product::getId)
            .orElse(null);
    }

    private Product createProduct(ProductCreateRequest request){
        UUID vendorId = jwtUtils.getCurrentUserId();
        
        Product product = buildProduct(request, vendorId);

        List<ProductVariant> variants = buildProductVariant(request.getProductVariant(), vendorId, product);
        
        variants.forEach(product :: addVariant);
        log.info("Product variants: {}", variants);
        return productRepository.save(product);
    }

    private List<ProductVariant> buildProductVariant(List<ProductVariantRequest> requests, UUID vendorId, Product product){
        return IntStream.range(0, requests.size())
        .mapToObj(i -> buildVariant(requests.get(i), i, vendorId, product))
        .toList();
    }

    private ProductVariant buildVariant(
            ProductVariantRequest request, 
            int index, 
            UUID vendorId, 
            Product product) {
        
        log.info("===== buildVariant START: index={}", index);
        
        ProductVariant variant = productMapper.toProductVariant(request, vendorId, product);
        log.info("===== After mapper: variant.id={}, variant.images={}", 
            variant.getId(), 
            variant.getImages() != null ? "NOT_NULL (size=" + variant.getImages().size() + ")" : "NULL");
        
        variant.setSku(skuHelper.generateSku(vendorId, product, index));
        log.info("===== After setSku: {}", variant.getSku());
        
        // Kiểm tra images list
        if (variant.getImages() == null) {
            log.warn("===== Images list is NULL! Initializing...");
            variant.setImages(new ArrayList<>());
        }
        
        List<ProductImage> images = buildImages(request.getImages(), variant);
        log.info("===== buildImages returned {} images", images.size());
        
        // Log trước khi add
        log.info("===== Before addImage: variant.images.size={}", variant.getImages().size());
        
        images.forEach(img -> {
            log.info("===== Adding image: url={}, variant.id={}, img.variant={}", 
                img.getUrl(), 
                variant.getId(),
                img.getVariant() != null ? "NOT_NULL" : "NULL");
            variant.addImage(img);
            log.info("===== After addImage: img.variant={}", img.getVariant() != null ? "NOT_NULL" : "NULL");
        });
        
        log.info("===== After addImage loop: variant.images.size={}", variant.getImages().size());
        log.info("===== buildVariant END");
        
        return variant;
    }


    private List<ProductImage> buildImages(List<ProductImageRequest> requests, ProductVariant variant) {
        log.info("===== buildImages START: requests={}, variant.id={}", 
            requests != null ? requests.size() : "NULL",
            variant != null ? variant.getId() : "NULL");
        
        if (requests == null || requests.isEmpty()) {
            log.info("===== buildImages: No images to build");
            return new ArrayList<>();  // Đổi từ Collections.emptyList()
        }
        
        List<ProductImage> result = requests.stream()
            .map(req -> {
                ProductImage img = ProductImage.builder()
                    .url(req.getUrl())
                    .isMain(Boolean.TRUE.equals(req.getIsMain()))
                    .build();
                log.info("===== Built image: url={}, isMain={}, variant={}", 
                    img.getUrl(), img.getIsMain(), img.getVariant() != null ? "NOT_NULL" : "NULL");
                return img;
            })
            .toList();
        
        log.info("===== buildImages END: returning {} images", result.size());
        return result;
    }

    private Product buildProduct(ProductCreateRequest request, UUID vendorId){
        Product product = productMapper.toProduct(request, vendorId, request.getCategoryId());
        product.setProductCode(generateProductCode(vendorId, request.getSlug()));
        product.setStatus(ProductStatus.ACTIVE);
        return product;
    }

    public ProductResponse checkAndUpdateProduct(UUID id, updateBasicInfoProduct productRequest) {
        Product product = productRepository.findByIdAndVendorId(id, jwtUtils.getCurrentUserId())
            .orElseThrow(() -> new ProductNotFoundException());

        updateProduct(id, productRequest, product);

        return productMapper.toProductResponse(product);
    }

    private Product updateProduct(UUID id, updateBasicInfoProduct productRequest, Product product) {
        productMapper.updateBaseInfoProduct(productRequest, product);

        productRepository.save(product);

        return product;
    }

    public boolean deleteProduct(UUID productId) {
        Product product = productRepository.findByIdAndVendorId(productId, jwtUtils.getCurrentUserId())
            .orElseThrow(() -> new ProductNotFoundException());

        product.setStatus(ProductStatus.INACTIVE);

        productRepository.save(product);
        return true;
    }

    public void publishProduct(UUID productId) {
        Product product = productRepository.findByIdAndVendorId(productId, jwtUtils.getCurrentUserId())
            .orElseThrow(() -> new ProductNotFoundException());

        product.setStatus(ProductStatus.ACTIVE);

        productRepository.save(product);
    }

    public void unPublishProduct(UUID productId) {
        Product product = productRepository.findByIdAndVendorId(productId, jwtUtils.getCurrentUserId())
            .orElseThrow(() -> new ProductNotFoundException());

        product.setStatus(ProductStatus.DRAFT);

        productRepository.save(product);
    }

    public void removeProductVariant(UUID productId, UUID variantId) {
        Product product = productRepository.findByIdAndVendorId(productId, jwtUtils.getCurrentUserId())
            .orElseThrow(() -> new ProductNotFoundException());

        ProductVariant variant = productVariantRepository.findById(variantId)
            .orElseThrow(() -> new ProductVariantNotFoundException());

        product.removeVariant(variant);

        productVariantRepository.delete(variant);

    }

    public ProductVariantResponse addProductVariant(UUID productId, ProductVariantRequest variantRequest) {
        Product product = productRepository.findByIdAndVendorId(productId, jwtUtils.getCurrentUserId())
            .orElseThrow(() -> new ProductNotFoundException());
        
        UUID vendorId = jwtUtils.getCurrentUserId();
        int index = product.getVariants().size();

        ProductVariant variant = buildVariant(variantRequest, index, vendorId, product);

        product.addVariant(variant);

        productVariantRepository.save(variant);

        return productMapper.toProductVariantResponse(variant);
    }

    public ProductVariantResponse updateProductVariantBaseInfo(UUID id, ProductVariantRequest variantRequest) {
        log.info("===== updateProductVariantBaseInfo START: id={}, variantRequest={}", id, variantRequest);
        ProductVariant variant = productVariantRepository.findById(id)
            .orElseThrow(() -> new ProductVariantNotFoundException());

        productMapper.updateProductVariant(variant, variantRequest);

        productVariantRepository.save(variant);

        return productMapper.toProductVariantResponse(variant);
    }

    // public ProductResponse duplicateProduct(UUID productId) {
    //     Product product = productRepository.findById(productId)
    //         .orElseThrow(() -> new ProductNotFoundException());
        
    //     UUID vendorId = jwtUtils.getCurrentUserId();

    //     if (!product.getVendorId().equals(vendorId)) {
    //         throw new ProductNotFoundException();
    //     }

    //     Product duplicateProduct = productMapper.duplicateProduct(product);
    //     duplicateProduct.setSlug(product.getSlug() + "-copy" + System.currentTimeMillis());
    //     duplicateProduct.setStatus(ProductStatus.DRAFT);
        
    //     //clone images
    //     List<ProductImage> duplicateImages = product.getImages().stream()
    //         .map(image -> productMapper.duplicateProductImage(image))
    //         .toList();
    //     duplicateProduct.setImages(duplicateImages);

    //     //clone variants
    //     List<ProductVariant> variants = product.getVariants();
    //     List<ProductVariant> duplicateVariants = variants.stream()
    //         .map(variant -> productMapper.duplicateProductVariant(variant))
    //         .toList();
    //     duplicateProduct.setVariants(duplicateVariants);

    //     productRepository.save(duplicateProduct);
            
    //     return productMapper.toProductResponse(duplicateProduct);
    // }

    private String generateProductCode(UUID vendorId, String slug) {
        String vendorCode = vendorId.toString().substring(0, 6).toUpperCase();

        String slugCode;
        if (slug == null || slug.isBlank()) {
            slugCode = "XXX";
        } else {
            slugCode = slug.length() <= 3 ? slug.toLowerCase() : slug.substring(0, 3).toLowerCase();
        }

        return "P-" + vendorCode + "-" + slugCode;
    }
}