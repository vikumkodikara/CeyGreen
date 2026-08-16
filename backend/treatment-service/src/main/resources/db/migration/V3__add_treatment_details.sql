-- Add new detail columns to treatments table
ALTER TABLE treatments
    ADD COLUMN phi_days INTEGER,
    ADD COLUMN application_method VARCHAR(255),
    ADD COLUMN brand_names VARCHAR(255),
    ADD COLUMN effectiveness_score INTEGER;

-- Create treatment_ratings table
CREATE TABLE treatment_ratings (
    id BIGSERIAL PRIMARY KEY,
    treatment_id BIGINT NOT NULL,
    farmer_id VARCHAR(255) NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_treatment_rating FOREIGN KEY (treatment_id) REFERENCES treatments(id) ON DELETE CASCADE
);

-- Add index for efficient querying
CREATE INDEX idx_treatment_rating_treatment_id ON treatment_ratings(treatment_id);

-- Update existing seed data with some sample details
UPDATE treatments
SET phi_days = 14,
    application_method = 'Foliar spray',
    brand_names = 'Daconil, Bravo',
    effectiveness_score = 85
WHERE product_name = 'Chlorothalonil';

UPDATE treatments
SET phi_days = 0,
    application_method = 'Foliar spray (thorough coverage)',
    brand_names = 'Neemix, Trilogy',
    effectiveness_score = 75
WHERE product_name = 'Neem Oil';

UPDATE treatments
SET phi_days = 7,
    application_method = 'Foliar spray',
    brand_names = 'Mancozeb, Dithane',
    effectiveness_score = 90
WHERE product_name = 'Mancozeb';

UPDATE treatments
SET phi_days = 0,
    application_method = 'Foliar spray',
    brand_names = 'Serenade, Cease',
    effectiveness_score = 80
WHERE product_name = 'Bacillus subtilis';
