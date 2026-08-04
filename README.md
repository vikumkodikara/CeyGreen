# CeyGreen — Microservices-Based Greenhouse Management System

CeyGreen is an end-to-end, distributed microservices platform that modernizes greenhouse farming by combining IoT sensing, machine-learning-assisted plant diagnostics, and community and marketplace features on a single platform. Each greenhouse is represented as a digital blueprint — a virtual layout of zones with ESP32-based IoT devices placed inside it that measure temperature, humidity, soil moisture, and soil NPK nutrient levels. Readings are captured hour by hour, streamed to Firebase for real-time updates, and turned into plain-language suggestions for the farmer.

All synchronous, user-facing traffic is routed through a central **API Gateway**, while an **Apache Kafka** event backbone connects the services that generate events to the service that consumes them. **Grafana** provides live dashboards over both the environmental and the sales data, and the full system is containerized with **Docker** and deployed to a single **AWS EC2** instance.

---

## Architecture at a Glan```mermaid
graph TD
    Client["Client App"] --> GW["API Gateway (:8080)<br/>Student 2"]
    GW -->|"Token Bucket"| Redis[("Redis<br/>(Rate Limiting)")]

    GW --> S1["IoT Telemetry & Control (:8082)<br/>Student 1"]
    GW --> S2["User Mgmt & Diagnosis (:8081)<br/>Student 2"]
    GW --> S3["Treatment & Suggestion (:8083)<br/>Student 3"]
    GW --> S4["E-Commerce Marketplace (:8084)<br/>Student 4"]
    GW --> S5["Community Forum (:8085)<br/>Student 5"]

    S1 -.-> DB1[("Firebase Realtime DB<br/>(hourly readings & suggestions)")]
    S2 -.-> DB2_PG[("PostgreSQL<br/>(users)")]
    S2 -.-> DB2_MG[("MongoDB<br/>(diagnoses)")]
    S3 -.-> DB3[("PostgreSQL<br/>(treatments)")]
    S4 -.-> DB4[("PostgreSQL<br/>(products & orders)")]
    S5 -.-> DB5[("MongoDB<br/>(posts & replies)")]

    S1 -->|"greenhouse-alerts"| Kafka
    S2 -->|"diagnosis-events"| Kafka
    S3 -->|"treatment-events"| Kafka
    S4 -->|"order-events & stock-events"| Kafka
    S5 -->|"forum-events"| Kafka

    Kafka["Apache Kafka (KRaft mode)<br/>Event Backbone"] --> S6["Sales Analytics & Notifications (:8086)<br/>Student 6"]
    S6 -.-> DB6[("PostgreSQL<br/>(sales summary & notify log)")]
```

```text
                                  ┌─────────────────────┐
                                  │     Client App      │
                                  └──────────┬──────────┘
                                             │
                                  ┌──────────▼──────────┐
                                  │  API Gateway :8080  │───────► [(Redis Cache)]
                                  │ JWT · CORS · Rate   │          (Token Bucket)
                                  └──┬──┬──┬──┬──┬──┬───┘
                                     │  │  │  │  │  │
           ┌─────────────────────────┘  │  │  │  │  └─────────────────────────┐
           │              ┌─────────────┘  │  │  └─────────────┐               │
           ▼              ▼                ▼  ▼                ▼               ▼
   ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
   │ IoT Service  │ │ User Mgmt &  │ │  Treatment   │ │  E-Commerce  │ │    Forum     │
   │    :8082     │ │  Diagnosis   │ │   Service    │ │ Marketplace  │ │   Service    │
   │  Student 1   │ │ :8081 (Stud2)│ │:8083 (Stud3) │ │:8084 (Stud4) │ │:8085 (Stud5) │
   └──────┬───────┘ └──────┬───────┘ └──────┬───────┘ └──────┬───────┘ └──────┬───────┘
          │                │                │                │                │
          ▼                ▼                ▼                ▼                ▼
   [(Firebase DB)]   [(PostgreSQL)]   [(PostgreSQL)]   [(PostgreSQL)]    [(MongoDB)]
                     [( MongoDB  )]
          │                │                │                │                │
          ▼                ▼                ▼                ▼                ▼
   ┌──────────────────────────────────────────────────────────────────────────────────┐
   │                            Apache Kafka (KRaft mode)                             │
   │  greenhouse-alerts · diagnosis-events · treatment-events · order/stock/forum   │
   └────────────────────────────────────────┬─────────────────────────────────────────┘
                                            │
                                  ┌─────────▼──────────┐
                                  │   Analytics &      │
                                  │Notification :8086  │
                                  │    Student 6       │
                                  └─────────┬──────────┘
                                            │
                                            ▼
                                      [(PostgreSQL)]
