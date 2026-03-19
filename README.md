# User Service

Manages user accounts, credentials, and roles for the LaborExchange platform.

## Overview

| Property | Value |
|---|---|
| Port | **8082** |
| Base paths | `/api/users`, `/api/roles` |
| Database | PostgreSQL (port 5434, `userdb`) |
| Cache | Redis (profile: 15 min TTL, email: 60 min TTL) |
| Swagger UI | http://localhost:8082/swagger-ui.html |
| Prometheus metrics | http://localhost:8082/actuator/prometheus |

## API Endpoints

### Users (`/api/users`)

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/api/users/register` | No | Create user account (called by AuthService) |
| `GET` | `/api/users/existsByEmail?email=` | No | Check email existence |
| `PUT` | `/api/users/{id}` | Gateway headers | Update user profile |
| `GET` | `/api/users/{userId}/username` | No | Get username by ID |
| `GET` | `/api/users/{id}/profile` | No | Get full profile (cached) |
| `POST` | `/api/users/checkLogin` | No | Validate credentials |
| `GET` | `/api/users/emailById?id=` | No | Get email by user ID (cached) |
| `GET` | `/api/users/userIdByEmail?email=` | No | Get user ID by email |

### Roles (`/api/roles`)

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/roles/roleByEmail?email=` | Get role name by email |
| `GET` | `/api/roles/roleById?id=` | Get role name by user ID |

## Data Model

### User

| Field | Type | Constraints |
|---|---|---|
| `id` | Long | Auto-generated |
| `username` | String | Unique, NOT NULL |
| `email` | String | Unique, NOT NULL |
| `password` | String | BCrypt encoded |
| `firstName` | String | Optional |
| `lastName` | String | Optional |
| `phoneNumber` | String | Optional |
| `role` | Role | ManyToOne, EAGER |
| `createdAt` | LocalDateTime | Auto |
| `updatedAt` | LocalDateTime | Auto |

### Roles

Available values: `JOB_SEEKER`, `EMPLOYER`, `ADMIN`

## Caching Strategy (Redis)

| Cache key | TTL | Eviction trigger |
|---|---|---|
| `users:profile` | 15 min | `update()` |
| `users:email` | 60 min | `update()` |

## Configuration

| Property | Default | Description |
|---|---|---|
| `server.port` | `8082` | HTTP port |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5434/userdb` | PostgreSQL URL |
| `spring.data.redis.host` | `localhost` | Redis host |

## Running locally

```bash
./gradlew bootRun
```

Requires: PostgreSQL on port 5434, Redis on port 6379.

## Error Responses

```json
{
  "error": "Entity not found",
  "code": 404,
  "timestamp": "2026-03-20T12:00:00"
}
```

| HTTP Code | Trigger |
|---|---|
| `400` | Validation error |
| `404` | User not found |
| `409` | Duplicate email |
| `500` | Unexpected error |
