DELETE FROM user_roles;
DELETE FROM roles;
DELETE FROM users;

INSERT INTO roles (id, name) VALUES (1, 'CUSTOMER');

INSERT INTO users (id, email, password, first_name, last_name)
VALUES (
           1,
           'user@example.com',
           '$2a$10$ZPZzTpxnZ/xXxQmVV9PhBOq1gTx2vBb83nb9eqrYBlM4Fn3rCmAYK',
           'John',
           'Doe'
       );

-- Прив’язка користувача до ролі
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);