```     │
         ▼               ▼            ▼          ▼               ▼
  ┌─────────────────────────────────────────────────────────────────────┐
  │                     Apache Kafka (KRaft mode)                       │
  │  greenhouse-alerts · diagnosis-events · treatment-events            │
  │  order-events · stock-events · forum-events                         │
  └─────────────────────────────┬───────────────────────────────────────┘
                                │
                      ┌─────────▼──────────┐
                      │ Analytics &         │
                      │ Notification :8086  │
                      │    Student 6        │
                      └────────────────────┘
```

---

## Team & Service Ownership

| Student | Microservice | Port | Database | Status |
|---|---|---|---|---|
| 1 | [IoT Telemetry & Control Service](iot-service/) | 8082 | Firebase Realtime DB | Placeholder |
| 2 | [API Gateway](api-gateway/) + [User Mgmt & Disease Detection](user-diagnosis-service/) | 8080, 8081 | PostgreSQL + MongoDB | ✅ Complete |
| 3 | [Treatment & Suggestion Service](treatment-service/) | 8083 | PostgreSQL | Placeholder |
| 4 | [E-Commerce Marketplace Service](ecommerce-service/) | 8084 | PostgreSQL | Placeholder |
| 5 | [Community Forum Service](forum-service/) | 8085 | MongoDB | Placeholder |
| 6 | [Sales Analytics & Notification Service](analytics-notification-service/) | 8086 | PostgreSQL | Placeholder |

---

## Service Independence — Hard Rule

> No internal service calls another service's REST API. All cross-service coordination is either **client-orchestrated** or **event-driven via Kafka**.

- **No REST call** ever goes from one internal microservice to another.
- **Diagnosis → Treatment is client-orchestrated**, not service-to-service. Disease Detection returns a predicted disease name to the client; the client then separately calls Treatment & Suggestion's `GET /treatments/{diseaseName}` itself.
- **Identity is a token claim, not a lookup.** Every service trusts the `farmerId`/`buyerId` embedded in the OAuth 2.0 access token that the Gateway validates and forwards. No service calls User Management to "ask" who a user is.
- **Each service owns its data exclusively** — database-per-service, with no exceptions.
- **Kafka consumption is not an availability dependency.** If Student 6's service is down, the five producers keep working; events queue up.
- **The Gateway is the one intentional exception** — it's an entry proxy, not a peer service.

**Rule of thumb:** if you can't stop any one service (other than the Gateway) and have the other five keep working, something is coupled that shouldn't be.

---

## Two Communication Styles

| Style | How | Example |
|---|---|---|
| **Synchronous (REST via Gateway)** | Client → Gateway → Service | Browsing products, uploading a diagnosis image, reading a forum thread |
| **Asynchronous (Kafka)** | Service → Kafka topic → Consumer | Greenhouse alert, order placed, stock low, new forum reply |

---

## Polyglot Persistence

| Database | Services | Why |
|---|---|---|
| **PostgreSQL** | User Management, Treatment & Suggestion, E-Commerce, Sales Analytics & Notification | Relational data: users, treatments, products, orders, analytics |
| **MongoDB** | Disease Detection, Community Forum | Document data: diagnoses, forum posts with nested replies |
| **Firebase Realtime DB** | IoT Telemetry & Control | Time-series / live-sync: hourly sensor readings, client dashboard updates without polling |

---

## Kafka Topics

| Topic | Producer | Consumer | Published When |
|---|---|---|---|
| `greenhouse-alerts` | Student 1 (IoT) | Student 6 | A reading crosses an urgent threshold |
| `diagnosis-events` | Student 2 (Disease Detection) | Student 6 | A diagnosis finishes processing |
| `treatment-events` | Student 3 (Treatment & Suggestion) | Student 6 | A severe-tier treatment recommendation |
| `order-events` | Student 4 (E-Commerce) | Student 6 | A buyer completes checkout |
| `stock-events` | Student 4 (E-Commerce) | Student 6 | Stock runs low or is refilled |
| `forum-events` | Student 5 (Forum) | Student 6 | A new reply is posted to a thread |

Kafka runs in **KRaft mode** (no separate ZooKeeper container).

---

## Grafana Dashboards

| Dashboard | Sourced From |
|---|---|
| **Greenhouse Health** | Temperature / humidity / soil-moisture / NPK trend lines per zone, sourced live from Firebase Realtime DB (Student 1) |
| **Farmer Sales** | Revenue and order-count trend plus leaderboard, sourced from `sales_summary` and `order_log` PostgreSQL tables (Student 6) |

