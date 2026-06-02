-- Students now reference the geographic reference data (owned by config-service).
-- `city` already exists and is repurposed to hold the denormalized city name;
-- we widen it and add the structured references + denormalized country/region names.
ALTER TABLE students ALTER COLUMN city TYPE VARCHAR(150);
ALTER TABLE students ADD COLUMN country_code VARCHAR(2);
ALTER TABLE students ADD COLUMN country_name VARCHAR(120);
ALTER TABLE students ADD COLUMN region_id    uuid;
ALTER TABLE students ADD COLUMN region_name  VARCHAR(120);
ALTER TABLE students ADD COLUMN city_id      uuid;