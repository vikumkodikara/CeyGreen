# IoT Telemetry & Control Service — Student 1

**Port:** `8082`  
**Database:** Firebase Realtime Database (optional; in-memory for local demo)  
**Owner:** Student 1 (IoT Engineer)

## Overview

Greenhouse sensory brain for the CeyGreen **web** platform. Physical setup for this student uses **1 zone + 1 ESP32** (DHT11, capacitive soil moisture, NPK). The ESP32 posts readings to this service; the service stores them, runs a rule engine, returns suggestions to the web client, and publishes urgent alerts to Kafka for Student 6.

### Hardware (Zone 1)

| Component | Measures |
|---|---|
| ESP32 | WiFi edge node |
| DHT11 | Temperature + humidity |
| Capacitive soil moisture | Soil water % |
| NPK sensor | Nitrogen, phosphorus, potassium |

Firmware: `firmware/esp32-zone1/`

### Data flow

```text
ESP32 → POST /api/iot/readings
      → Store (memory or Firebase)
      → Rule engine → suggestions
      → Kafka greenhouse-alerts (if HIGH/CRITICAL)
      → Web app GET /api/iot/suggestions/{id}
```

## Endpoints

All paths are under `/api/iot` (API Gateway routes `/api/iot/**` here).

| Method | Path | Description |
|---|---|---|
| POST | `/api/iot/greenhouses` | Create greenhouse + zones + ESP32 devices |
| POST | `/api/iot/readings` | Ingest one ESP32 reading |
| GET | `/api/iot/suggestions/{greenhouseId}` | List suggestions for the dashboard |
| PUT | `/api/iot/thresholds/{zoneId}` | Update zone rule limits |

### Auth

Send header on every API call (except actuator health):

```http
X-API-Key: ceygreen-dev-api-key
```

### Example — create 1 greenhouse / 1 zone

```json
POST /api/iot/greenhouses
{
  "name": "Greenhouse A",
  "farmerId": "farmer-001",
  "greenhouseId": "GH001",
  "zones": [
    { "zoneId": "ZONE1", "zoneName": "Zone 1", "cropType": "Tomato" }
  ]
}
```

### Example — sensor reading

```json
POST /api/iot/readings
{
  "greenhouseId": "GH001",
  "zoneId": "ZONE1",
  "temperature": 39,
  "humidity": 70,
  "soilMoisture": 35,
  "n": 10,
  "p": 12,
  "k": 9
}
```

## Rule engine (defaults)

| Condition | Suggestion | Kafka? |
|---|---|---|
| temp > 30 | Cool the greenhouse | No |
| temp > 38 | URGENT: Open roof | Yes (`HIGH`) |
| soil < 20 | Start irrigation | No |
| humidity > 90 | Open vent | No |
| N / P / K low | Apply fertilizer | No |

## Firebase schema

Used when `FIREBASE_ENABLED=true`:

| Path | Purpose |
|---|---|
| `/greenhouses/{id}` | Greenhouse blueprint |
| `/greenhouses/{id}/zones/{zoneId}` | Zone + thresholds + devices |
| `.../readings/{timestamp}` | Sensor samples |
| `.../suggestions/{timestamp}` | Rule suggestions |

## Kafka

| Topic | Role |
|---|---|
| `greenhouse-alerts` | Producer — urgent alerts for Student 6 |

## Run locally

```powershell
cd iot-service
.\mvnw.cmd spring-boot:run
```

Health: `http://localhost:8082/actuator/health`

Tests:

```powershell
.\mvnw.cmd -B -ntp test
```

## Docker

From repo root:

```powershell
docker compose up --build iot-service kafka
```

Default: `FIREBASE_ENABLED=false` (in-memory). Set Firebase env vars in `.env` when ready.

## Tech

- Java 17, Spring Boot 3.5.x
- Firebase Admin (optional)
- Spring Kafka
- API key security (`SERVICE_API_KEY`)
