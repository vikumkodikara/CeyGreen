/*
 * CeyGreen Zone 1 — ESP32 sensor node
 *
 * Hardware (1 zone):
 *   - ESP32 DevKit
 *   - DHT11  (temp + humidity)  -> GPIO 4
 *   - Capacitive soil moisture  -> GPIO 34 (ADC)
 *   - NPK (optional / stub)     -> fill readNpk() when wired
 *
 * Libraries (Arduino Library Manager):
 *   - DHT sensor library (Adafruit)
 *   - Adafruit Unified Sensor
 *   - ArduinoJson
 *
 * Setup:
 *   1. Copy secrets.h.example -> secrets.h and fill WiFi + API values
 *   2. Select board: ESP32 Dev Module
 *   3. Upload this sketch
 */

#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <DHT.h>
#include "secrets.h"

#define DHT_PIN 4
#define DHT_TYPE DHT11
#define SOIL_PIN 34

// For demo use 30 seconds. For production use 3600000UL (1 hour).
#define READ_INTERVAL_MS 30000UL

DHT dht(DHT_PIN, DHT_TYPE);

void setup() {
  Serial.begin(115200);
  dht.begin();

  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println();
  Serial.print("WiFi OK: ");
  Serial.println(WiFi.localIP());
}

void loop() {
  float temperature = dht.readTemperature();
  float humidity = dht.readHumidity();
  int soilRaw = analogRead(SOIL_PIN);
  // Map ADC to ~0-100%. Calibrate dry/wet for your sensor.
  float soilMoisture = constrain(map(soilRaw, 3000, 1200, 0, 100), 0, 100);

  float n = 0, p = 0, k = 0;
  readNpk(n, p, k);

  if (isnan(temperature) || isnan(humidity)) {
    Serial.println("DHT11 read failed");
  } else {
    postReading(temperature, humidity, soilMoisture, n, p, k);
  }

  delay(READ_INTERVAL_MS);
}

/**
 * Placeholder for RS485 NPK sensor.
 * Replace with your Modbus/UART read when hardware is connected.
 */
void readNpk(float &n, float &p, float &k) {
  n = 12;
  p = 10;
  k = 11;
}

void postReading(float temperature, float humidity, float soilMoisture,
                 float n, float p, float k) {
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("WiFi lost — skip POST");
    return;
  }

  HTTPClient http;
  String url = String(IOT_BASE_URL) + "/api/iot/readings";
  http.begin(url);
  http.addHeader("Content-Type", "application/json");
  http.addHeader("X-API-Key", API_KEY);

  JsonDocument doc;
  doc["greenhouseId"] = GREENHOUSE_ID;
  doc["zoneId"] = ZONE_ID;
  doc["temperature"] = temperature;
  doc["humidity"] = humidity;
  doc["soilMoisture"] = soilMoisture;
  doc["n"] = n;
  doc["p"] = p;
  doc["k"] = k;

  String body;
  serializeJson(doc, body);
  Serial.println(body);

  int code = http.POST(body);
  Serial.printf("POST /api/iot/readings -> %d\n", code);
  if (code > 0) {
    Serial.println(http.getString());
  }
  http.end();
}
