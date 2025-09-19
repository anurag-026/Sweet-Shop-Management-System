-- Add new fields to orders table for better tracking and payment management

-- Add payment mode column
ALTER TABLE orders ADD COLUMN payment_mode VARCHAR(50) NOT NULL DEFAULT 'CREDIT_CARD';

-- Add payment transaction ID
ALTER TABLE orders ADD COLUMN payment_transaction_id VARCHAR(255);

-- Add shipping address
ALTER TABLE orders ADD COLUMN shipping_address TEXT;

-- Add customer notes
ALTER TABLE orders ADD COLUMN customer_notes TEXT;

-- Add tracking number for shipment
ALTER TABLE orders ADD COLUMN tracking_number VARCHAR(255);

-- Add estimated delivery date
ALTER TABLE orders ADD COLUMN estimated_delivery_date TIMESTAMP;

-- Add actual delivery date
ALTER TABLE orders ADD COLUMN actual_delivery_date TIMESTAMP;

-- Add last updated timestamp
ALTER TABLE orders ADD COLUMN last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Update existing order status enum to include more statuses
-- Note: This is database-specific. For PostgreSQL, we need to add new enum values
DO $$ 
BEGIN
    -- Add new order status values if they don't exist
    IF NOT EXISTS (SELECT 1 FROM pg_enum WHERE enumlabel = 'PROCESSING' AND enumtypid = (SELECT oid FROM pg_type WHERE typname = 'order_status')) THEN
        ALTER TYPE order_status ADD VALUE 'PROCESSING' AFTER 'CONFIRMED';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_enum WHERE enumlabel = 'OUT_FOR_DELIVERY' AND enumtypid = (SELECT oid FROM pg_type WHERE typname = 'order_status')) THEN
        ALTER TYPE order_status ADD VALUE 'OUT_FOR_DELIVERY' AFTER 'SHIPPED';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_enum WHERE enumlabel = 'REFUNDED' AND enumtypid = (SELECT oid FROM pg_type WHERE typname = 'order_status')) THEN
        ALTER TYPE order_status ADD VALUE 'REFUNDED' AFTER 'CANCELLED';
    END IF;
END $$;

-- Create payment_mode enum type if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'payment_mode') THEN
        CREATE TYPE payment_mode AS ENUM ('CREDIT_CARD', 'PAYPAL', 'BANK_TRANSFER', 'CASH_ON_DELIVERY');
    END IF;
END $$;

-- Update payment_mode column to use the enum
ALTER TABLE orders ALTER COLUMN payment_mode TYPE payment_mode USING payment_mode::payment_mode;

-- Create index for better query performance
CREATE INDEX IF NOT EXISTS idx_orders_payment_mode ON orders(payment_mode);
CREATE INDEX IF NOT EXISTS idx_orders_status_date ON orders(status, order_date);
CREATE INDEX IF NOT EXISTS idx_orders_tracking_number ON orders(tracking_number);
CREATE INDEX IF NOT EXISTS idx_orders_last_updated ON orders(last_updated);

-- Update existing orders to have the last_updated field set
UPDATE orders SET last_updated = order_date WHERE last_updated IS NULL;
