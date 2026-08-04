# Sales Analytics & Notification Service — Student 6

**Port:** `8086`  
**Database:** PostgreSQL  
**Owner:** Student 6

## Overview

Two closely related jobs in one service:

1. **Sales Analytics** — Consumes `order-events` to build a running picture of each farmer's sales (total revenue, order count, best-selling crop) without ever calling the E-Commerce service directly. This is the data source for the platform's **Grafana sales dashboard**.

2. **Unified Notifications** — Consumes events from **all five other services** and delivers each as an email, SMS, or in-app notification according to the recipient's saved preferences.

Both halves are fed by the same idea — consuming events from Kafka rather than being called directly — so they share one service and one deployable unit.

## Part A — Sales Analytics Endpoints

| Method | Path | CRUD | Description |
|---|---|---|---|
| GET | `/analytics/sales/{farmerId}` | Read | Sales summary: revenue, order count, top-selling crop |
| GET | `/analytics/sales/{farmerId}/trend` | Read | Revenue/order trend over time (for charting & Grafana) |
| GET | `/analytics/leaderboard` | Read | Platform-wide top-performing farmers and products |

## Part B — Unified Notification Endpoints

| Method | Path | CRUD | Description |
|---|---|---|---|
| GET | `/notify/history/{userId}` | Read | A user's past notifications across all event types |
| PUT | `/notify/preferences/{userId}` | Update | Set which event types a user wants, and on which channel |
| DELETE | `/notify/history/{id}` | Delete | Clear a notification record |

## Database Schema (PostgreSQL)

| Table | Key Columns | Purpose |
|---|---|---|
| `sales_summary` | `farmer_id, total_orders, total_revenue, last_updated` | Running aggregate rebuilt as order-events arrive |
| `order_log` | `id, farmer_id, order_id, amount, product, recorded_at` | Raw event log backing the trend charts |
| `notifications` | `id, user_id, source_topic, channel, message, sent_at, status` | Delivery log for every notification sent |
| `notification_preferences` | `user_id, event_type, channel, enabled` | Per-user, per-event-type opt-in/opt-out settings |

## Kafka Integration

| Topic | Role | Source |
|---|---|---|
| `greenhouse-alerts` | **Consumer** | Student 1 (IoT) |
| `diagnosis-events` | **Consumer** | Student 2 (Disease Detection) |
| `treatment-events` | **Consumer** | Student 3 (Treatment & Suggestion) |
| `order-events` | **Consumer** | Student 4 (E-Commerce) |
| `stock-events` | **Consumer** | Student 4 (E-Commerce) |
| `forum-events` | **Consumer** | Student 5 (Forum) |

## Security

- API Key verification on all REST endpoints
- A farmer can only read their own sales data unless authenticated as admin
- Kafka consumer group config ensures exactly-once processing

## Ideas Worth Adding

- A weekly automated digest email summarising sales performance and unresolved greenhouse alerts

## Getting Started

```bash
# Place your Spring Boot project here
# Port: 8086
# Tech: Spring Boot 3.5.x, Java 17, PostgreSQL
```
