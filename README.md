# Virtual Wallet API

Concurrent and scalable Virtual Wallet REST API built with Spring Boot for Fintech use cases.

## Tech Stack

Java 21, Spring Boot 3, PostgreSQL, Redis, RabbitMQ, Docker

## Features

- Optimistic Locking for concurrent deposit/transfer safety
- Redis caching for transaction history
- Event-driven processing via RabbitMQ
- Centralized exception handling with structured JSON responses
- Transactional integrity for money transfers
- Paginated transaction history
- Swagger UI documentation

## API Endpoints

| Method | Endpoint | Description |
|--------|------------------------------------------|---------------------------|
| POST | `/api/v1/users` | Register user |
| POST | `/api/v1/wallets` | Create wallet |
| POST | `/api/v1/transactions/deposit` | Deposit |
| POST | `/api/v1/transactions/transfer` | Transfer between wallets |
| GET | `/api/v1/transactions/history/{walletId}` | Transaction history |

## Quick Start

```bash
docker-compose up -d
./mvnw spring-boot:run
```

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Tests

```bash
./mvnw test
```
