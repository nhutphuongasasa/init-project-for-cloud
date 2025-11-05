CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TYPE TENANT_STATUS AS ENUM ('active', 'inactive', 'suspended');
CREATE TYPE USER_STATUS AS ENUM ('active', 'inactive', 'suspended');
CREATE TYPE EMPLOYEE_STATUS AS ENUM ('active', 'inactive', 'suspended');    
CREATE TYPE USER_ROLE AS ENUM ('vendor', 'customer');
CREATE TYPE EMPLOYEE_ROLE AS ENUM ('admin', 'staff');

-- 1. Bảng: tenants mot khac hang hay 1 cai gi do 
CREATE TABLE tenants (
    tenant_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    slug VARCHAR(100) UNIQUE NOT NULL, 
    status TENANT_STATUS DEFAULT 'active',
    metadata JSONB DEFAULT '{}', 
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);

-- 2. Bảng: users userid lay tu keycloak
CREATE TABLE users (
    user_id UUID PRIMARY KEY, 
    full_name VARCHAR(255),
    avatar_url TEXT,
    is_active USER_STATUS DEFAULT 'active',
    last_login_at TIMESTAMPTZ,
    metadata JSONB DEFAULT '{}', 
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);

-- 3. Bảng: user_tenants_info table phu la nguoi dung cua tenant do 
CREATE TABLE user_tenants_info (
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    tenant_id UUID NOT NULL REFERENCES tenants(tenant_id) ON DELETE CASCADE,
    role USER_ROLE NOT NULL DEFAULT 'vendor', 
    status USER_STATUS DEFAULT 'active',
    invited_by UUID REFERENCES users(user_id),
    invited_at TIMESTAMPTZ,
    joined_at TIMESTAMPTZ,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (user_id, tenant_id)
);

-- 4. Bảng: user_profiles la thong tin mo rong cua user 
CREATE TABLE extra_user_profiles (
    user_id UUID NOT NULL REFERENCES users(user_id),
    bio TEXT,
    phone VARCHAR(20) UNIQUE,
    address JSONB,
    preferences JSONB DEFAULT '{}',
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (user_id)
);

--5: Bảng: employees la thong tin cua nhan vien la staff hoac admin
CREATE TABLE employees (
    employee_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    bio TEXT,
    phone VARCHAR(20) UNIQUE,
    address JSONB,
    preferences JSONB DEFAULT '{}',
    role EMPLOYEE_ROLE NOT NULL DEFAULT 'staff', 
    status EMPLOYEE_STATUS DEFAULT 'active',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);

--tenant
CREATE UNIQUE INDEX idx_tenants_slug ON tenants(slug);


-- 1️⃣ Tenants
INSERT INTO tenants (name, description, slug, status)
VALUES
('Tenant A', 'Description for Tenant A', 'tenant-a', 'active'::TENANT_STATUS),
('Tenant B', 'Description for Tenant B', 'tenant-b', 'active'::TENANT_STATUS),
('Tenant C', 'Description for Tenant C', 'tenant-c', 'active'::TENANT_STATUS),
('Tenant D', 'Description for Tenant D', 'tenant-d', 'inactive'::TENANT_STATUS),
('Tenant E', 'Description for Tenant E', 'tenant-e', 'active'::TENANT_STATUS),
('Tenant F', 'Description for Tenant F', 'tenant-f', 'suspended'::TENANT_STATUS),
('Tenant G', 'Description for Tenant G', 'tenant-g', 'active'::TENANT_STATUS),
('Tenant H', 'Description for Tenant H', 'tenant-h', 'inactive'::TENANT_STATUS),
('Tenant I', 'Description for Tenant I', 'tenant-i', 'active'::TENANT_STATUS),
('Tenant J', 'Description for Tenant J', 'tenant-j', 'active'::TENANT_STATUS);

