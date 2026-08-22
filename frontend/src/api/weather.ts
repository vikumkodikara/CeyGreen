export type WeatherNow = {
  place: string;
  temperature: number;
  humidity: number;
  windKmh: number;
  condition: string;
};

const KANDY = { lat: 7.2906, lon: 80.6337, place: 'Kandy, Sri Lanka' };

function conditionFromCode(code: number): string {
  if (code === 0) return 'Clear';
  if (code <= 2) return 'Partly Cloudy';
  if (code === 3) return 'Overcast';
  if (code <= 48) return 'Fog';
  if (code <= 57) return 'Drizzle';
  if (code <= 67) return 'Rain';
  if (code <= 77) return 'Snow';
  if (code <= 82) return 'Showers';
  if (code <= 86) return 'Snow showers';
  if (code <= 99) return 'Thunderstorm';
  return 'Unknown';
}

function finite(n: unknown, fallback = 0): number {
  const v = Number(n);
  return Number.isFinite(v) ? v : fallback;
}

export async function fetchKandyWeather(signal?: AbortSignal): Promise<WeatherNow> {
  const url =
    `https://api.open-meteo.com/v1/forecast` +
    `?latitude=${KANDY.lat}&longitude=${KANDY.lon}` +
    `&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m` +
    `&wind_speed_unit=kmh&timezone=Asia%2FColombo`;

  const res = await fetch(url, { signal });
  if (!res.ok) {
    throw new Error('Weather request failed');
  }
  const data = await res.json();
  const current = data?.current;
  if (!current) {
    throw new Error('Weather data missing');
  }

  return {
    place: KANDY.place,
    temperature: Math.round(finite(current.temperature_2m)),
    humidity: Math.round(finite(current.relative_humidity_2m)),
    windKmh: Math.round(finite(current.wind_speed_10m)),
    condition: conditionFromCode(finite(current.weather_code, -1)),
  };
}