Extending to system-level monitoring (request latency, error rates, Kafka consumer lag) would additionally need Prometheus scraping each service's Spring Boot Actuator endpoint — reasonable stretch goal for future work.

---

## Prerequisites

- **Docker Desktop** (Compose v2)
- **Java 17+** (only needed for local Maven builds / tests)
- Optional: the Maven wrappers under each project (`mvnw` / `mvnw.cmd`)

---

## Quick Start

```bash
cp .env.example .env        # adjust if needed
docker compose up --build    # starts all infrastructure + available services
```

This starts PostgreSQL, MongoDB, Redis, Kafka (KRaft), `user-diagnosis-service`, and `api-gateway`. Teammate services will start once their code is added.

### Useful URLs

| What | URL |
|---|---|
| Gateway health | http://localhost:8080/actuator/health |
| User & Diagnosis service health | http://localhost:8081/actuator/health |
| Swagger UI (User & Diagnosis) | http://localhost:8081/swagger-ui.html |
| OpenAPI JSON | http://localhost:8081/v3/api-docs |
| JWKS endpoint | http://localhost:8081/oauth2/jwks |

Client traffic should go through the Gateway (`http://localhost:8080/api/...`).

---

## Student 2 — Detailed Documentation

Student 2 (the repo creator) owns three closely related pieces:

### API Gateway (port 8080)

- **OAuth 2.0** authentication/authorization — RS256 JWT validation
- **CORS** configuration from `CORS_ALLOWED_ORIGINS` env var
- **Rate limiting** — Redis-backed token bucket, configurable via `RATE_LIMIT_REQUESTS_PER_MIN`
- **Routing table** — maps `/api/**` paths to each internal microservice
- **Identity injection** — strips client-supplied identity headers, injects decoded `X-User-Id`, `X-Farmer-Id`, `X-User-Role`, plus the shared `X-API-Key`

### User Management (port 8081, PostgreSQL)

| Method | Path | Auth |
|---|---|---|
| POST | `/users/register` | public |
| POST | `/users/login` | public |
| GET | `/users/{id}` | bearer + API key |
| PUT | `/users/{id}` | bearer + API key (own profile only) |

Via gateway: prefix with `/api`.

### Disease Detection (port 8081, MongoDB)

| Method | Path | Auth |
|---|---|---|
| POST | `/diagnosis/upload` | bearer + API key |
| GET | `/diagnosis/{id}` | bearer + API key |
| GET | `/diagnosis/history/{farmerId}` | bearer + API key |
| DELETE | `/diagnosis/{id}` | bearer + API key |

Upload is `multipart/form-data` with parts `image`, `farmerId`, `cropType`.

ML inference runs behind `DiseaseClassifier`. Default implementation is `MockDiseaseClassifier` (`disease.classifier.impl=mock`). An ONNX stub and the `onnxruntime` dependency are present for a later model drop-in.

Low confidence (below `disease.classifier.confidence-threshold`, default `0.6`) returns `"uncertain - consult an expert"`.

### Security

- **OAuth 2.0**: Register → login → receive RS256 access token → `Authorization: Bearer <token>` on every gateway call
- **API Key (defense in depth)**: Every call to `user-diagnosis-service` except register/login must carry `X-API-Key: ceygreen-dev-api-key`. The gateway injects this automatically. Calling the service on port 8081 directly without it is rejected with 401.
- **Test credentials**:

| Field | Value |
|---|---|
| API key | `ceygreen-dev-api-key` |
| Sample register | see [`docs/student2-api.http`](docs/student2-api.http) |
| Dev JWT keys | `*/src/main/resources/keys/dev-*.pem` (local only) |

### API Collection

- HTTP file: [`docs/student2-api.http`](docs/student2-api.http)
- Postman: [`docs/CeyGreen-Student2.postman_collection.json`](docs/CeyGreen-Student2.postman_collection.json)

### Local Tests

```bash
# from each project directory
./mvnw test          # macOS / Linux
.\mvnw.cmd test      # Windows
```

Integration tests use Testcontainers (Postgres, Mongo, Kafka, Redis). Docker must be running.

---

## All Service Endpoints — Summary

### Student 1 — IoT Telemetry & Control

