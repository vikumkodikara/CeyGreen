# Treatment & Suggestion Service — Student 3

**Port:** `8083`  
**Database:** PostgreSQL  
**Owner:** Student 3

## Overview

Turns a disease into action. Called from the client in two situations:

1. **After diagnosis** — the client takes the disease name from Student 2's Disease Detection response and calls this service with it.
2. **Direct lookup** — a farmer picks a disease by name and asks for treatment without uploading a photo.

Either way, this service only receives a disease name from the **client** — it has **no integration with, and never calls**, the Disease Detection service. Given a disease name, it returns the recommended treatment — which pesticide, fertilizer, or organic remedy to use, at what dosage, and how often.

> A trained model is optional. If the disease-to-treatment mapping is curated directly in the database, that satisfies the brief.

## Endpoints

| Method | Path | CRUD | Description |
|---|---|---|---|
| GET | `/treatments/{diseaseName}` | Read | Fetch all recommended treatments for a given disease |
| GET | `/treatments/search?crop=&severity=` | Read | Filter recommendations by crop type and/or severity |
| POST | `/treatments` | Create | Add a new treatment entry (admin/curation use) |
| PUT | `/treatments/{id}` | Update | Update dosage, frequency, or safety notes |

> `DELETE /treatments/{id}` is folded into PUT (admin sets status to inactive rather than removing agricultural history).

## Database Schema (PostgreSQL)

| Table | Key Columns | Purpose |
|---|---|---|
| `diseases` | `id, name, description` | Canonical disease list — matched by name |
| `treatments` | `id, disease_id (FK), product_name, type, dosage, frequency, safety_notes, active` | One or more treatment options per disease, tagged organic vs. chemical |

## Kafka Integration

| Topic | Role | Published When |
|---|---|---|
| `treatment-events` | **Producer** | A severe-tier treatment recommendation is generated |

## Security

- API Key verification on all endpoints
- Write endpoints check for admin/curator role from the gateway's OAuth token

## Getting Started

```bash
# Place your Spring Boot project here
# Port: 8083
# Tech: Spring Boot 3.5.x, Java 17, PostgreSQL
```
