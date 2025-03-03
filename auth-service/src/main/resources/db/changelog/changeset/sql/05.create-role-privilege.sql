CREATE TABLE role_privilege
(
    id           SERIAL PRIMARY KEY,
    role_id      BIGINT NOT NULL,
    privilege_id BIGINT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles (id),
    FOREIGN KEY (privilege_id) REFERENCES privileges (id)
);