# 📅 Booking App

Welcome to **Booking App** — a modern platform for accommodation booking. This project is designed for convenient search, booking, and payment of accommodations by users, as well as efficient management of properties and bookings by managers. The application integrates with Stripe for payment processing and Telegram for notifications, ensuring secure and scalable operations.

---

## 🔎 What’s Included in the Documentation

This documentation includes:
- Project overview
- Technologies and libraries used
- Main features and API capabilities
- Database schema and entity relationships
- Detailed description of controller functionality with endpoints
- Setup, running, and testing instructions
- Instructions for running with Docker
- Example API requests for testing
- Placeholders for database schema and Swagger UI screenshots

---

## Project Overview

**Booking App** is a RESTful API backend application that provides:
- 🏠 Search and booking of accommodations by parameters (type, location, price, amenities)
- 📅 Booking management (create, view, update, cancel)
- 💳 Payment processing via Stripe
- 🔐 JWT-based authentication and role-based authorization (CUSTOMER, MANAGER)
- 🔔 Notifications via Telegram
- 🕒 Automated checks for expired bookings and payments
- 📄 Pagination for accommodation and booking lists

---

## Technologies Used

| Technology | Description |
|------------|-------------|
| ☕ **Java 21** | Core programming language |
| 🚀 **Spring Boot 3.4.5** | Framework for rapid development |
| 🔐 **Spring Security** | JWT-based authentication and access control |
| 🗄️ **Spring Data JPA** | ORM with Hibernate |
| 🐬 **MySQL** | Database for production and testing |
| 🌐 **Swagger (Springdoc 2.7.0)** | API documentation and UI |
| 🧱 **Liquibase 4.29.1** | Database schema migrations |
| 🔄 **MapStruct 1.5.3** | Mapping between DTOs and entities |
| 🧹 **Lombok** | Boilerplate code reduction |
| ⚙️ **Maven** | Dependency and build management |
| 🔑 **JJWT 0.11.5** | JWT handling |
| 🔔 **Telegram Bots 6.8.0** | Notifications via Telegram |
| 💳 **Stripe Java 26.4.0** | Payment processing |
| 🧪 **JUnit & Mockito** | Unit and integration testing |
| 🧪 **Testcontainers 1.20.6** | Integration testing with MySQL containers |
| 📊 **JaCoCo 0.8.12** | Code coverage analysis |
| 🧼 **Checkstyle 3.1.1** | Code style checking |
| ✅ **Validation API** | Data validation annotations |
| 🐳 **Docker** | Containerization and deployment |

---

## Main Features

- 🔐 **JWT Authentication** — Secure user registration and login with tokens (token validity: 300,000,000 ms)
- 🏠 **Accommodation Management** — CRUD operations (for managers), search by parameters
- 📅 **Booking Management** — Create, view, update, cancel bookings
- 💳 **Payments** — Create and process payment sessions via Stripe
- 🧑‍💼 **Role Management** — Assign CUSTOMER or MANAGER roles
- 🔔 **Notifications** — Send booking and payment notifications via Telegram
- 🕒 **Automation** — Check and update expired bookings and payments
- 📄 **Pagination** — For accommodation and booking lists

---

## Database Schema

### Entity Overview

- **users** ↔ **roles** — many-to-many via `user_roles`
- **accommodations** ↔ **bookings** — one-to-many
- **bookings** ↔ **payments** — one-to-one
- **accommodations** includes an embedded **address** entity (street, city, country, postal code)
- **accommodations** has a list of amenities (`amenities`) as a collection

### Visual Schema
![Database Schema](booking_diagram.png)
---

## Controller Functionality

### 🔑 AuthController
- `POST /api/auth/register` — Register a new user
- `POST /api/auth/login` — Authenticate and obtain a JWT token

### 🏠 AccommodationController
- `GET /api/accommodations` — List all accommodations (with pagination)
- `GET /api/accommodations/{id}` — Get accommodation details by ID
- `GET /api/accommodations/search` — Search accommodations by parameters (type, location, price, amenities)
- `POST /api/accommodations` — Create a new accommodation (MANAGER only)
- `PUT /api/accommodations/{id}` — Update an accommodation (MANAGER only)
- `DELETE /api/accommodations/{id}` — Delete an accommodation (MANAGER only)

### 📅 BookingController
- `POST /api/bookings` — Create a new booking (authenticated users)
- `GET /api/bookings/my` — View current user's bookings
- `GET /api/bookings` — List bookings with filters (userId, status) (MANAGER only)
- `GET /api/bookings/{id}` — Get booking details by ID (owner or MANAGER)
- `PUT /api/bookings/{id}` — Update a booking (owner or MANAGER)
- `DELETE /api/bookings/{id}` — Cancel a booking (owner or MANAGER)

### 💳 PaymentController
- `POST /api/payments` — Create a payment session for a booking
- `GET /api/payments` — List payments, optionally filtered by userId (MANAGER only)
- `GET /api/payments/my` — View current user's payments
- `GET /api/payments/success?session_id={sessionId}` — Handle successful payment
- `GET /api/payments/cancel?session_id={sessionId}` — Handle canceled payment

### 🧑‍💼 UserController
- `GET /api/users/me` — Get current user's profile
- `PUT /api/users/me` — Update current user's profile
- `PUT /api/users/{id}/role` — Update a user's role (MANAGER only)

