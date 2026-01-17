package com.cloud.auth_service.domain.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @nhutphuong
 * @since 2026/1/11 12h
 * @version 1
 */
@Builder
@Entity
@Table(name = "jwt_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtKeys {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(updatable = false, nullable = false)
    private UUID kid;

    @Column(name = "private_key")
    private String privateKey;

    @Column(name = "public_key")
    private String publicKey;

    private String algorithm;
    
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "is_active")
    private boolean Active;
}