-- V3__add_analytics_tables.sql
-- Add analytics-related tables and columns to support proper analytics

-- 1. Enhance Sweet table with additional fields for analytics
ALTER TABLE sweets ADD COLUMN IF NOT EXISTS cost DECIMAL(10,2);
ALTER TABLE sweets ADD COLUMN IF NOT EXISTS profit_margin DECIMAL(5,2);
ALTER TABLE sweets ADD COLUMN IF NOT EXISTS supplier_id UUID;
ALTER TABLE sweets ADD COLUMN IF NOT EXISTS min_threshold INTEGER DEFAULT 10;
ALTER TABLE sweets ADD COLUMN IF NOT EXISTS last_restocked TIMESTAMP;

-- 2. Create Supplier table
CREATE TABLE IF NOT EXISTS suppliers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(50),
    address TEXT,
    website VARCHAR(255),
    notes TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add foreign key constraint to Sweet table
ALTER TABLE sweets ADD CONSTRAINT fk_sweet_supplier 
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE SET NULL;

-- 3. Create Product Analytics table
CREATE TABLE IF NOT EXISTS product_analytics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sweet_id UUID NOT NULL REFERENCES sweets(id) ON DELETE CASCADE,
    units_sold INTEGER DEFAULT 0,
    revenue DECIMAL(12,2) DEFAULT 0,
    profit DECIMAL(12,2) DEFAULT 0,
    view_count INTEGER DEFAULT 0,
    add_to_cart_count INTEGER DEFAULT 0,
    conversion_rate DECIMAL(5,2) DEFAULT 0,
    time_period VARCHAR(10) NOT NULL, -- 'daily', 'weekly', 'monthly', 'yearly'
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(sweet_id, time_period, period_start)
);

-- 4. Create Customer Analytics table
CREATE TABLE IF NOT EXISTS customer_segments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    segment_name VARCHAR(100) NOT NULL,
    description TEXT,
    min_order_value DECIMAL(10,2),
    min_order_frequency INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(segment_name)
);

CREATE TABLE IF NOT EXISTS customer_analytics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    segment_id UUID REFERENCES customer_segments(id) ON DELETE SET NULL,
    total_orders INTEGER DEFAULT 0,
    total_spent DECIMAL(12,2) DEFAULT 0,
    avg_order_value DECIMAL(10,2) DEFAULT 0,
    first_order_date TIMESTAMP,
    last_order_date TIMESTAMP,
    order_frequency_days DECIMAL(6,2),
    is_returning BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id)
);

-- 5. Create Website Traffic table
CREATE TABLE IF NOT EXISTS website_traffic (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id VARCHAR(255) NOT NULL,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    ip_address VARCHAR(50),
    user_agent TEXT,
    referrer VARCHAR(255),
    landing_page VARCHAR(255),
    exit_page VARCHAR(255),
    page_views INTEGER DEFAULT 1,
    session_duration_seconds INTEGER,
    is_bounce BOOLEAN DEFAULT false,
    visit_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(session_id)
);

-- 6. Create Conversion Funnel table
CREATE TABLE IF NOT EXISTS conversion_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id VARCHAR(255) NOT NULL,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    event_type VARCHAR(50) NOT NULL, -- 'visit', 'product_view', 'add_to_cart', 'checkout', 'purchase'
    product_id UUID REFERENCES sweets(id) ON DELETE SET NULL,
    order_id UUID REFERENCES orders(id) ON DELETE SET NULL,
    event_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. Create Inventory History table
CREATE TABLE IF NOT EXISTS inventory_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sweet_id UUID NOT NULL REFERENCES sweets(id) ON DELETE CASCADE,
    previous_quantity INTEGER NOT NULL,
    new_quantity INTEGER NOT NULL,
    change_reason VARCHAR(50) NOT NULL, -- 'restock', 'sale', 'adjustment', 'return', 'damaged'
    reference_id UUID, -- Could be order_id, supplier_order_id, etc.
    notes TEXT,
    changed_by UUID REFERENCES users(id) ON DELETE SET NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 8. Create Shipping Analytics table
