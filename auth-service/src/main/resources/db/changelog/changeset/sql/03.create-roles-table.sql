CREATE TABLE roles
(
    id   SERIAL PRIMARY KEY,
    name user_role UNIQUE NOT NULL
);