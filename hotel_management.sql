CREATE DATABASE hotel_management;
USE hotel_management;

CREATE TABLE hotel (
    hotel_id INT AUTO_INCREMENT PRIMARY KEY,
    hotel_name VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL
);

CREATE TABLE room (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    hotel_id INT,
    room_type VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (hotel_id) REFERENCES hotel(hotel_id)
);

CREATE TABLE customer (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    contact_number VARCHAR(15),
    email VARCHAR(100)
);

CREATE TABLE booking (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    room_id INT,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (room_id) REFERENCES room(room_id)
);

INSERT INTO hotel (hotel_name, location) VALUES
('Taj Mahal Palace', 'Mumbai'),
('The Oberoi', 'New Delhi'),
('ITC Grand Chola', 'Chennai'),
('Leela Palace', 'Bengaluru'),
('Radisson Blu', 'Kolkata');

INSERT INTO room (hotel_id, room_type, price) VALUES
(1, 'Deluxe', 15000.00),
(1, 'Suite', 30000.00),
(2, 'Standard', 10000.00),
(3, 'Executive', 12000.00),
(4, 'Suite', 25000.00);

INSERT INTO customer (customer_name, contact_number, email) VALUES
('Rohan Sharma', '9876543210', 'rohan.sharma@example.com'),
('Priya Iyer', '9988776655', 'priya.iyer@example.com'),
('Amit Patel', '9123456789', 'amit.patel@example.com'),
('Neha Singh', '9812345678', 'neha.singh@example.com');



