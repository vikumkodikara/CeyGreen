# Grafana Dashboards

## Overview

Grafana provides live dashboards over two genuinely time-series-shaped data sources:

| Dashboard | Sourced From |
|---|---|
| **Greenhouse Health** | Temperature / humidity / soil-moisture / NPK trend lines per zone, sourced live from Student 1's Firebase Realtime Database |
| **Farmer Sales** | Revenue and order-count trend plus the leaderboard, sourced from Student 6's `sales_summary` and `order_log` PostgreSQL tables |

## Directory Structure

```
grafana/
├── provisioning/
│   ├── dashboards/          # Dashboard JSON definitions
│   │   └── .gitkeep
│   └── datasources/         # Datasource YAML configs (PostgreSQL, Firebase)
│       └── .gitkeep
└── README.md
```

## Setup

Add Grafana as a service in the root `docker-compose.yml`:

```yaml
grafana:
  image: grafana/grafana:latest
  ports:
    - "3001:3000"
  volumes:
    - ./grafana/provisioning:/etc/grafana/provisioning
    - grafana_data:/var/lib/grafana
  environment:
    GF_SECURITY_ADMIN_USER: admin
    GF_SECURITY_ADMIN_PASSWORD: admin
```

## Stretch Goal

Extending to system-level monitoring (request latency, error rates, Kafka consumer lag) would need Prometheus scraping each service's Spring Boot Actuator endpoint.
