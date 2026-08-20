-- Add author details to treatments
ALTER TABLE treatments
ADD COLUMN added_by_farmer_id VARCHAR(255),
ADD COLUMN added_by_farmer_name VARCHAR(255);
