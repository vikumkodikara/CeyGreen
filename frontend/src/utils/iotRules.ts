import { LiveReading, Suggestion } from '../types/iot';

/** Same defaults as ZoneThresholds in iot-service. */
export const IOT_THRESHOLDS = {
  maxTemperature: 32,
  urgentMaxTemperature: 38,
  minTemperature: 24,
  minSoilMoisture: 35,
  urgentMinSoilMoisture: 20,
  maxHumidity: 80,
  minHumidity: 60,
  minNitrogen: 10,
  minPhosphorus: 8,
  minPotassium: 8,
};

export function evaluateReading(reading: LiveReading): Suggestion[] {
  const t = IOT_THRESHOLDS;
  const out: Suggestion[] = [];
  const zoneId = reading.zoneId || 'ZONE1';

  const add = (message: string, severity: 'NORMAL' | 'HIGH') => {
    out.push({ zoneId, message, severity, resolved: false });
  };

  if (reading.temperature > t.urgentMaxTemperature) {
    add('URGENT: Open roof — temperature critical', 'HIGH');
  } else if (reading.temperature > t.maxTemperature) {
    add('Cool the greenhouse', 'NORMAL');
  } else if (reading.temperature < t.minTemperature) {
    add('Warm the greenhouse', 'NORMAL');
  }

  if (reading.soilMoisture < t.urgentMinSoilMoisture) {
    add('Start irrigation — soil critically dry', 'HIGH');
  } else if (reading.soilMoisture < t.minSoilMoisture) {
    add('Start irrigation', 'NORMAL');
  }

  if (reading.humidity > t.maxHumidity) {
    add('Open vent', 'NORMAL');
  } else if (reading.humidity < t.minHumidity) {
    add('Raise humidity', 'NORMAL');
  }

  if (reading.n < t.minNitrogen) add('Apply Nitrogen fertilizer', 'NORMAL');
  if (reading.p < t.minPhosphorus) add('Apply Phosphorus fertilizer', 'NORMAL');
  if (reading.k < t.minPotassium) add('Apply Potassium fertilizer', 'NORMAL');

  return out;
}
