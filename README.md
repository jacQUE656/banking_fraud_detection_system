# Banking System

A microservices-based banking platform built with Spring Boot, PostgreSQL, Kafka, and Redis.

## Architecture

The system is composed of the following services:

| Service | Description |
|---|---|
| `api-gateway` | Entry point routing requests to downstream services |
| `accounting-service` | Manages accounts and ledger records |
| `transaction-service` | Handles transaction processing |
| `payment-service` | Handles payment operations |
| `fraud-detection-service` | Monitors transactions for fraud, backed by Redis |
| `notification-service` | Sends user notifications (e.g. transaction alerts) |

## Tech Stack

- **Language:** Java 17
- **Framework:** Spring Boot
- **Database:** PostgreSQL 18
- **Caching / Rate Limiting:** Redis
- **Messaging:** Apache Kafka (with Zookeeper)
- **Containerization:** Docker & Docker Compose

## Prerequisites

- Java 17 (`openjdk-17-jdk`)
- Docker & Docker Compose
- Maven (or use the included `mvnw` wrapper)

## Getting Started

### 1. Clone the repository

```bash
git clone <repo-url>
cd banking-system
```

### 2. Start infrastructure services

This spins up PostgreSQL, Redis, Kafka, and Zookeeper:

```bash
docker compose up -d
```

### 3. Run a service

From within a service directory (e.g. `accounting-service`):

```bash
./mvnw spring-boot:run
```

Or build and run the jar directly:

```bash
./mvnw clean package
java -jar target/*.jar
```

## Configuration

Each service reads its configuration from `application.yaml`. Key settings include:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/banking_db
    username: banking_user
    password: banking_password
  kafka:
    bootstrap-servers: localhost:9092
```

> **Note:** Default credentials are for local development only. Do not commit real credentials — use environment variables or a secrets manager for any non-local environment.

## Ports

| Service | Port |
|---|---|
| PostgreSQL | 5432 |
| Redis | 6379 |
| Kafka | 9092 |
| Zookeeper | 2181 |
| accounting-service | 9001 |

*(Update this table as other services are assigned ports.)*

## Health Checks

Each Spring Boot service exposes actuator health and info endpoints:

```
GET /actuator/health
GET /actuator/info
```

## License

_TBD_
