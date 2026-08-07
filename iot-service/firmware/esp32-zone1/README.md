# CeyGreen ESP32 — Zone 1 (single device)

## Wiring

| Sensor | ESP32 pin |
|---|---|
| DHT11 data | GPIO 4 |
| DHT11 VCC | 3.3V |
| DHT11 GND | GND |
| Soil moisture AOUT | GPIO 34 |
| Soil moisture VCC | 3.3V |
| Soil moisture GND | GND |

NPK (RS485) is optional for first demo — sketch uses placeholder N/P/K values.

## Upload steps

1. Install Arduino IDE + ESP32 board support
2. Install libraries: DHT, Adafruit Unified Sensor, ArduinoJson
3. `copy secrets.h.example secrets.h` and edit WiFi + PC IP
4. Create greenhouse `GH001` / `ZONE1` via Postman first
5. Upload `esp32-zone1.ino`
6. Watch Serial Monitor (115200) for POST responses
