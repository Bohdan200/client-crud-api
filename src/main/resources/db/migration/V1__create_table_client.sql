CREATE TABLE client (
    id IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    birthday DATE,
    gender VARCHAR(20)
);