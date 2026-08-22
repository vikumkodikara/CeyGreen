import React from 'react';
import { useWeather } from '../../hooks/useWeather';

/** Isolated from greenhouse IoT and the main API client. */
export const WeatherPanel: React.FC = () => {
  const { weather, error } = useWeather();

  return (
    <article className="panel wx">
      <h3>Weather</h3>
      <p className="wx-place">{weather?.place || 'Kandy, Sri Lanka'}</p>
      {error && !weather ? (
        <p className="wx-place" style={{ marginTop: 0 }}>{error}</p>
      ) : (
        <div className="weather-now">
          <span className="wx-temp">{weather ? `${weather.temperature}°` : '—'}</span>
          <div>
            <p>{weather?.condition || 'Loading…'}</p>
            <small>
              {weather
                ? `Humidity ${weather.humidity}% · Wind ${weather.windKmh} km/h`
                : 'Humidity — · Wind —'}
            </small>
          </div>
        </div>
      )}
    </article>
  );
};
