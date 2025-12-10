package com.cloud.product_service.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter  
@Setter
@Builder
@Table(name = "product_variants")
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"product", "images"})
public class ProductVariant {
    @Id 
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "vendor_id")
    private UUID vendorId;
    
    private String sku;

    @Column(name = "price")
    private BigDecimal price;
    
    @Column(name = "original_price")
    private BigDecimal originalPrice;
    
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private JsonNode attributes;
    
    @Column(name = "weight_gram")
    private Integer weightGram;
    
    @Builder.Default
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    public void addImage(ProductImage image) {
        this.images.add(image);
        image.setVariant(this);
    }

    public void removeImage(ProductImage image) {
        this.images.remove(image);
        image.setVariant(null);
    }
}