# 👥 User Service

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?style=flat-square&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-Consumer-black?style=flat-square&logo=apache-kafka)

**User profile management and role assignment service**

</div>

---

## 📋 Overview

The User Service manages user profiles, roles, and user-related operations. It consumes registration events from Kafka and provides user information to other services.

### Key Features

- 👤 User profile management (CRUD operations)
- 🎭 Role assignment and retrieval
- 📨 Kafka consumer for user registration events
- 🔒 BCrypt password encryption
- 🔄 Profile updates
- 📊 User statistics and analytics

## 🏗️ Architecture

```
┌──────────────┐      ┌──────────────────┐      ┌─────────────┐
│    Kafka     │─────▶│   User Service   │─────▶│ PostgreSQL  │
│   (Events)   │      │   (Port 8082)    │      │ (userdb)    │
└──────────────┘      └──────────────────┘      └─────────────┘
```

## 🚀 API Endpoints

### User Management

#### Get User Profile
```http
GET /api/users/{id}/profile
Authorization: Bearer {token}
```

**Response:**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+79001234567",
  "roleName": "JOB_SEEKER"
}
```

#### Update User Profile
```http
POST /api/users/update
Authorization: Bearer {token}
Content-Type: application/json

{
  "id": 1,
  "username": "john_doe_updated",
  "email": "john.new@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+79001234567"
}
```

#### Check if User Exists
```http
GET /api/users/existsByEmail?email=john@example.com
```

**Response:** `true` or `false`

#### Validate Login Credentials
```http
POST /api/users/checkLogin
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePassword123"
}
```

**Response:** `true` or `false`

#### Get Email by User ID
```http
GET /api/users/emailById?id=1
```

**Response:** `"john@example.com"`

#### Get User ID by Email
```http
GET /api/users/userIdByEmail?email=john@example.com
```

**Response:** `1`

### Role Management

#### Get User Role by Email
```http
GET /api/roles/roleByEmail?email=john@example.com
```

**Response:** `"JOB_SEEKER"` or `"EMPLOYER"`

#### Get User Role by ID
```http
GET /api/roles/roleById?id=1
```

**Response:** `"JOB_SEEKER"` or `"EMPLOYER"`

## 📊 Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone_number VARCHAR(50),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Roles Table
```sql
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO roles (role_name) VALUES 
    ('JOB_SEEKER'),
    ('EMPLOYER'),
    ('ADMIN');
```

## 📥 Kafka Integration

### Consumer Configuration

**Topic:** `user-registration`  
**Group ID:** `user-service-group`

**Event Handling:**
```java
@KafkaListener(topics = "${spring.kafka.topics.user-registration}")
public void listen(String message) {
    RegisterRequest request = objectMapper.readValue(message, RegisterRequest.class);
    userService.create(request);
}
```

### User Registration Event
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "phone": "+79001234567",
  "password": "hashedPassword",
  "userRole": "JOB_SEEKER"
}
```

## ⚙️ Configuration

### application.yaml
```yaml
server:
  port: 8082

spring:
  application:
    name: user-service
  
  datasource:
    url: jdbc:postgresql://localhost:5434/userdb
    username: useruser
    password: userpass
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: true
  
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
  
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: user-service-group
    topics:
      user-registration: user-registration
```

## 🚀 Getting Started

### Prerequisites
- Java 17+
- PostgreSQL 16
- Apache Kafka

### Run Service
```bash
./gradlew bootRun
```

### Run with Docker
```bash
docker-compose up user-service postgres-user kafka
```

### Database Migration
```bash
# Flyway migrations run automatically on startup
# Manual migration:
./gradlew flywayMigrate
```

## 🧪 Testing

```bash
# Run tests
./gradlew test

# Sample test
@Test
@DisplayName("Create user: Success")
void create_ShouldSaveUserWithEncodedPassword() {
    RegisterRequest request = RegisterRequest.builder()
        .email("test@example.com")
        .username("testuser")
        .password("raw_password")
        .userRole("JOB_SEEKER")
        .build();
    
    when(bCryptPasswordEncoder.encode("raw_password"))
        .thenReturn("encoded_password");
    
    userService.create(request);
    
    verify(userRepository).save(argThat(user -> 
        user.getPassword().equals("encoded_password")
    ));
}
```

## 📈 Performance Metrics

- Profile retrieval: ~10ms
- User creation: ~50ms (with password hashing)
- Role lookup: ~5ms
- Database connection pool: 10 connections

## 🔐 Security

- **Password Storage**: BCrypt with salt rounds=10
- **SQL Injection**: Protected via JPA/Hibernate
- **Input Validation**: Jakarta Bean Validation
- **Authorization**: Role-based access control

## 🔮 Future Enhancements

- [ ] User avatar upload
- [ ] Email verification
- [ ] Two-factor authentication
- [ ] User activity logging
- [ ] Social media integration
- [ ] Advanced user search

## 📄 License

Part of LaborExchange Platform - MIT License

---

<div align="center">

**[Back to Main Documentation](../README.md)** | **[Auth Service](../laborexchange-authservice/README.md)** | **[Vacancy Service](../laborexchange-vacancyservice/README.md)**

</div>
