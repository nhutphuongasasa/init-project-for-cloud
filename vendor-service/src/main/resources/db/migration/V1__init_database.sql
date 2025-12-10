CREATE TABLE IF NOT EXISTS vendors (
    id              UUID PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    slug            VARCHAR(100) UNIQUE NOT NULL,
    logo_url        VARCHAR(500),
    description     VARCHAR(500),
    status          VARCHAR(20) DEFAULT 'PENDING',
    joined_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_name ON vendors(name);

CREATE INDEX IF NOT EXISTS idx_slug ON vendors(slug);
CREATE INDEX IF NOT EXISTS idx_status ON vendors(status);

CREATE TABLE IF NOT EXISTS vendor_profiles (
    vendor_id   UUID PRIMARY KEY,
    address     TEXT,
    phone       VARCHAR(20) UNIQUE,
    email       VARCHAR(255) NOT NULL,
    tax_code    VARCHAR(50),
    website_url VARCHAR(255),
    social_links JSONB DEFAULT '{}'::jsonb,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_email ON vendor_profiles(email);

INSERT INTO vendors (id, name, slug, logo_url, description, status)
VALUES (
    'c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b',
    'Vendor Demo',
    'vendor-demo',
    'https://example.com/logo.png',
    'Đây là vendor mặc định để test',
    'ACTIVE'
);

INSERT INTO vendor_profiles (vendor_id, address, phone, email, tax_code, website_url)
VALUES (
    'c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b',
    '123 Đường ABC, TP.HCM',
    '0909123456',
    'demo@vendor.com',
    'TAX123456',
    'https://vendor-demo.com'
);

-- Vendor đang chờ duyệt
INSERT INTO vendors (id, name, slug, logo_url, description, status)
VALUES (
    gen_random_uuid(),
    'Nhà cung cấp A',
    'nha-cung-cap-a',
    NULL,
    'Vendor chưa có logo',
    'PENDING'
);

-- Vendor bị từ chối
INSERT INTO vendors (id, name, slug, logo_url, description, status)
VALUES (
    gen_random_uuid(),
    'Nhà cung cấp B',
    'nha-cung-cap-b',
    'https://example.com/logo-b.png',
    'Vendor bị từ chối do thiếu giấy tờ',
    'REJECTED'
);

-- Vendor đã active
INSERT INTO vendors (id, name, slug, logo_url, description, status)
VALUES (
    gen_random_uuid(),
    'Nhà cung cấp C',
    'nha-cung-cap-c',
    'https://example.com/logo-c.png',
    'Vendor chuyên cung cấp sản phẩm điện tử',
    'ACTIVE'
);

-- Profile cho Vendor A
INSERT INTO vendor_profiles (vendor_id, address, phone, email, tax_code, website_url, social_links)
VALUES (
    (SELECT id FROM vendors WHERE slug = 'nha-cung-cap-a'),
    '456 Đường XYZ, Hà Nội',
    '0912345678',
    'vendorA@example.com',
    'TAXA123',
    'https://vendor-a.com',
    '{"facebook": "https://facebook.com/vendorA", "zalo": "vendorA"}'
);

-- Profile cho Vendor B
INSERT INTO vendor_profiles (vendor_id, address, phone, email, tax_code, website_url, social_links)
VALUES (
    (SELECT id FROM vendors WHERE slug = 'nha-cung-cap-b'),
    '789 Đường DEF, Đà Nẵng',
    '0987654321',
    'vendorB@example.com',
    'TAXB456',
    NULL,
    '{"instagram": "https://instagram.com/vendorB"}'
);

-- Profile cho Vendor C
INSERT INTO vendor_profiles (vendor_id, address, phone, email, tax_code, website_url, social_links)
VALUES (
    (SELECT id FROM vendors WHERE slug = 'nha-cung-cap-c'),
    '321 Đường GHI, Cần Thơ',
    '0978123456',
    'vendorC@example.com',
    'TAXC789',
    'https://vendor-c.com',
    '{"linkedin": "https://linkedin.com/company/vendorC"}'
);
