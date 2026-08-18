# CeyGreen Smart Greenhouse - Sales Analytics & Notification Service (Student 6)

A production-ready Spring Boot 3 / Java 17 microservice designed for **Student 6's module** in the CeyGreen Smart Greenhouse Ecosystem.

---

## 🌟 Key Features

1. **API Key Security**:
   - Custom Spring Security filter verifying `X-API-KEY` header on incoming REST calls.
   - Configurable key via environment variable `CEYGREEN_API_KEY` (default: `ceygreen-secret-api-key-2026`).
   - Open access for OpenAPI / Swagger UI at `/swagger-ui.html`.

2. **Database Entities & Persistence (PostgreSQL & H2 fallback)**:
   - `sales_summary` (`farmer_id` [PK], `total_orders`, `total_revenue`, `last_updated`)
   - `order_log` (`id` [PK, Auto], `farmer_id`, `order_id`, `amount`, `product`, `recorded_at`)
   - `notifications` (`id` [PK, Auto], `user_id`, `source_topic`, `channel`, `message`, `sent_at`, `status`)
   - `notification_preferences` (`user_id` [PK component], `event_type`, `channel`, `enabled`)

3. **Multi-topic Kafka Consumers (`@KafkaListener`)**:
   - Consumes and deserializes events from 6 distinct Kafka topics:
     - `order-events` -> Logs to `order_log`, updates aggregated `sales_summary`, and dispatches notification.
     - `greenhouse-alerts` -> Dispatches environmental anomaly notices.
     - `diagnosis-events` -> Dispatches crop pathology notifications.
     - `treatment-events` -> Dispatches agricultural treatment logs.
     - `stock-events` -> Dispatches inventory & threshold warnings.
     - `forum-events` -> Dispatches community engagement alerts.

4. **REST Endpoints**:
   - `GET /analytics/sales/{farmerId}`: Retrieves aggregated sales summary.
   - `GET /analytics/sales/{farmerId}/trend`: Retrieves historical sales trend & average order value.
   - `GET /analytics/leaderboard`: Ranks farmers by gross revenue.
   - `GET /notify/history/{userId}`: Retrieves user notification history.
   - `PUT /notify/preferences/{userId}`: Updates channel & event-level preferences.
   - `DELETE /notify/history/{id}`: Deletes a specific notification log entry.

---

## 🚀 Getting Started

### Prerequisites
- **Java 17+** (OpenJDK 17 or higher)
- **Maven 3.8+** (or use included `./mvnw`)
- **Docker & Docker Compose** (Optional, for full containerized stack)

### Running Locally with In-Memory H2 / Embedded Mode
```bash
./mvnw clean spring-boot:run
```

### Running with Docker Compose (PostgreSQL + Kafka + App)
```bash
docker-compose up --build
```

---

## 📖 API Documentation & Swagger UI

Once the service is running, access Swagger UI at:
- **Interactive UI**: [http://localhost:8086/swagger-ui.html](http://localhost:8086/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8086/v3/api-docs](http://localhost:8086/v3/api-docs)

> **Note**: Click the **Authorize** button in Swagger UI and input `ceygreen-secret-api-key-2026` to authenticate API calls.

---

## 🧪 Testing the APIs

### 1. Sales Summary for Farmer
```bash
curl -X GET "http://localhost:8086/analytics/sales/FARMER-101" \
     -H "X-API-KEY: ceygreen-secret-api-key-2026"
```

### 2. Sales Trend for Farmer
```bash
curl -X GET "http://localhost:8086/analytics/sales/FARMER-101/trend" \
     -H "X-API-KEY: ceygreen-secret-api-key-2026"
```

### 3. Sales Leaderboard
```bash
curl -X GET "http://localhost:8086/analytics/leaderboard" \
     -H "X-API-KEY: ceygreen-secret-api-key-2026"
```

### 4. Notification History
```bash
curl -X GET "http://localhost:8086/notify/history/FARMER-101" \
     -H "X-API-KEY: ceygreen-secret-api-key-2026"
```

### 5. Update Notification Preference
```bash
curl -X PUT "http://localhost:8086/notify/preferences/FARMER-101" \
     -H "X-API-KEY: ceygreen-secret-api-key-2026" \
     -H "Content-Type: application/json" \
     -d '{
       "eventType": "greenhouse-alerts",
       "channel": "SMS",
       "enabled": true
     }'
```

### 6. Delete Notification History
```bash
curl -X DELETE "http://localhost:8086/notify/history/1" \
     -H "X-API-KEY: ceygreen-secret-api-key-2026"
```

---

## 🧪 Running Automated Tests
```bash
./mvnw clean test
```