| Method | Path | Description |
|---|---|---|
| POST | `/iot/greenhouses` | Register a greenhouse blueprint |
| POST | `/iot/readings` | Ingest hourly ESP32 reading |
| GET | `/iot/suggestions/{greenhouseId}` | Current recommendations per zone |
| PUT | `/iot/thresholds/{zoneId}` | Adjust rule-engine thresholds |

### Student 2 — User Management + Disease Detection

See [detailed section above](#student-2--detailed-documentation).

### Student 3 — Treatment & Suggestion

| Method | Path | Description |
|---|---|---|
| GET | `/treatments/{diseaseName}` | Treatments for a given disease |
| GET | `/treatments/search?crop=&severity=` | Filter by crop type and severity |
| POST | `/treatments` | Add treatment entry (admin) |
| PUT | `/treatments/{id}` | Update dosage/frequency/safety notes |

### Student 4 — E-Commerce Marketplace

| Method | Path | Description |
|---|---|---|
| GET | `/products` | List active harvest listings |
| GET | `/products/{id}` | Single listing detail |
| POST | `/products` | Create a listing (farmer only) |
| PUT | `/products/{id}` | Update price/quantity/availability |
| POST | `/orders/checkout` | Buyer checkout |

### Student 5 — Community Forum

| Method | Path | Description |
|---|---|---|
| GET | `/forum/posts` | List posts (paginated, filterable) |
| POST | `/forum/posts` | Create a discussion post |
| GET | `/forum/posts/{id}` | Post with full reply thread |
| POST | `/forum/posts/{id}/replies` | Add a reply |
| DELETE | `/forum/posts/{id}` | Remove post (author/admin) |

### Student 6 — Sales Analytics & Notifications

| Method | Path | Description |
|---|---|---|
| GET | `/analytics/sales/{farmerId}` | Farmer sales summary |
| GET | `/analytics/sales/{farmerId}/trend` | Revenue/order trend |
| GET | `/analytics/leaderboard` | Top farmers and products |
| GET | `/notify/history/{userId}` | Past notifications |
| PUT | `/notify/preferences/{userId}` | Notification preferences |
| DELETE | `/notify/history/{id}` | Clear a notification |

---

## Deployment — AWS EC2

The entire stack is deployed to a single **AWS EC2** instance running the same `docker-compose.yml` used for local development.

**Practical notes:**
- Size the instance for the full stack (six services + Kafka + multiple databases + Grafana) — **t3.medium or larger**
- Open only the ports needed in the EC2 security group: Gateway port for the client, Grafana port for dashboards. Internal services and databases should **not** be exposed publicly
- Firebase Realtime Database is a managed external service, not a container
- For the report: capture `docker compose ps` output on EC2 alongside client screenshots as deployment evidence

---

## Project Layout

```text
CeyGreen/
├── api-gateway/                          # Student 2 — Gateway (port 8080)
├── user-diagnosis-service/               # Student 2 — Users + Diagnosis (port 8081)
├── iot-service/                          # Student 1 — IoT (port 8082)
├── treatment-service/                    # Student 3 — Treatments (port 8083)
├── ecommerce-service/                    # Student 4 — E-Commerce (port 8084)
├── forum-service/                        # Student 5 — Forum (port 8085)
├── analytics-notification-service/       # Student 6 — Analytics & Notifications (port 8086)
├── client/                               # Client application
├── grafana/                              # Grafana dashboard provisioning
├── docs/                                 # API collections & test files
├── docker-compose.yml                    # Full stack orchestration
├── .env.example                          # Environment variable template
└── README.md                             # This file
```

---

## Tech Stack

| Component | Version |
|---|---|
| Java | 17 |
| Spring Boot | 3.5.x |
| Spring Cloud | 2025.0.3 (Northfields) |
| ONNX Runtime | 1.28.0 |
| Kafka | KRaft mode (no ZooKeeper) |
| PostgreSQL | 16 |
| MongoDB | 7.0 |
| Redis | 7 |
| Docker Compose | v2 |
| Grafana | Latest |

---

## Optional Future Enhancements

Not required for the current 6-member scope — useful for the report's future-work section.

### Option A — Weather Integration Service

| Method | Path | Description |
|---|---|---|
| GET | `/weather/forecast/{greenhouseId}` | Current forecast for a greenhouse's location |
| GET | `/weather/recommendations/{greenhouseId}` | Combine forecast with IoT readings for forward-looking guidance |

### Option B — Farmer Profile & Certification Service

| Method | Path | Description |
|---|---|---|
| GET | `/profiles/{farmerId}/certifications` | Retrieve a farmer's certification badges |
| POST | `/profiles/{farmerId}/certifications` | Add a certification badge (e.g. "Certified Organic") |