CREATE TABLE IF NOT EXISTS shipping_analytics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    shipping_carrier VARCHAR(100),
    shipping_cost DECIMAL(10,2),
    estimated_delivery_date TIMESTAMP,
    actual_delivery_date TIMESTAMP,
    shipping_time_days DECIMAL(5,2),
    is_on_time BOOLEAN,
    tracking_updates INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(order_id)
);

-- 9. Create Order Analytics table
CREATE TABLE IF NOT EXISTS order_analytics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    time_to_process_minutes INTEGER,
    items_count INTEGER,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    tax_amount DECIMAL(10,2) DEFAULT 0,
    profit_amount DECIMAL(10,2) DEFAULT 0,
    profit_margin DECIMAL(5,2) DEFAULT 0,
    acquisition_channel VARCHAR(50), -- 'direct', 'search', 'social', 'email', 'referral'
    device_type VARCHAR(20), -- 'desktop', 'mobile', 'tablet'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(order_id)
);

-- 10. Create Monthly Sales Summary table
CREATE TABLE IF NOT EXISTS monthly_sales (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    month INTEGER NOT NULL,
    year INTEGER NOT NULL,
    total_revenue DECIMAL(12,2) DEFAULT 0,
    total_orders INTEGER DEFAULT 0,
    total_profit DECIMAL(12,2) DEFAULT 0,
    avg_order_value DECIMAL(10,2) DEFAULT 0,
    growth_percentage DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(month, year)
);

-- 11. Create Category Sales table
CREATE TABLE IF NOT EXISTS category_sales (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category VARCHAR(100) NOT NULL,
    units_sold INTEGER DEFAULT 0,
    revenue DECIMAL(12,2) DEFAULT 0,
    profit DECIMAL(12,2) DEFAULT 0,
    percentage DECIMAL(5,2) DEFAULT 0,
    time_period VARCHAR(10) NOT NULL, -- 'daily', 'weekly', 'monthly', 'yearly'
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(category, time_period, period_start)
);

-- 12. Create System Alerts table
CREATE TABLE IF NOT EXISTS system_alerts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    alert_type VARCHAR(50) NOT NULL, -- 'warning', 'info', 'success', 'error'
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL, -- 'low', 'medium', 'high'
    is_read BOOLEAN DEFAULT false,
    is_dismissed BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_product_analytics_sweet_id ON product_analytics(sweet_id);
CREATE INDEX IF NOT EXISTS idx_product_analytics_period ON product_analytics(time_period, period_start, period_end);
CREATE INDEX IF NOT EXISTS idx_customer_analytics_user_id ON customer_analytics(user_id);
CREATE INDEX IF NOT EXISTS idx_customer_analytics_segment ON customer_analytics(segment_id);
CREATE INDEX IF NOT EXISTS idx_website_traffic_date ON website_traffic(visit_date);
CREATE INDEX IF NOT EXISTS idx_website_traffic_user ON website_traffic(user_id);
CREATE INDEX IF NOT EXISTS idx_conversion_events_session ON conversion_events(session_id);
CREATE INDEX IF NOT EXISTS idx_conversion_events_type ON conversion_events(event_type);
CREATE INDEX IF NOT EXISTS idx_inventory_history_sweet ON inventory_history(sweet_id);
CREATE INDEX IF NOT EXISTS idx_shipping_analytics_order ON shipping_analytics(order_id);
CREATE INDEX IF NOT EXISTS idx_order_analytics_order ON order_analytics(order_id);
CREATE INDEX IF NOT EXISTS idx_monthly_sales_date ON monthly_sales(year, month);
CREATE INDEX IF NOT EXISTS idx_category_sales_category ON category_sales(category);
CREATE INDEX IF NOT EXISTS idx_category_sales_period ON category_sales(time_period, period_start, period_end);
CREATE INDEX IF NOT EXISTS idx_system_alerts_type ON system_alerts(alert_type);
CREATE INDEX IF NOT EXISTS idx_system_alerts_priority ON system_alerts(priority);
