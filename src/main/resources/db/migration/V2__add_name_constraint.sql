ALTER TABLE client
ADD CONSTRAINT name_length
CHECK (LENGTH(name) >= 2 AND LENGTH(name) <= 20);