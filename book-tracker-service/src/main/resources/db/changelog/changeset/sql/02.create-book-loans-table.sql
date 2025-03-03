CREATE TABLE book_loans (
                            id SERIAL PRIMARY KEY,
                            book_id BIGINT NOT NULL UNIQUE,
                            status VARCHAR(255) NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                            modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                            returned_at TIMESTAMP,
                            deleted BOOLEAN NOT NULL DEFAULT FALSE
);
