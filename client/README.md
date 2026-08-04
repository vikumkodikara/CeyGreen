# CeyGreen Client Application

**Owner:** Team (shared)

## Overview

The client application that communicates with all backend microservices exclusively through the **API Gateway** (`http://localhost:8080/api/...`).

The client **orchestrates cross-service flows** on behalf of the user. For example, after receiving a diagnosis result from Student 2's service, the client separately calls Student 3's Treatment service — the backend services never call each other.

## Key Flows

1. **Registration & Login** → `POST /api/users/register`, `POST /api/users/login`
2. **Disease Diagnosis** → `POST /api/diagnosis/upload` → client receives disease name → `GET /api/treatments/{diseaseName}`
3. **Greenhouse Monitoring** → `GET /api/iot/suggestions/{greenhouseId}` + Firebase Realtime DB listener for live readings
4. **Marketplace** → `GET /api/products`, `POST /api/orders/checkout`
5. **Forum** → `GET /api/forum/posts`, `POST /api/forum/posts/{id}/replies`
6. **Analytics** → `GET /api/analytics/sales/{farmerId}`

## Tech Stack

To be decided by the team. Options:
- React / Next.js / Vite (JavaScript/TypeScript)
- Angular
- Flutter (if mobile)

## Getting Started

```bash
# Place your client application here
# All API calls go through the Gateway at http://localhost:8080/api/...
# Authorization: Bearer <token> on every authenticated request
```
