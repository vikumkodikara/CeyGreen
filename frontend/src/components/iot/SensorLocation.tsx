import React from 'react';

type SensorLocationProps = {
  zoneLabel: string;
  active: boolean;
  registered: boolean;
};

export const SensorLocation: React.FC<SensorLocationProps> = ({
  zoneLabel,
  active,
  registered,
}) => (
  <section className="sensor-location" aria-label="Sensor location">
    <h3>Sensor Location</h3>
    <div className="sensor-location-art">
      <svg viewBox="0 0 420 220" className="greenhouse-iso" role="img" aria-label="Greenhouse zone">
        <ellipse cx="210" cy="198" rx="150" ry="14" fill="#e8eee9" />
        <path d="M70 188h280v8H70z" fill="#d7e0d9" />
        <path d="M48 188c8-6 18-8 28-8h268c10 0 20 2 28 8H48Z" fill="#c5d2c8" />
        <path d="M96 188 210 78l114 110H96Z" fill="#9ec9b0" opacity="0.35" />
        <path d="M118 176 210 92l92 84H118Z" fill="#b7dcc6" />
        <path d="M210 78 324 188H210V78Z" fill="#8fbfa3" />
        <path d="M210 78 96 188H210V78Z" fill="#a8d0b8" />
        <path d="M96 188h228L210 78 96 188Z" fill="none" stroke="#8aa196" strokeWidth="3" />
        <path d="M210 78v110M153 132h114M132 154h156M118 176h184" fill="none" stroke="#ffffff" strokeWidth="2" opacity="0.55" />
        <path d="M168 176c8-22 18-38 42-38s34 16 42 38" fill="#2f8a4e" />
        <path d="M186 176c6-16 12-26 24-26s18 10 24 26" fill="#3fa05c" />
        <path d="M248 176c6-14 12-24 22-24 12 0 20 12 26 24" fill="#2d7a46" />
        <path d="M132 176c6-12 12-20 20-20 10 0 16 10 22 20" fill="#348a4c" />
        <circle cx="210" cy="128" r="5" fill="#1f6b3a" />
        <path d="M78 188c-4-10 2-16 10-14 6 10 2 14-10 14Z" fill="#4caf62" />
        <path d="M332 188c4-10-2-16-10-14-6 10-2 14 10 14Z" fill="#4caf62" />
      </svg>

      {registered && (
        <div className="zone-chip">
          <span className="zone-chip-pin" aria-hidden>
            <svg width="16" height="16" viewBox="0 0 24 24">
              <path fill="#16a34a" d="M12 2a7 7 0 0 0-7 7c0 5.25 7 13 7 13s7-7.75 7-13a7 7 0 0 0-7-7Zm0 9.5A2.5 2.5 0 1 1 12 6a2.5 2.5 0 0 1 0 5.5Z" />
            </svg>
          </span>
          <div>
            <strong>{zoneLabel}</strong>
            <small>{active ? 'All sensors active' : 'Waiting for ESP32'}</small>
          </div>
        </div>
      )}
    </div>
  </section>
);
