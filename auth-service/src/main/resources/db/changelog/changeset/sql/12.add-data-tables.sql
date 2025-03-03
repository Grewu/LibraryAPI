INSERT INTO roles (id, name)
VALUES (1, 'ADMIN'),
       (2, 'USER');

INSERT INTO privileges (id, name)
VALUES (1, 'READ'),
       (2, 'WRITE'),
       (3, 'DELETE');

INSERT INTO role_privilege (role_id, privilege_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (2, 1);


INSERT INTO users (email, password, role_id)
VALUES ('pavelgurevichwork@gmail.com', '$2a$10$BS7udr3QYwyUkX4w3p2V.Ovjc/kWnjMJMrSTu9.XFzrHbWuDLasjK', 1),
       ('pavelgurevich97@gmail.com', '$2b$12$QQMT.3IZJEK2SDCnnWlz8erjeUABxfQ45/OYd4hE6ErxOf6lC2efe', 2),
       ('guest@example.com', '$2a$10$lpvBiZ9Vp/caYDQBSpeItuxglzf/Ukx4LRnTTEjw4mmR.iG0.Y67u', 3),
       ('anotheruser@example.com', '$2a$10$kSsePSg1z1uGyt14p97Bp.VmN1mkCpuV2Yykiv4IUI6RJqVIt4que', 3);
