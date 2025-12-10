CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE fulfillment_orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_code VARCHAR(50) UNIQUE NOT NULL,
    vendor_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    external_ref VARCHAR(100),
    customer_name VARCHAR(255),
    customer_phone VARCHAR(20),
    shipping_address TEXT,
    source VARCHAR(30) NOT NULL DEFAULT 'MANUAL',
    status VARCHAR(30) NOT NULL DEFAULT 'CREATED',
    picked_at TIMESTAMP,
    packed_at TIMESTAMP,
    shipped_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE fulfillment_order_details (
    id BIGSERIAL PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES fulfillment_orders(id) ON DELETE CASCADE,
    product_variant_id UUID NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity_requested INTEGER NOT NULL,
    quantity_picked INTEGER,
    unit_price DECIMAL(14,2),
    notes TEXT
);

CREATE TABLE inbound_orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    inbound_code VARCHAR(50) UNIQUE NOT NULL,
    vendor_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    external_ref VARCHAR(100),
    supplier_name VARCHAR(255),
    expected_at DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    received_at TIMESTAMP,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vendor_status ON inbound_orders (vendor_id, status);
CREATE INDEX idx_warehouse_status ON inbound_orders (warehouse_id, status);
CREATE INDEX idx_inbound_code ON inbound_orders (inbound_code);

CREATE TABLE inbound_order_details (
    id BIGSERIAL PRIMARY KEY,
    inbound_order_id UUID NOT NULL REFERENCES inbound_orders(id) ON DELETE CASCADE,
    product_variant_id UUID NOT NULL,
    -- sku VARCHAR(100) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity_expected INTEGER NOT NULL,
    quantity_received INTEGER  DEFAULT 0,
    unit_price DECIMAL(14,2),
    notes TEXT
);


INSERT INTO fulfillment_orders (
    id, order_code, vendor_id, warehouse_id, external_ref,
    customer_name, customer_phone, shipping_address,
    source, status, created_by
) VALUES (
    uuid_generate_v4(), 'FO2512090001', 'c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b',
    '11111111-1111-1111-1111-111111111111', 'EXT-ORDER-001',
    'Nguyễn Văn A', '0901234567', '12 Láng Hạ, Hà Nội',
    'MANUAL', 'CREATED', 'c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b'
);

INSERT INTO fulfillment_order_details (
    order_id, product_variant_id, product_name, quantity_requested, unit_price, notes
) VALUES (
    (SELECT id FROM fulfillment_orders WHERE order_code = 'FO2512090001'),
    '66666666-6666-6666-6666-666666666666', 'Áo thun đen size M', 2, 150000, 'Khách đặt 2 cái'
);

INSERT INTO inbound_orders (
    id, inbound_code, vendor_id, warehouse_id, external_ref,
    supplier_name, expected_at, status, created_by
) VALUES (
    uuid_generate_v4(), 'IN2512090001', 'c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b',
    '11111111-1111-1111-1111-111111111111', 'SUP-ORDER-001',
    'Công ty May ABC', CURRENT_DATE, 'DRAFT',
    'c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b'
);

INSERT INTO inbound_order_details (
    inbound_order_id, product_variant_id, product_name, quantity_expected, unit_price, notes
) VALUES (
    (SELECT id FROM inbound_orders WHERE inbound_code = 'IN2512090001'),
    '66666666-6666-6666-6666-666666666666', 'Áo thun đen size M', 100, 120000, 'Nhập lô hàng mới'
);
