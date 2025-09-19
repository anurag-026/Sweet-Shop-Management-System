-- Database initialization script
-- This script creates the database if it doesn't exist

-- Connect to postgres database first
\c postgres;

-- Create database if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'sweetshop') THEN
        CREATE DATABASE sweetshop;
        RAISE NOTICE 'Database sweetshop created successfully';
    ELSE
        RAISE NOTICE 'Database sweetshop already exists';
    END IF;
END
$$;

-- Connect to the sweetshop database
\c sweetshop;

-- Create a dedicated user for the application (optional)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'sweetshop_user') THEN
        CREATE USER sweetshop_user WITH PASSWORD 'sweetshop_password';
        GRANT ALL PRIVILEGES ON DATABASE sweetshop TO sweetshop_user;
        RAISE NOTICE 'User sweetshop_user created successfully';
    ELSE
        RAISE NOTICE 'User sweetshop_user already exists';
    END IF;
END
$$;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address TEXT,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER'
);

-- Create sweets table
CREATE TABLE IF NOT EXISTS sweets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(100),
    image_url VARCHAR(500),
    stock_quantity INTEGER DEFAULT 0,
    is_available BOOLEAN DEFAULT true
);

-- Create cart_items table
CREATE TABLE IF NOT EXISTS cart_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    sweet_id UUID NOT NULL REFERENCES sweets(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, sweet_id)
);

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create order_items table
CREATE TABLE IF NOT EXISTS order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    sweet_id UUID NOT NULL REFERENCES sweets(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

-- Grant necessary privileges
GRANT ALL PRIVILEGES ON SCHEMA public TO sweetshop_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO sweetshop_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO sweetshop_user;
