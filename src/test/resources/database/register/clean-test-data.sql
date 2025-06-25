DELETE FROM user_roles;
DELETE FROM roles;
DELETE FROM users;

INSERT INTO roles (id, name) VALUES (1, 'CUSTOMER');