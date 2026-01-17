CREATE TABLE users (
    id UUID PRIMARY KEY,
    provider VARCHAR(50) NOT NULL,                  -- 'google', 'facebook', 'apple', 'microsoft', etc.
    provider_id VARCHAR(255) NOT NULL,              -- ID từ provider
    email VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    avatar_url VARCHAR(500),
    email_verified BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(provider, provider_id),
    UNIQUE(email)                                  
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_provider ON users(provider, provider_id);

CREATE TABLE roles (
    id UUID PRIMARY KEY ,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    description TEXT
);

CREATE TABLE user_roles (
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE oauth2_registered_client (
    id varchar(100) NOT NULL,
    client_id varchar(100) NOT NULL,
    client_id_issued_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret varchar(200) DEFAULT NULL,
    client_secret_expires_at timestamp DEFAULT NULL,
    client_name varchar(200) NOT NULL,
    client_authentication_methods varchar(1000) NOT NULL,
    authorization_grant_types varchar(1000) NOT NULL,
    redirect_uris varchar(1000) DEFAULT NULL,
    post_logout_redirect_uris varchar(1000) DEFAULT NULL,
    scopes varchar(1000) NOT NULL,
    client_settings varchar(2000) NOT NULL,
    token_settings varchar(2000) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE oauth2_authorization (
    id varchar(100) NOT NULL,
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorization_grant_type varchar(100) NOT NULL,
    authorized_scopes varchar(1000) DEFAULT NULL,
    attributes text DEFAULT NULL, -- Đổi từ blob sang text (lưu JSON)
    state varchar(500) DEFAULT NULL,
    
    -- Các giá trị token thực tế dùng BYTEA để lưu dữ liệu nhị phân mã hóa
    authorization_code_value bytea DEFAULT NULL, 
    authorization_code_issued_at timestamp DEFAULT NULL,
    authorization_code_expires_at timestamp DEFAULT NULL,
    authorization_code_metadata text DEFAULT NULL, -- Đổi sang text
    
    access_token_value bytea DEFAULT NULL,
    access_token_issued_at timestamp DEFAULT NULL,
    access_token_expires_at timestamp DEFAULT NULL,
    access_token_metadata text DEFAULT NULL, -- Đổi sang text
    access_token_type varchar(100) DEFAULT NULL,
    access_token_scopes varchar(1000) DEFAULT NULL,
    
    oidc_id_token_value bytea DEFAULT NULL,
    oidc_id_token_issued_at timestamp DEFAULT NULL,
    oidc_id_token_expires_at timestamp DEFAULT NULL,
    oidc_id_token_metadata text DEFAULT NULL, -- Đổi sang text
    
    refresh_token_value bytea DEFAULT NULL,
    refresh_token_issued_at timestamp DEFAULT NULL,
    refresh_token_expires_at timestamp DEFAULT NULL,
    refresh_token_metadata text DEFAULT NULL, -- Đổi sang text
    
    user_code_value bytea DEFAULT NULL,
    user_code_issued_at timestamp DEFAULT NULL,
    user_code_expires_at timestamp DEFAULT NULL,
    user_code_metadata text DEFAULT NULL, -- Đổi sang text
    
    device_code_value bytea DEFAULT NULL,
    device_code_issued_at timestamp DEFAULT NULL,
    device_code_expires_at timestamp DEFAULT NULL,
    device_code_metadata text DEFAULT NULL, -- Đổi sang text
    
    PRIMARY KEY (id)
);

CREATE TABLE jwt_keys (
    kid VARCHAR(100) PRIMARY KEY, -- Key ID (định danh duy nhất cho mỗi cặp khóa)
    private_key TEXT NOT NULL,     -- Lưu dưới dạng PEM hoặc Base64
    public_key TEXT NOT NULL,      -- Lưu dưới dạng PEM hoặc Base64
    algorithm VARCHAR(20) DEFAULT 'RS256',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE -- Đánh dấu khóa nào đang dùng để ký mới
);

-- 1. Nạp Role
INSERT INTO roles (id, code, name, description) VALUES 
(gen_random_uuid(), 'ROLE_ADMIN', 'Administrator', 'Quản trị toàn hệ thống'),
(gen_random_uuid(), 'ROLE_USER', 'User', 'Người dùng chính thức'),
(gen_random_uuid(), 'ROLE_GUEST', 'Guest', 'Người dùng chưa định danh hoặc khách');

-- 2. Đăng ký các Client (Bao gồm Microservices và UI)
INSERT INTO oauth2_registered_client (
    id, client_id, client_id_issued_at, client_secret, client_name, 
    client_authentication_methods, authorization_grant_types, 
    redirect_uris, scopes, client_settings, token_settings
) VALUES 
(
    'vendor-id', 'vendor-service', CURRENT_TIMESTAMP, 
    '{bcrypt}$2a$10$7R6C.I3VlR6B8p2.L3v7k.eM7XvVv5Y/Q7.9uX0n4zI0e2l/K6X7y', 
    'Vendor Service Client', 'client_secret_basic', 'client_credentials', 
    NULL, 'read,write', 
    '{"@class":"java.util.Collections$UnmodifiableMap"}', '{"@class":"java.util.Collections$UnmodifiableMap"}'
),
(
    'inventory-id', 'inventory-service', CURRENT_TIMESTAMP, 
    '{bcrypt}$2a$10$7R6C.I3VlR6B8p2.L3v7k.eM7XvVv5Y/Q7.9uX0n4zI0e2l/K6X7y', 
    'Inventory Service Client', 'client_secret_basic', 'client_credentials', 
    NULL, 'read,write', 
    '{"@class":"java.util.Collections$UnmodifiableMap"}', '{"@class":"java.util.Collections$UnmodifiableMap"}'
),
(
    'order-id', 'order-service', CURRENT_TIMESTAMP, 
    '{bcrypt}$2a$10$7R6C.I3VlR6B8p2.L3v7k.eM7XvVv5Y/Q7.9uX0n4zI0e2l/K6X7y', 
    'Order Service Client', 'client_secret_basic', 'client_credentials', 
    NULL, 'read,write', 
    '{"@class":"java.util.Collections$UnmodifiableMap"}', '{"@class":"java.util.Collections$UnmodifiableMap"}'
),
(
    'product-id', 'product-service', CURRENT_TIMESTAMP, 
    '{bcrypt}$2a$10$7R6C.I3VlR6B8p2.L3v7k.eM7XvVv5Y/Q7.9uX0n4zI0e2l/K6X7y', 
    'Product Service Client', 'client_secret_basic', 'client_credentials', 
    NULL, 'read,write', 
    '{"@class":"java.util.Collections$UnmodifiableMap"}', '{"@class":"java.util.Collections$UnmodifiableMap"}'
);

INSERT INTO oauth2_registered_client (
    id, client_id, client_id_issued_at, client_secret, 
    client_name, client_authentication_methods, authorization_grant_types, 
    redirect_uris, scopes, client_settings, token_settings
) VALUES (
    'warehouse-ui-id', 
    'warehouse-client', 
    CURRENT_TIMESTAMP, 
    '{bcrypt}$2a$10$7R6C.I3VlR6B8p2.L3v7k.eM7XvVv5Y/Q7.9uX0n4zI0e2l/K6X7y', 
    'Warehouse NextJS UI', 
    'client_secret_basic', 
    'authorization_code,refresh_token,client_credentials', 
    'http://localhost:3000/auth/callback', 
    'openid,profile,email', 
    -- QUAN TRỌNG: Đã đổi sang FALSE ở dưới đây
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}', 
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.access-token-time-to-live":["java.time.Duration",3600.0],"settings.token.reuse-refresh-tokens":true}'
);