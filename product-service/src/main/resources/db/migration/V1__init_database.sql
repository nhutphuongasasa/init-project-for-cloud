
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS categories (
    id         UUID PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    parent_id  UUID,
    slug       VARCHAR(255) UNIQUE,
    icon_url   VARCHAR(500),
    is_active  BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (parent_id) REFERENCES categories(id)
);

CREATE INDEX IF NOT EXISTS idx_categories_name ON categories(name);
CREATE INDEX IF NOT EXISTS idx_categories_slug ON categories(slug);
CREATE INDEX IF NOT EXISTS idx_categories_active ON categories(is_active);
CREATE INDEX IF NOT EXISTS idx_categories_parent ON categories(parent_id);

CREATE TABLE products (
    id           UUID PRIMARY KEY ,
    vendor_id    UUID NOT NULL,
    name         VARCHAR(255) NOT NULL,
    description  TEXT,
    product_code VARCHAR(70),
    slug         VARCHAR(255) NOT NULL,
    category_id  UUID,
    status       VARCHAR(20) DEFAULT 'DRAFT',
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_slug UNIQUE (slug, vendor_id),
    CONSTRAINT unique_product_code UNIQUE (product_code, vendor_id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE INDEX IF NOT EXISTS idx_products_vendor ON products(vendor_id);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category_id);
CREATE INDEX IF NOT EXISTS idx_products_status ON products(status);

CREATE TABLE product_variants (
    id              UUID PRIMARY KEY  ,
    product_id      UUID NOT NULL,
    sku             VARCHAR(100) UNIQUE NOT NULL,
    vendor_id       UUID NOT NULL,
    price           DECIMAL(12,2) NOT NULL,
    original_price  DECIMAL(12,2),
    attributes      JSONB DEFAULT '{}',
    weight_gram     INT DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_variants_product ON product_variants(product_id);
CREATE INDEX IF NOT EXISTS idx_variants_sku ON product_variants(sku);
CREATE INDEX IF NOT EXISTS idx_variants_vendor ON product_variants(vendor_id);
CREATE INDEX IF NOT EXISTS idx_variants_price ON product_variants(price);

CREATE TABLE product_images (
    id          UUID PRIMARY KEY,
    -- product_id  UUID,
    url         VARCHAR(500) NOT NULL,
    variant_id  UUID NOT NULL,
    -- sort_order  INT DEFAULT 0,
    is_main     BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE CASCADE
);

CREATE TABLE import_jobs (
    id              UUID PRIMARY KEY ,
    vendor_id       UUID NOT NULL,
    file_name       VARCHAR(255) NOT NULL,
    total_rows      BIGINT,
    processed_rows  BIGINT DEFAULT 0,
    success_rows    BIGINT DEFAULT 0,
    failed_rows     BIGINT DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'PENDING',
    started_at      TIMESTAMP,
    finished_at     TIMESTAMP,
    error_message   TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_import_jobs_vendor ON import_jobs(vendor_id);

INSERT INTO categories (id, name, slug, icon_url, is_active)
VALUES 
    ('11111111-1111-1111-1111-111111111111', 'Quần áo', 'quan-ao', 'https://example.com/icons/clothes.png', TRUE),
    ('22222222-2222-2222-2222-222222222222', 'Điện thoại', 'dien-thoai', 'https://example.com/icons/phone.png', TRUE),
    ('33333333-3333-3333-3333-333333333333', 'Giày dép', 'giay-dep', 'https://example.com/icons/shoes.png', TRUE);

INSERT INTO products (id, vendor_id, name, description, product_code, slug, category_id, status)
VALUES 
    ('44444444-4444-4444-4444-444444444444', 'c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b', 'Áo thun nam', 'Áo thun cotton thoáng mát', 'ATN001', 'ao-thun-nam', 
     '11111111-1111-1111-1111-111111111111', 'ACTIVE'),
    ('55555555-5555-5555-5555-555555555555', 'c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b', 'iPhone 15', 'Điện thoại Apple iPhone 15', 'IP15', 'iphone-15', 
     '22222222-2222-2222-2222-222222222222', 'ACTIVE');

INSERT INTO product_variants (id, product_id, sku, vendor_id, price, original_price, attributes, weight_gram)
VALUES 
    ('66666666-6666-6666-6666-666666666666', '44444444-4444-4444-4444-444444444444', 'ATN001-BLACK-M', 
     'c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b', 150000, 200000, '{"color":"black","size":"M"}', 250),
    ('77777777-7777-7777-7777-777777777777', '55555555-5555-5555-5555-555555555555', 'IP15-128GB-BLACK', 
     'c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b', 25000000, 27000000, '{"color":"black","storage":"128GB"}', 200);

INSERT INTO product_images (id, url, variant_id, is_main)
VALUES 
    ('88888888-8888-8888-8888-888888888888', 'https://example.com/images/ao-thun-den-m.jpg', 
     '66666666-6666-6666-6666-666666666666', TRUE),
    ('99999999-9999-9999-9999-999999999999', 'https://example.com/images/iphone15-black.jpg', 
     '77777777-7777-7777-7777-777777777777', TRUE);
