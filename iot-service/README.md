# IoT Telemetry & Control Service — Student 1

**Port:** `8082`  
**Database:** Firebase Realtime Database  
**Owner:** Student 1 (IoT Engineer)

## Overview

The platform's sensory system. Each greenhouse is modelled as a **blueprint** — a digital layout broken into zones. Every zone holds an **ESP32** microcontroller wired to a DHT11 (temperature + humidity), a capacitive soil-moisture sensor, and an NPK soil-nutrient sensor. The ESP32 wakes up hourly, takes a reading, and pushes it to this service, which writes it to Firebase and evaluates it against configurable zone rules.

### Hardware

| Component | Measures |
|---|---|
| ESP32 | Edge microcontroller, WiFi, 1 JSON/hour |
| DHT11 | Temperature + humidity |
| Capacitive soil-moisture sensor | Soil water content |
| NPK sensor | Nitrogen, phosphorus, potassium |

### Data Flow

```
ESP32 → POST /iot/readings → Firebase Realtime DB → Rule engine → Suggestion (+ Kafka if severe)
```

## Endpoints

| Method | Path | CRUD | Description |
|---|---|---|---|
| POST | `/iot/greenhouses` | Create | Register a greenhouse blueprint with zones and devices |
| POST | `/iot/readings` | Create | Ingest one hourly reading from a zone's ESP32 |
| GET | `/iot/suggestions/{greenhouseId}` | Read | Current recommended actions per zone |
| PUT | `/iot/thresholds/{zoneId}` | Update | Adjust rule-engine thresholds for a zone |

## Firebase Schema

| Path | Shape | Purpose |
|---|---|---|
| `/greenhouses/{id}` | `name, farmerId, createdAt` | One blueprint per greenhouse |
| `/greenhouses/{id}/zones/{zoneId}` | `zoneName, cropType, thresholds{}` | Sub-areas with rule thresholds |
| `/greenhouses/{id}/zones/{zoneId}/devices/{deviceId}` | `deviceType, deviceCode` | ESP32 sensor node |
| `/greenhouses/{id}/zones/{zoneId}/readings/{timestamp}` | `temperature, humidity, soilMoisture, n, p, k` | Live hourly readings |
| `/greenhouses/{id}/zones/{zoneId}/suggestions/{timestamp}` | `message, severity, resolved` | Generated suggestions |

## Kafka Integration

| Topic | Role | Published When |
|---|---|---|
| `greenhouse-alerts` | **Producer** | A reading crosses an urgent threshold |

## Security

- API Key verification on every request
- Firebase security rules enforce farmer-scoped writes

## Getting Started

```bash
# Place your Spring Boot project here
# Port: 8082
# Tech: Spring Boot 3.5.x, Java 17, Firebase Realtime Database
```
