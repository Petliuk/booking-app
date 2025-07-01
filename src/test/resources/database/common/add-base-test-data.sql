DELETE FROM users_roles;
DELETE FROM roles;
DELETE FROM users;

INSERT INTO roles (id, name) VALUES (1, 'CUSTOMER');
INSERT INTO roles (id, name) VALUES (2, 'MANAGER');

INSERT INTO users (id, email, password, first_name, last_name)
VALUES (1, 'user@example.com', 'password123', 'John', 'Doe');

INSERT INTO users_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO users_roles (user_id, role_id) VALUES (1, 2);