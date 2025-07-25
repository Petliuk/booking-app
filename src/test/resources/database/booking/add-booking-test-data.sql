INSERT INTO users (id, email, first_name, last_name, password)
VALUES (1, 'user@example.com', 'John', 'Doe', '$2a$10$hashedPassword123')
    ON DUPLICATE KEY UPDATE email=email;

INSERT INTO users_roles (user_id, role_id)
SELECT 1, id FROM roles WHERE name = 'CUSTOMER'
    ON DUPLICATE KEY UPDATE user_id=user_id;

INSERT INTO accommodations (id, property_type, street, city, country, postal_code, size, amount_to_pay, availability)
VALUES (1, 'APARTMENT', '123 Test St', 'Kyiv', 'Ukraine', '01001', '80 sqm', 50.00, 2)
    ON DUPLICATE KEY UPDATE id=id;

INSERT INTO accommodation_amenities (accommodation_id, amenities)
VALUES (1, 'WiFi'), (1, 'Parking')
    ON DUPLICATE KEY UPDATE accommodation_id=accommodation_id;

INSERT INTO bookings (id, check_in_date, check_out_date, accommodation_id, user_id, status)
VALUES (1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1, 1, 'PENDING')
ON DUPLICATE KEY UPDATE id=id;
