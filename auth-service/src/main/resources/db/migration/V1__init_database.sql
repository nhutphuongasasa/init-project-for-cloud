CREATE TABLE users (
    id UUID PRIMARY KEY,
    provider VARCHAR(50) NOT NULL,                  
    provider_id VARCHAR(255) NOT NULL,              
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

CREATE TABLE roles (
    id UUID PRIMARY KEY ,
    vendor_id UUID,
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

CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,      
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE role_permissions (
    role_id UUID REFERENCES roles(id) ON DELETE CASCADE,
    permission_id UUID REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE user_vendor_access (
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    vendor_id UUID NOT NULL, 
    role_id UUID REFERENCES roles(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, vendor_id)
);

CREATE INDEX idx_uva_user_id ON user_vendor_access(user_id);

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
    attributes text DEFAULT NULL, 
    state varchar(500) DEFAULT NULL,
    
    authorization_code_value bytea DEFAULT NULL, 
    authorization_code_issued_at timestamp DEFAULT NULL,
    authorization_code_expires_at timestamp DEFAULT NULL,
    authorization_code_metadata text DEFAULT NULL, 
    
    access_token_value bytea DEFAULT NULL,
    access_token_issued_at timestamp DEFAULT NULL,
    access_token_expires_at timestamp DEFAULT NULL,
    access_token_metadata text DEFAULT NULL, 
    access_token_type varchar(100) DEFAULT NULL,
    access_token_scopes varchar(1000) DEFAULT NULL,
    
    oidc_id_token_value bytea DEFAULT NULL,
    oidc_id_token_issued_at timestamp DEFAULT NULL,
    oidc_id_token_expires_at timestamp DEFAULT NULL,
    oidc_id_token_metadata text DEFAULT NULL,
    
    refresh_token_value bytea DEFAULT NULL,
    refresh_token_issued_at timestamp DEFAULT NULL,
    refresh_token_expires_at timestamp DEFAULT NULL,
    refresh_token_metadata text DEFAULT NULL,
    
    user_code_value bytea DEFAULT NULL,
    user_code_issued_at timestamp DEFAULT NULL,
    user_code_expires_at timestamp DEFAULT NULL,
    user_code_metadata text DEFAULT NULL, 
    
    device_code_value bytea DEFAULT NULL,
    device_code_issued_at timestamp DEFAULT NULL,
    device_code_expires_at timestamp DEFAULT NULL,
    device_code_metadata text DEFAULT NULL, 
    
    PRIMARY KEY (id)
);

CREATE TABLE jwt_keys (
    kid UUID PRIMARY KEY, 
    private_key TEXT NOT NULL,     
    public_key TEXT NOT NULL,      
    algorithm VARCHAR(20) DEFAULT 'RS256',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

INSERT INTO roles (id, code, name, description) VALUES 
(gen_random_uuid(), 'SYS_ADMIN', 'System Administrator', 'Platform-wide administrator with full access to all vendors'),
(gen_random_uuid(), 'VENDOR_OWNER', 'Vendor Owner', 'The primary account holder for a specific vendor with full control'),
(gen_random_uuid(), 'VENDOR_MANAGER', 'Vendor Manager', 'Can manage products, stock, and orders but cannot manage billing or members'),
(gen_random_uuid(), 'VENDOR_STAFF', 'Vendor Staff', 'Operational role for handling day-to-day warehouse and order tasks');

INSERT INTO permissions (id, code, name, description) VALUES 
(gen_random_uuid(), '*:*', 'Full Access', 'Ultimate authority across all services'),

(gen_random_uuid(), 'vendor:*', 'Full Vendor Management', 'Complete control over vendor profile and members'),
(gen_random_uuid(), 'vendor:view', 'View Vendor', 'Permission to view organization and store details'),
(gen_random_uuid(), 'vendor:update', 'Update Vendor', 'Permission to edit store information'),
(gen_random_uuid(), 'vendor:manage_members', 'Manage Members', 'Permission to invite, remove, or change member roles'),

(gen_random_uuid(), 'product:*', 'Full Product Management', 'Complete control over product catalog'),
(gen_random_uuid(), 'product:view', 'View Products', 'Permission to browse and view product details'),
(gen_random_uuid(), 'product:create', 'Create Product', 'Permission to add new products'),
(gen_random_uuid(), 'product:update', 'Update Product', 'Permission to edit existing products'),
(gen_random_uuid(), 'product:delete', 'Delete Product', 'Permission to remove products from the system'),

(gen_random_uuid(), 'inventory:*', 'Full Inventory Management', 'Complete control over stock and warehouses'),
(gen_random_uuid(), 'inventory:view', 'View Inventory', 'Permission to view stock levels and locations'),
(gen_random_uuid(), 'inventory:adjust', 'Adjust Stock', 'Permission to perform stock counts and adjustments'),
(gen_random_uuid(), 'inventory:transfer', 'Transfer Stock', 'Permission to move goods between locations/warehouses'),

(gen_random_uuid(), 'order:*', 'Full Order Management', 'Complete control over order lifecycle'),
(gen_random_uuid(), 'order:view', 'View Orders', 'Permission to view order lists and details'),
(gen_random_uuid(), 'order:create', 'Create Order', 'Permission to generate new orders'),
(gen_random_uuid(), 'order:update', 'Process Order', 'Permission to approve, ship, or cancel orders'),
(gen_random_uuid(), 'order:report', 'View Reports', 'Permission to access sales and operational analytics');

INSERT INTO role_permissions (role_id, permission_id)
SELECT (SELECT id FROM roles WHERE code = 'SYS_ADMIN'), id 
FROM permissions 
WHERE code = '*:*';

INSERT INTO role_permissions (role_id, permission_id)
SELECT (SELECT id FROM roles WHERE code = 'VENDOR_OWNER'), id 
FROM permissions 
WHERE code IN ('vendor:*', 'product:*', 'inventory:*', 'order:*');

INSERT INTO role_permissions (role_id, permission_id)
SELECT (SELECT id FROM roles WHERE code = 'VENDOR_MANAGER'), id 
FROM permissions 
WHERE code IN ('vendor:view', 'product:*', 'inventory:*', 'order:*');

INSERT INTO role_permissions (role_id, permission_id)
SELECT (SELECT id FROM roles WHERE code = 'VENDOR_STAFF'), id 
FROM permissions 
WHERE code IN (
    'product:view', 
    'inventory:view', 
    'inventory:adjust', 
    'order:view', 
    'order:update'
);