-- 2️⃣ Users
INSERT INTO users (user_id, full_name, avatar_url, is_active)
VALUES
(uuid_generate_v4(), 'Alice Nguyen', 'https://example.com/avatar1.png', 'active'::USER_STATUS),
(uuid_generate_v4(), 'Bob Tran', 'https://example.com/avatar2.png', 'active'::USER_STATUS),
(uuid_generate_v4(), 'Charlie Pham', 'https://example.com/avatar3.png', 'active'::USER_STATUS),
(uuid_generate_v4(), 'David Le', 'https://example.com/avatar4.png', 'inactive'::USER_STATUS),
(uuid_generate_v4(), 'Eva Hoang', 'https://example.com/avatar5.png', 'active'::USER_STATUS),
(uuid_generate_v4(), 'Frank Dang', 'https://example.com/avatar6.png', 'suspended'::USER_STATUS),
(uuid_generate_v4(), 'Grace Vu', 'https://example.com/avatar7.png', 'active'::USER_STATUS),
(uuid_generate_v4(), 'Hannah Bui', 'https://example.com/avatar8.png', 'active'::USER_STATUS),
(uuid_generate_v4(), 'Ian Phan', 'https://example.com/avatar9.png', 'active'::USER_STATUS),
(uuid_generate_v4(), 'Jane Ngo', 'https://example.com/avatar10.png', 'active'::USER_STATUS);

-- 3️⃣ User Tenants Info
INSERT INTO user_tenants_info (user_id, tenant_id, role, status)
SELECT u.user_id, t.tenant_id,
       CASE WHEN row_number() OVER () % 2 = 0 THEN 'vendor'::USER_ROLE ELSE 'customer'::USER_ROLE END,
       'active'::USER_STATUS
FROM users u CROSS JOIN tenants t
LIMIT 10;

-- 4️⃣ Extra User Profiles
INSERT INTO extra_user_profiles (user_id, bio, phone, address)
SELECT user_id,
       'Bio for ' || full_name,
       '+84' || lpad((trunc(random()*1000000000)::text),9,'0'),
       '{"city": "Hanoi", "country": "Vietnam"}'::jsonb
FROM users
LIMIT 10;

-- 5️⃣ Employees
INSERT INTO employees (bio, phone, address, role, status)
VALUES
('Bio Alice', '+84900000001', '{"city":"Hanoi"}'::jsonb, 'admin'::EMPLOYEE_ROLE, 'active'::EMPLOYEE_STATUS),
('Bio Bob', '+84900000002', '{"city":"HCMC"}'::jsonb, 'staff'::EMPLOYEE_ROLE, 'active'::EMPLOYEE_STATUS),
('Bio Charlie', '+84900000003', '{"city":"Danang"}'::jsonb, 'staff'::EMPLOYEE_ROLE, 'active'::EMPLOYEE_STATUS),
('Bio David', '+84900000004', '{"city":"Hue"}'::jsonb, 'admin'::EMPLOYEE_ROLE, 'active'::EMPLOYEE_STATUS),
('Bio Eva', '+84900000005', '{"city":"Hanoi"}'::jsonb, 'staff'::EMPLOYEE_ROLE, 'active'::EMPLOYEE_STATUS),
('Bio Frank', '+84900000006', '{"city":"HCMC"}'::jsonb, 'staff'::EMPLOYEE_ROLE, 'suspended'::EMPLOYEE_STATUS),
('Bio Grace', '+84900000007', '{"city":"Danang"}'::jsonb, 'staff'::EMPLOYEE_ROLE, 'active'::EMPLOYEE_STATUS),
('Bio Hannah', '+84900000008', '{"city":"Hue"}'::jsonb, 'staff'::EMPLOYEE_ROLE, 'active'::EMPLOYEE_STATUS),
('Bio Ian', '+84900000009', '{"city":"Hanoi"}'::jsonb, 'admin'::EMPLOYEE_ROLE, 'inactive'::EMPLOYEE_STATUS),
('Bio Jane', '+84900000010', '{"city":"HCMC"}'::jsonb, 'staff'::EMPLOYEE_ROLE, 'active'::EMPLOYEE_STATUS);