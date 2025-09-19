-- Sweet Shop Management System - Database Setup Script
-- Run this script in your PostgreSQL database after deployment

-- Create database (if not exists)
-- CREATE DATABASE sweetshop;

-- Connect to the database
-- \c sweetshop;

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    role VARCHAR(20) DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create sweets table
CREATE TABLE IF NOT EXISTS sweets (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    category VARCHAR(50) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    image_url VARCHAR(255),
    is_available BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create cart_items table
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    sweet_id BIGINT NOT NULL REFERENCES sweets(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, sweet_id)
);

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    shipping_address TEXT NOT NULL,
    payment_method VARCHAR(50),
    payment_status VARCHAR(20) DEFAULT 'PENDING' CHECK (payment_status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED')),
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivery_date TIMESTAMP,
    tracking_number VARCHAR(100),
    notes TEXT
);

-- Create order_items table
CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    sweet_id BIGINT NOT NULL REFERENCES sweets(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create suppliers table
CREATE TABLE IF NOT EXISTS suppliers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create inventory_history table
CREATE TABLE IF NOT EXISTS inventory_history (
    id BIGSERIAL PRIMARY KEY,
    sweet_id BIGINT NOT NULL REFERENCES sweets(id) ON DELETE CASCADE,
    supplier_id BIGINT REFERENCES suppliers(id) ON DELETE SET NULL,
    change_type VARCHAR(20) NOT NULL CHECK (change_type IN ('IN', 'OUT', 'ADJUSTMENT')),
    quantity_change INTEGER NOT NULL,
    previous_quantity INTEGER NOT NULL,
    new_quantity INTEGER NOT NULL,
    reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create analytics tables
CREATE TABLE IF NOT EXISTS monthly_sales (
    id BIGSERIAL PRIMARY KEY,
    year INTEGER NOT NULL,
    month INTEGER NOT NULL CHECK (month >= 1 AND month <= 12),
    total_revenue DECIMAL(12,2) DEFAULT 0,
    total_orders INTEGER DEFAULT 0,
    total_items_sold INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(year, month)
);

CREATE TABLE IF NOT EXISTS category_sales (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(50) NOT NULL,
    year INTEGER NOT NULL,
    month INTEGER NOT NULL CHECK (month >= 1 AND month <= 12),
    revenue DECIMAL(12,2) DEFAULT 0,
    quantity_sold INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(category, year, month)
);

CREATE TABLE IF NOT EXISTS customer_analytics (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    total_orders INTEGER DEFAULT 0,
    total_spent DECIMAL(12,2) DEFAULT 0,
    average_order_value DECIMAL(10,2) DEFAULT 0,
    last_order_date TIMESTAMP,
    customer_segment VARCHAR(50) DEFAULT 'NEW',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS product_analytics (
    id BIGSERIAL PRIMARY KEY,
    sweet_id BIGINT NOT NULL REFERENCES sweets(id) ON DELETE CASCADE,
    views INTEGER DEFAULT 0,
    cart_adds INTEGER DEFAULT 0,
    purchases INTEGER DEFAULT 0,
    revenue DECIMAL(12,2) DEFAULT 0,
    conversion_rate DECIMAL(5,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS website_traffic (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL UNIQUE,
    page_views INTEGER DEFAULT 0,
    unique_visitors INTEGER DEFAULT 0,
    bounce_rate DECIMAL(5,2) DEFAULT 0,
    session_duration DECIMAL(8,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS conversion_events (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    event_type VARCHAR(50) NOT NULL,
    event_data JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS shipping_analytics (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    shipping_method VARCHAR(50),
    shipping_cost DECIMAL(8,2) DEFAULT 0,
    delivery_time_days INTEGER,
    delivery_status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS system_alerts (
    id BIGSERIAL PRIMARY KEY,
    alert_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) DEFAULT 'INFO' CHECK (severity IN ('INFO', 'WARNING', 'ERROR', 'CRITICAL')),
    message TEXT NOT NULL,
    is_resolved BOOLEAN DEFAULT false,
    resolved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS customer_segments (
    id BIGSERIAL PRIMARY KEY,
    segment_name VARCHAR(50) NOT NULL UNIQUE,
    criteria JSONB NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_analytics (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    funnel_stage VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_sweets_category ON sweets(category);
CREATE INDEX IF NOT EXISTS idx_sweets_available ON sweets(is_available);
CREATE INDEX IF NOT EXISTS idx_cart_items_user_id ON cart_items(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_date ON orders(order_date);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_inventory_history_sweet_id ON inventory_history(sweet_id);
CREATE INDEX IF NOT EXISTS idx_monthly_sales_year_month ON monthly_sales(year, month);
CREATE INDEX IF NOT EXISTS idx_category_sales_category ON category_sales(category);
CREATE INDEX IF NOT EXISTS idx_customer_analytics_user_id ON customer_analytics(user_id);
CREATE INDEX IF NOT EXISTS idx_product_analytics_sweet_id ON product_analytics(sweet_id);
CREATE INDEX IF NOT EXISTS idx_website_traffic_date ON website_traffic(date);
CREATE INDEX IF NOT EXISTS idx_conversion_events_type ON conversion_events(event_type);
CREATE INDEX IF NOT EXISTS idx_system_alerts_resolved ON system_alerts(is_resolved);

-- Insert sample data
INSERT INTO users (username, email, password, first_name, last_name, role) VALUES
('admin', 'admin@sweetshop.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Admin', 'User', 'ADMIN'),
('user', 'user@sweetshop.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Regular', 'User', 'USER')
ON CONFLICT (email) DO NOTHING;

INSERT INTO sweets (name, description, price, category, stock_quantity, image_url) VALUES
('Gulab Jamun', 'Soft and spongy milk-based sweet', 25.00, 'Traditional', 100, 'https://example.com/gulab-jamun.jpg'),
('Rasgulla', 'Spongy cottage cheese balls in sugar syrup', 20.00, 'Traditional', 80, 'https://example.com/rasgulla.jpg'),
('Kaju Katli', 'Cashew fudge with silver leaf', 45.00, 'Premium', 50, 'https://example.com/kaju-katli.jpg'),
('Ladoo', 'Round sweet made with gram flour', 15.00, 'Traditional', 120, 'https://example.com/ladoo.jpg'),
('Barfi', 'Milk-based fudge with nuts', 30.00, 'Traditional', 75, 'https://example.com/barfi.jpg'),
('Jalebi', 'Crispy spiral sweet in sugar syrup', 12.00, 'Fried', 60, 'https://example.com/jalebi.jpg'),
('Kheer', 'Rice pudding with dry fruits', 35.00, 'Dessert', 40, 'https://example.com/kheer.jpg'),
('Halwa', 'Semolina pudding with ghee', 28.00, 'Traditional', 55, 'https://example.com/halwa.jpg')
ON CONFLICT DO NOTHING;

INSERT INTO suppliers (name, contact_person, email, phone, address) VALUES
('Sweet Suppliers Ltd', 'John Doe', 'john@sweetsuppliers.com', '+91-9876543210', '123 Sweet Street, Mumbai'),
('Traditional Sweets Co', 'Jane Smith', 'jane@traditionalsweets.com', '+91-9876543211', '456 Traditional Road, Delhi'),
('Premium Confectionery', 'Mike Johnson', 'mike@premiumconfectionery.com', '+91-9876543212', '789 Premium Avenue, Bangalore')
ON CONFLICT DO NOTHING;

-- Insert sample analytics data
INSERT INTO monthly_sales (year, month, total_revenue, total_orders, total_items_sold) VALUES
(2024, 1, 15000.00, 120, 300),
(2024, 2, 18000.00, 150, 380),
(2024, 3, 22000.00, 180, 450),
(2024, 4, 19000.00, 160, 400),
(2024, 5, 25000.00, 200, 500),
(2024, 6, 28000.00, 220, 550),
(2024, 7, 30000.00, 240, 600),
(2024, 8, 32000.00, 260, 650),
(2024, 9, 35000.00, 280, 700)
ON CONFLICT (year, month) DO NOTHING;

INSERT INTO category_sales (category, year, month, revenue, quantity_sold) VALUES
('Traditional', 2024, 9, 21000.00, 420),
('Premium', 2024, 9, 8000.00, 160),
('Fried', 2024, 9, 3000.00, 60),
('Dessert', 2024, 9, 3000.00, 60)
ON CONFLICT (category, year, month) DO NOTHING;

-- Insert sample system alerts
INSERT INTO system_alerts (alert_type, severity, message) VALUES
('LOW_STOCK', 'WARNING', 'Kaju Katli stock is low (5 items remaining)'),
('HIGH_DEMAND', 'INFO', 'Gulab Jamun has high demand today'),
('SYSTEM_HEALTH', 'INFO', 'All systems running normally')
ON CONFLICT DO NOTHING;

-- Create triggers for updated_at timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_sweets_updated_at BEFORE UPDATE ON sweets FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_cart_items_updated_at BEFORE UPDATE ON cart_items FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_suppliers_updated_at BEFORE UPDATE ON suppliers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_monthly_sales_updated_at BEFORE UPDATE ON monthly_sales FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_category_sales_updated_at BEFORE UPDATE ON category_sales FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_customer_analytics_updated_at BEFORE UPDATE ON customer_analytics FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_product_analytics_updated_at BEFORE UPDATE ON product_analytics FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_website_traffic_updated_at BEFORE UPDATE ON website_traffic FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

COMMIT;
