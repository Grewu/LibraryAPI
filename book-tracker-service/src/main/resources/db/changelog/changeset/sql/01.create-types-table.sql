CREATE TYPE book_status AS ENUM (
    'AVAILABLE',
    'BORROWED',
    'RETURNED',
    'OVERDUE'
);