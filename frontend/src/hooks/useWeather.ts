import { useEffect, useState } from 'react';
import { fetchKandyWeather, WeatherNow } from '../api/weather';

/** Sidebar-only. Failures stay local — never uses the API client / auth interceptor. */
export function useWeather() {
  const [weather, setWeather] = useState<WeatherNow | null>(null);
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;
    let inFlight: AbortController | null = null;

    const load = async () => {
      inFlight?.abort();
      const controller = new AbortController();
      inFlight = controller;
      const timeout = window.setTimeout(() => controller.abort(), 8000);
      try {
        const now = await fetchKandyWeather(controller.signal);
        if (!cancelled) {
          setWeather(now);
          setError('');
        }
      } catch (err) {
        if (cancelled || (err instanceof DOMException && err.name === 'AbortError')) return;
        if (!cancelled) setError('Weather unavailable');
      } finally {
        window.clearTimeout(timeout);
      }
    };

    load();
    const id = window.setInterval(load, 10 * 60 * 1000);
    return () => {
      cancelled = true;
      inFlight?.abort();
      window.clearInterval(id);
    };
  }, []);

  return { weather, error };
}
