CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    email    VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(100)        NOT NULL,
    role_id  BIGINT              NOT NULL,
    CHECK (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);