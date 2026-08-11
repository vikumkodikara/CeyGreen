# E-Commerce Marketplace Service

**Port:** `8084`  
**Database:** PostgreSQL (`ceygreen_ecommerce`)

## Overview

Farmers list harvested produce; buyers browse and purchase through the client. Successful checkouts and meaningful stock changes are published to Kafka for the analytics and notification service.

This service is **producer-only** for Kafka — it never calls other microservices over REST.

## Architecture

```text
Client / API Gateway
        |  X-API-Key + identity headers
        v
E-Commerce Service (:8084)
        |-- PostgreSQL (products, orders)
        |-- Kafka producer -> order-events, stock-events
```

## Endpoints

| Method | Path | Auth |
|---|---|---|
| GET | `/products` | API key; optional `?cropName=` `?location=` |
| GET | `/products/{id}` | API key |
| POST | `/products` | API key + `FARMER` + `X-Farmer-Id` |
| PUT | `/products/{id}` | API key + owning `FARMER` (or `ADMIN`) |
| POST | `/orders/checkout` | API key + `BUYER` + `X-Buyer-Id` |

> No `DELETE /products/{id}` — use `PUT` with `"active": false` to preserve listing history.

Gateway paths are prefixed with `/api` (for example `/api/products`).

## Database

Flyway migration `V1__init_products_and_orders.sql` owns the schema. Hibernate `ddl-auto=validate`.

| Table | Purpose |
|---|---|
| `products` | Harvest listings (`active` flag keeps history) |
| `orders` | One row per successful checkout |

## Kafka (producer)

| Topic | When |
|---|---|
| `order-events` | Checkout completes (`ORDER_CREATED`) |
| `stock-events` | Stock crosses low threshold (`LOW_STOCK`) or farmer restocks (`RESTOCKED`) |

### Order event fields

`eventId`, `orderId`, `buyerId`, `farmerId`, `productId`, `cropName`, `quantity`, `unitPrice`, `totalPrice`, `status`, `orderedAt`, `eventType`

### Stock event fields

`eventId`, `productId`, `farmerId`, `cropName`, `previousQuantity`, `currentQuantity`, `threshold`, `eventType`, `occurredAt`

## Security

- Every business endpoint requires `X-API-Key` (gateway injects this for authenticated traffic).
- Farmer/buyer identity comes from gateway headers derived from JWT claims — never trust `farmerId`/`buyerId` in request bodies.

## Configuration

| Variable | Default | Purpose |
|---|---|---|
| `POSTGRES_URL` | `jdbc:postgresql://localhost:5433/ceygreen_ecommerce` | Database |
| `SERVICE_API_KEY` | `ceygreen-dev-api-key` | API key |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9094` | Kafka broker (host); Docker services use `kafka:9092` |
| `MARKETPLACE_LOW_STOCK_THRESHOLD` | `10` | Low-stock threshold |
| `MARKETPLACE_RESTOCK_MIN_INCREASE` | `5` | Minimum increase for `RESTOCKED` |

## Local run (Maven)

```bash
cd ecommerce-service
docker compose -f ../docker-compose.yml up -d postgres kafka
# Host apps must use port 9094 (see docker-compose Kafka EXTERNAL listener).
.\mvnw.cmd spring-boot:run
```

```bash
curl http://localhost:8084/actuator/health
curl -H "X-API-Key: ceygreen-dev-api-key" http://localhost:8084/products
```

If Postgres was created before `ceygreen_ecommerce` existed:

```bash
docker compose down -v
docker compose up -d postgres kafka
```

## Docker

From repository root:

```bash
docker compose up --build ecommerce-service
```

Service URL: `http://localhost:8084`

## Tests

```bash
.\mvnw.cmd test
```

Includes unit tests, API tests, Kafka producer tests, and a concurrent checkout test that verifies stock cannot be oversold.

Sample HTTP requests: [`docs/ecommerce-api.http`](../docs/ecommerce-api.http)