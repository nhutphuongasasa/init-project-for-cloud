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
    failure_reason VARCHAR(50),
    failure_message VARCHAR(255),
    failure_step VARCHAR(50),
    failed_at TIMESTAMP,
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
    'aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1', -- UUID cố định
    'FO2512090002', 'e198f49f-0887-4566-9d65-c269fb1e264f',
    '11111111-1111-1111-1111-111111111111', 'EXT-ORDER-002',
    'Nguyễn Văn B', '0912345678', '123 Trần Duy Hưng, Hà Nội',
    'MANUAL', 'CREATED', 'c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b'
);

INSERT INTO fulfillment_order_details (
    order_id, product_variant_id, product_name, quantity_requested, unit_price, notes
) VALUES (
    'aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1',
    '66666666-6666-6666-6666-666666666666', -- Áo thun đen size M
    'Áo thun nam - đen size M', 2, 150000, 'Khách đặt 2 cái'
);

INSERT INTO inbound_orders (
    id, inbound_code, vendor_id, warehouse_id, external_ref,
    supplier_name, expected_at, status, created_by
) VALUES (
    'bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbb1',
    'IN2512090002', 'e198f49f-0887-4566-9d65-c269fb1e264f',
    '22222222-2222-2222-2222-222222222222', 'SUP-ORDER-002',
    'Apple Vietnam', CURRENT_DATE, 'DRAFT',
    'e198f49f-0887-4566-9d65-c269fb1e264f'
);

INSERT INTO inbound_order_details (
    inbound_order_id, product_variant_id, product_name, quantity_expected, unit_price, notes
) VALUES (
    'bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbb1',
    '77777777-7777-7777-7777-777777777777', -- iPhone 15 128GB Black
    'iPhone 15 128GB Black', 50, 25000000, 'Nhập lô hàng iPhone mới'
);
