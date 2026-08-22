# Grafana Dashboards

Greenhouse Health visualizes Student 1 IoT telemetry: temperature, humidity, soil moisture, NPK, and zone samples.

## Data path

```text
ESP32  →  IoT service (:8082)  →  Firebase Realtime DB (or RAM for local demo)
                                    ↓
                         Grafana Infinity plugin
                                    ↓
                      Greenhouse Health dashboard (:3001)
```

Firebase rules require farmer auth, so Grafana does not call Firebase in the browser. It calls `GET /iot/grafana/series` on the IoT service (API key). That endpoint reads the **same** rows the service already wrote to Firebase.

## Run

```powershell
docker compose up --build iot-service kafka grafana
```

The plugin error happens when Grafana still loads Infinity **4.0.0** from an old Docker volume. This image bakes in Infinity **2.10.0** under `/opt/plugins`, so that old copy is ignored.

Rebuild Grafana (Docker Desktop can stay on):

```powershell
docker compose up -d --build --force-recreate grafana
```

Open [http://localhost:3001](http://localhost:3001) — user `admin` / password `admin`.

Open **CeyGreen → Greenhouse Health**. Set **Greenhouse ID** and **Farmer ID** to the values you used on the Greenhouse page, then wait for ESP32 (or Postman) readings.

## Stretch goal

System metrics (latency, Kafka lag) would need Prometheus scraping Spring Boot Actuator. That is not this dashboard.
