package com.cloud.inventory.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder
@Setter
@Getter
@Entity
@Table(name = "warehouses")
@NoArgsConstructor
@AllArgsConstructor
@Access(AccessType.FIELD)
@EntityListeners(AuditingEntityListener.class)
public class Warehouse {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @OneToMany(mappedBy = "warehouse")
    private Set<InventoryItem> inventoryItems;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
