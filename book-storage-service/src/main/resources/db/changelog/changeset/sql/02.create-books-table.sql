CREATE TABLE books (
                       id SERIAL PRIMARY KEY,
                       isbn VARCHAR(20) NOT NULL UNIQUE,
                       name VARCHAR(255) NOT NULL,
                       genre genre_type NOT NULL,
                       description TEXT NOT NULL,
                       author VARCHAR(255) NOT NULL
);