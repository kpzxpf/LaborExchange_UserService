# User Service

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.6-brightgreen?logo=springboot)
![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Port](https://img.shields.io/badge/port-8082-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-userdb-336791?logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-cached-DC382D?logo=redis)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

Internal microservice for user account and role management. Primarily consumed by AuthService via Feign.

## Table of Contents

- [Overview](#overview)
- [API Endpoints](#api-endpoints)
- [Data Model](#data-model)
- [Caching](#caching)
- [Configuration](#configuration)
- [Running Locally](#running-locally)

## Overview

| Property | Value |
|---|---|
| Port | **8082** |
| Base paths | `/api/users`, `/api/roles` |
| Database | PostgreSQL — `userdb` (port 5434) |
| Cache | Redis (profiles: 15 min TTL, emails: 60 min TTL) |
| Migrations | Flyway |
| Swagger UI | `http://localhost:8082/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8082/v3/api-docs` |
| Prometheus | `http://localhost:8082/actuator/prometheus` |

## API Endpoints

### Users — `/api/users`

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/register` | No | Create user (called by AuthService) |
| `PUT` | `/{id}` | JWT | Update user profile |
| `GET` | `/{id}/profile` | No | Get user profile (cached) |
| `GET` | `/{userId}/username` | No | Get username by ID |
| `GET` | `/emailById?id=` | No | Get email by ID (cached) |
| `GET` | `/userIdByEmail?email=` | No | Get user ID by email |
| `GET` | `/existsByEmail?email=` | No | Check email availability |
| `POST` | `/checkLogin` | No | Validate credentials (called by AuthService) |

### Roles — `/api/roles`

| Method | Path | Description |
|---|---|---|
| `GET` | `/roleByEmail?email=` | Get role by email |
| `GET` | `/roleById?id=` | Get role by user ID |

Role values: `JOB_SEEKER`, `EMPLOYER`, `ADMIN`

## Data Model

### UserDto

| Field | Type | Constraints |
|---|---|---|
| `id` | Long | Auto-generated |
| `username` | String | Required, unique |
| `email` | String | Required, valid email, unique |
| `firstName` | String | Optional |
| `lastName` | String | Optional |
| `phoneNumber` | String | Optional |
| `roleName` | String | `JOB_SEEKER`, `EMPLOYER`, or `ADMIN` |

## Caching

| Cache key | TTL | Eviction |
|---|---|---|
| User profile (`userId`) | 15 min | On `PUT /{id}` |
| User email (`userId`) | 60 min | On `PUT /{id}` |

## Configuration

| Property | Default | Description |
|---|---|---|
| `server.port` | `8082` | HTTP port |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5434/userdb` | Database URL |
| `spring.data.redis.host` | `localhost` | Redis host |
| `spring.data.redis.port` | `6379` | Redis port |

## Running Locally

```bash
./gradlew bootRun
```

Requires PostgreSQL on port 5434 (`userdb`) and Redis on port 6379.
