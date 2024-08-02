ALTER TABLE client
ADD CONSTRAINT gender_values
CHECK gender IN ('male', 'female');