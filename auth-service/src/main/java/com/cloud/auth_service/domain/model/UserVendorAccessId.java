package com.cloud.auth_service.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode 
public class UserVendorAccessId implements Serializable {
    
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "vendor_id")
    private UUID vendorId;
}