---

## Setup Instructions

### ✅ Prerequisites
- **Java 21**
- **Maven 3.8+**
- **MySQL** (for production and testing)
- **Docker** (for containerization)
- **Postman** (optional for API testing)
- API keys for **Stripe** and **Telegram Bot**

### 📥 Clone the Repository
```bash
git clone <https://github.com/your-username/booking-app.git>
cd booking-app
```

### ⚙️ Database Setup

For MySQL, create the `booking` database before running the application. Database configuration is set via the `.env` file. Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/booking?serverTimezone=UTC
spring.datasource.username=${MYSQLDB_USERNAME}
spring.datasource.password=${MYSQLDB_ROOT_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jackson.deserialization.fail-on-unknown-properties=true
spring.config.import=optional:file:.env[.properties]
```

**Note**: Before running, create the `booking` database in MySQL by executing:
```sql
CREATE DATABASE booking;
```

---

## 🐳 Running with Docker

1. **Create the `.env` file**:
   Copy `.env.template` to `.env` and fill in the variables:
   ```env
   MYSQLDB_ROOT_PASSWORD=your_mysql_password
   MYSQLDB_USERNAME=your_mysql_username
   MYSQLDB_DATABASE=booking
   MYSQLDB_LOCAL_PORT=3307
   MYSQLDB_DOCKER_PORT=3306
   APP_LOCAL_PORT=8081
   APP_DOCKER_PORT=8080
   SPRING_PROFILES_ACTIVE=prod
   STRIPE_SECRET_KEY=your_stripe_secret_key
   TELEGRAM_BOT_TOKEN=your_telegram_bot_token
   TELEGRAM_CHAT_ID=your_telegram_chat_id
   TELEGRAM_USERNAME=your_telegram_bot_username
   JWT_SECRET=your_jwt_secret_key
   ```

2. **Build and run containers**:
   ```bash
   docker-compose up --build
   ```
   For background mode:
   ```bash
   docker-compose up -d --build
   ```

3. **Access services**:
    - **Application**: `http://localhost:8081/api`
    - **Swagger UI**: `http://localhost:8081/api/swagger-ui/index.html`
    - **MySQL**:
        - Host: `localhost`
        - Port: `3307`
        - User: `your_mysql_username`
        - Password: `your_mysql_password`
        - Database: `booking`

4. **Stop containers**:
   ```bash
   docker-compose down
   ```

### 🔌 Key Docker Commands
| Command | Description |
|---------|-------------|
| `docker-compose up --build` | Build and run containers |
| `docker-compose down` | Stop and remove containers |
| `docker-compose logs -f app` | View application logs |
| `docker-compose ps` | Check container status |

### ℹ️ Notes
- The first run may take 3-5 minutes due to image downloads and database initialization via Liquibase.
- Ensure ports `3307` (MySQL) and `8081` (application) are free.
- The healthcheck in `docker-compose.yml` ensures the application starts only after MySQL is ready.
- The `spring.jpa.hibernate.ddl-auto=validate` setting requires a pre-created database schema via Liquibase.

---

## 🧪 API Testing

### 📡 API Access
- **Endpoint**: `http://localhost:8081/api` (Docker) or `http://localhost:8080/api` (locally)
- **Swagger UI**: `http://localhost:8081/api/swagger-ui/index.html` (Docker) or `http://localhost:8080/api/swagger-ui/index.html` (locally)

### 🧪 Testing with Testcontainers
Integration testing uses Testcontainers with MySQL:
- Tests automatically create a MySQL container for testing.
- Use the `testcontainers-junit-jupiter` and `testcontainers-mysql` dependencies in `pom.xml`.

### 🧪 Swagger UI Screenshots
Below are placeholders for Swagger UI screenshots for each controller, showcasing example API requests:

#### 🔑 AuthController
![Swagger UI - AuthController](auth_controller.png)

#### 🏠 AccommodationController
![Swagger UI - AccommodationController](accommodation_controller.png)

#### 📅 BookingController
![Swagger UI - BookingController](booking_controller.png)

#### 💳 PaymentController
![Swagger UI - PaymentController](payment_controller.png)

#### 🧑‍💼 UserController
![Swagger UI - UserController](user_controller.png)

### 🧪 Example Requests

#### ✅ Register a New User
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "repeatPassword": "password123",
  "firstName": "test_name",
  "lastName": "test_lastname"
}
```

#### 🔐 Login to Obtain JWT
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

Use the obtained token in the header:
```http
Authorization: Bearer <JWT_TOKEN>
```

#### 🏠 Create a New Accommodation (MANAGER only)
```http
POST /api/accommodations
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "propertyType": "APARTMENT",
  "location": {
    "street": "Main St 123",
    "city": "Kyiv",
    "country": "Ukraine",
    "postalCode": "01001"
  },
  "size": "50 sqm",
  "amenities": ["Wi-Fi", "Parking"],
  "pricePerDay": 50.00,
  "availability": 5
}
```

#### 📅 Create a Booking
```http
POST /api/bookings
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "checkInDate": "2025-07-01",
  "checkOutDate": "2025-07-05",
  "accommodationId": 1
}
```

#### 💳 Create a Payment Session
```http
POST /api/payments
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "bookingId": 1
}
```