ALTER TABLE products
    ADD COLUMN description TEXT,
    ADD COLUMN image_url VARCHAR(512),
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

ALTER TABLE orders
    ADD COLUMN buyer_name VARCHAR(255),
    ADD COLUMN phone VARCHAR(64),
    ADD COLUMN address VARCHAR(512),
    ADD COLUMN city VARCHAR(128),
    ADD COLUMN postal_code VARCHAR(32),
    ADD COLUMN unit_price NUMERIC(12, 2),
    ADD COLUMN crop_name VARCHAR(255),
    ADD COLUMN farmer_id UUID;

UPDATE orders o
SET unit_price = p.unit_price,
    crop_name = p.crop_name,
    farmer_id = p.farmer_id
FROM products p
WHERE o.product_id = p.id
  AND o.unit_price IS NULL;

UPDATE orders SET status = 'DELIVERED' WHERE status = 'COMPLETED';

CREATE INDEX idx_orders_farmer_id ON orders (farmer_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_products_created_at ON products (created_at);
