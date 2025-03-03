CREATE DATABASE "auth-db";
CREATE DATABASE "books-storage-db";
CREATE DATABASE "books-tracker-db";

GRANT ALL PRIVILEGES ON DATABASE "auth-db" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "books-storage-db" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "books-tracker-db" TO postgres;
