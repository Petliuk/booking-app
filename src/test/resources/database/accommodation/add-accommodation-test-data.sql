INSERT INTO accommodations (id, property_type, street, city, country, postal_code, size, amount_to_pay, availability)
VALUES (1, 'APARTMENT', '123 Test St', 'Kyiv', 'Ukraine', '01001', '80 sqm', 50.00, 2);

INSERT INTO accommodation_amenities (accommodation_id, amenities) VALUES (1, 'WiFi');
INSERT INTO accommodation_amenities (accommodation_id, amenities) VALUES (1, 'Parking');