CREATE TABLE diseases (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(150) NOT NULL UNIQUE,
    normalized_name  VARCHAR(150) NOT NULL,
    description      TEXT
);

CREATE INDEX idx_diseases_normalized_name ON diseases(normalized_name);

CREATE TABLE treatments (
    id            BIGSERIAL PRIMARY KEY,
    disease_id    BIGINT NOT NULL REFERENCES diseases(id),
    product_name  VARCHAR(150) NOT NULL,
    type          VARCHAR(20) NOT NULL,      -- 'ORGANIC' or 'CHEMICAL'
    dosage        VARCHAR(100),
    frequency     VARCHAR(100),
    safety_notes  TEXT,
    crop_type     VARCHAR(100),              
    severity      VARCHAR(20),               
    active        BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_treatments_disease_id ON treatments(disease_id);