# E-Commerce Marketplace Service — Student 4

**Port:** `8084`  
**Database:** PostgreSQL  
**Owner:** Student 4

## Overview

Turns CeyGreen from a monitoring tool into a marketplace where farmers earn from their harvest. Farmers list produce, manage stock, and buyers purchase directly through the client. This is the most conventional CRUD service of the six — a good anchor for demonstrating solid REST practices.

Every new order and every stock change is published as a Kafka event so Student 6's service can turn it into both a notification and a data point for sales analytics.

## Endpoints

| Method | Path | CRUD | Description |
|---|---|---|---|
| GET | `/products` | Read | List all active harvest listings; filterable by crop type and location |
| GET | `/products/{id}` | Read | Get full detail for a single listing |
| POST | `/products` | Create | Create a new listing (farmer only) |
| PUT | `/products/{id}` | Update | Update price, quantity, or availability (including marking inactive) |
| POST | `/orders/checkout` | Create | Buyer purchases a listed quantity; decrements stock, creates order, publishes events |

> Explicit `DELETE /products/{id}` was retired in favour of PUT with an availability flag, keeping listing history intact for analytics.

## Database Schema (PostgreSQL)

| Table | Key Columns | Purpose |
|---|---|---|
| `products` | `id, farmer_id, crop_name, quantity, unit_price, harvest_date, location, active` | Active and past harvest listings |
| `orders` | `id, buyer_id, product_id, quantity, total_price, status, ordered_at` | Transactional record of each purchase |

## Kafka Integration

| Topic | Role | Published When |
|---|---|---|
| `order-events` | **Producer** | A buyer completes checkout |
| `stock-events` | **Producer** | Stock runs low or is refilled |

## Security

- API Key verification on every endpoint
- Write access to a listing restricted to its owning farmer

## Ideas Worth Adding

- Auto-decrement stock on checkout and flag low stock via `stock-events`
- A star-rating field on completed orders for buyer feedback

## Getting Started

### Prerequisites

- Java 17+
- Docker Desktop (for PostgreSQL on port **5433** and Kafka on **9092**)

### Local run

```bash
cd ecommerce-service
.\mvnw.cmd spring-boot:run    # Windows
./mvnw spring-boot:run        # macOS / Linux
```

Health (no API key required):

```bash
curl http://localhost:8084/actuator/health
```

Business endpoints require `X-API-Key: ceygreen-dev-api-key` (or `SERVICE_API_KEY` from `.env`).

### Database

This service uses PostgreSQL database `ceygreen_ecommerce`. Docker Compose creates it via
[`docker/postgres/init-databases.sql`](../docker/postgres/init-databases.sql) on first Postgres startup.

If you already had a Postgres volume before that script existed, recreate it once:

```bash
docker compose down -v
docker compose up -d postgres kafka
```

Flyway is enabled; schema migrations will be added in Phase 2. Until then, Hibernate `ddl-auto=update`
manages the scaffold tables.

### Configuration

| Variable | Default | Purpose |
|---|---|---|
| `POSTGRES_URL` | `jdbc:postgresql://localhost:5433/ceygreen_ecommerce` | Database connection |
| `SERVICE_API_KEY` | `ceygreen-dev-api-key` | API key for all endpoints |
| `MARKETPLACE_LOW_STOCK_THRESHOLD` | `10` | Low-stock alert threshold (used in Phase 6) |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka broker |

