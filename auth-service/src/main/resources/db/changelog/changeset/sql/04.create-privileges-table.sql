CREATE TABLE privileges
(
    id   SERIAL PRIMARY KEY,
    name privileges_type UNIQUE NOT NULL
);