CREATE TABLE vendors (
    id              UUID PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    slug            VARCHAR(100) UNIQUE NOT NULL,     
    logo_url        VARCHAR(500),
    description     TEXT,
    code            VARCHAR(50) UNIQUE NOT NULL,
    owner_id        UUID NOT NULL,        

    status          VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACTIVE', 'REJECTED', 'SUSPENDED', 'DELETED')),

    joined_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vendors_slug   ON vendors(slug);
CREATE INDEX idx_vendors_status ON vendors(status);
CREATE INDEX idx_vendors_owner  ON vendors(owner_id);

CREATE TABLE vendor_members (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    vendor_id   UUID NOT NULL,
    user_id     UUID NOT NULL,
    
    role        VARCHAR(50) NOT NULL DEFAULT 'STAFF' CHECK (role IN ('OWNER', 'STAFF', 'VIEWER')),
    
    permissions JSONB DEFAULT '{}'::jsonb,
    
    status      VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INVITED', 'SUSPENDED', 'LEFT')),
    
    joined_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    left_at     TIMESTAMP,     

    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP   

    CONSTRAINT unique_vendor_user UNIQUE (vendor_id, user_id),
    
    FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE
);

CREATE INDEX idx_vendor_members_vendor ON vendor_members(vendor_id);
CREATE INDEX idx_vendor_members_user   ON vendor_members(user_id);
CREATE INDEX idx_vendor_members_role   ON vendor_members(role);


CREATE TABLE vendor_profiles (
    vendor_id       UUID PRIMARY KEY,
    legal_name      VARCHAR(255),               
    tax_code        VARCHAR(50) UNIQUE,   
    pickup_address  TEXT,                       
    bank_info       JSONB DEFAULT '{}'::jsonb,   
    business_license_url VARCHAR(500),
    address         TEXT,
    phone           VARCHAR(20),
    email           VARCHAR(255),
    website_url     VARCHAR(255),
    social_links    JSONB DEFAULT '{}'::jsonb,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE
);

CREATE TABLE vendor_audit_logs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vendor_id       UUID NOT NULL REFERENCES vendors(id),
    action          VARCHAR(50) NOT NULL, 
    old_value       TEXT,
    new_value       TEXT,
    reason          TEXT,                
    performed_by    UUID NOT NULL,       
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_vendor_audit_vendor ON vendor_audit_logs(vendor_id);