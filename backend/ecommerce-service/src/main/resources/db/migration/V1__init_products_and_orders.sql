CREATE TABLE products (
    id              BIGSERIAL PRIMARY KEY,
    farmer_id       UUID         NOT NULL,
    crop_name       VARCHAR(255) NOT NULL,
    quantity        INTEGER      NOT NULL CHECK (quantity >= 0),
    unit_price      NUMERIC(12, 2) NOT NULL CHECK (unit_price > 0),
    harvest_date    DATE         NOT NULL,
    location        VARCHAR(255) NOT NULL,
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    version         BIGINT       NOT NULL DEFAULT 0
);

CREATE TABLE orders (
    id           BIGSERIAL PRIMARY KEY,
    buyer_id     UUID           NOT NULL,
    product_id   BIGINT         NOT NULL REFERENCES products (id),
    quantity     INTEGER        NOT NULL CHECK (quantity > 0),
    total_price  NUMERIC(12, 2) NOT NULL,
    status       VARCHAR(32)    NOT NULL,
    ordered_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_products_active ON products (active);
CREATE INDEX idx_products_crop_name ON products (crop_name);
CREATE INDEX idx_products_location ON products (location);
CREATE INDEX idx_orders_buyer_id ON orders (buyer_id);
CREATE INDEX idx_orders_product_id ON orders (product_id);
