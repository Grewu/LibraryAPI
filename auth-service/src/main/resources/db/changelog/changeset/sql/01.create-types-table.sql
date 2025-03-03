CREATE TYPE user_role AS ENUM (
    'ADMIN',
    'USER'
    );

CREATE TYPE privileges_type AS ENUM (
    'READ',
    'WRITE',
    'DELETE'
    );
