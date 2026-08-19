-- Add comment and farmer_name columns to treatment_ratings table
ALTER TABLE treatment_ratings
ADD COLUMN farmer_name VARCHAR(255),
ADD COLUMN comment TEXT;
