import { LiveReading, Suggestion } from '../types/iot';

/** Hourly suggestion engine — same limits as backend ZoneThresholds. */
export const IOT_THRESHOLDS = {
  maxTemperature: 30,
  urgentMaxTemperature: 38,
  minTemperature: 15,
  minSoilMoisture: 20,
  maxHumidity: 90,
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
    add('Open roof vents for passive cooling', 'HIGH');
  } else if (reading.temperature > t.maxTemperature) {
    add('Cool the zone with the misting/water system', 'NORMAL');
  } else if (reading.temperature < t.minTemperature) {
    add('Close vents; activate heating if available', 'NORMAL');
  }

  if (reading.soilMoisture < t.minSoilMoisture) {
    add('Trigger irrigation for that zone', 'NORMAL');
  }

  if (reading.humidity > t.maxHumidity) {
    add('Open vents to reduce humidity', 'NORMAL');
  }

  if (reading.n < t.minNitrogen) add('Apply Nitrogen at the recommended dose', 'NORMAL');
  if (reading.p < t.minPhosphorus) add('Apply Phosphorus at the recommended dose', 'NORMAL');
  if (reading.k < t.minPotassium) add('Apply Potassium at the recommended dose', 'NORMAL');

  return out;
}
