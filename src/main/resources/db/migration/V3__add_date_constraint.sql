ALTER TABLE client
ADD CONSTRAINT start_date
CHECK (birthday > '1930-12-31');