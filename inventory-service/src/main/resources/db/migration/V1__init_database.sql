
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE warehouses (
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code       VARCHAR(50) UNIQUE NOT NULL,
    name       VARCHAR(255) NOT NULL,
    address    TEXT,
    is_active  BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inventory_items (
    product_variant_id UUID NOT NULL,
    warehouse_id       UUID NOT NULL,
    vendor_id          UUID NOT NULL,
    quantity_available INT DEFAULT 0,
    quantity_reserved  INT DEFAULT 0,
    safety_stock       INT DEFAULT 10,
    last_updated       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (product_variant_id, warehouse_id),
    CONSTRAINT fk_inventory_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE RESTRICT
);

CREATE INDEX idx_inventory_vendor ON inventory_items(vendor_id);
CREATE INDEX idx_inventory_warehouse ON inventory_items(warehouse_id);

CREATE TABLE stock_movements (
    id                 UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_variant_id UUID NOT NULL,
    warehouse_id       UUID NOT NULL,
    vendor_id          UUID NOT NULL,
    type               VARCHAR(30) NOT NULL,
    quantity           INT NOT NULL,
    reference_type     VARCHAR(50),
    notes              TEXT,
    created_by         VARCHAR(100),
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_stock_sku_date ON stock_movements(product_variant_id, created_at);
CREATE INDEX idx_stock_vendor ON stock_movements(vendor_id);
CREATE INDEX idx_stock_type ON stock_movements(type);

INSERT INTO warehouses (id, code, name, address)
VALUES 
    ('11111111-1111-1111-1111-111111111111', 'WH001', 'Kho Hà Nội', '123 Trần Duy Hưng, Hà Nội'),
    ('22222222-2222-2222-2222-222222222222', 'WH002', 'Kho Sài Gòn', '456 Nguyễn Huệ, TP.HCM');

INSERT INTO inventory_items (product_variant_id, warehouse_id, vendor_id, quantity_available, quantity_reserved, safety_stock)
VALUES 
('66666666-6666-6666-6666-666666666666', '11111111-1111-1111-1111-111111111111', 'c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b', 100, 0, 10);

INSERT INTO inventory_items (product_variant_id, warehouse_id, vendor_id, quantity_available, quantity_reserved, safety_stock)
VALUES 
('77777777-7777-7777-7777-777777777777', '22222222-2222-2222-2222-222222222222', 'c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b', 50, 0, 5);

INSERT INTO stock_movements (id, product_variant_id, warehouse_id, vendor_id, type, quantity, reference_type, notes, created_by)
VALUES 
(uuid_generate_v4(), '66666666-6666-6666-6666-666666666666', '11111111-1111-1111-1111-111111111111', 'c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b',
 'INBOUND', 100, 'IMPORT', 'Nhập hàng áo thun đen size M', 'system');

INSERT INTO stock_movements (id, product_variant_id, warehouse_id, vendor_id, type, quantity, reference_type, notes, created_by)
VALUES 
(uuid_generate_v4(), '77777777-7777-7777-7777-777777777777', '22222222-2222-2222-2222-222222222222', 'c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b',
 'INBOUND', 50, 'IMPORT', 'Nhập hàng iPhone 15 128GB', 'system');
