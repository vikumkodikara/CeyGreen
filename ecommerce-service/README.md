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

```bash
# Place your Spring Boot project here
# Port: 8084
# Tech: Spring Boot 3.5.x, Java 17, PostgreSQL
```
