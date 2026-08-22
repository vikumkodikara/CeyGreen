import React, { useState } from 'react';

type SensorLocationProps = {
  greenhouseName: string;
  zoneLabel: string;
  active: boolean;
};

export const SensorLocation: React.FC<SensorLocationProps> = ({
  greenhouseName,
  zoneLabel,
  active,
}) => {
  const [photoOk, setPhotoOk] = useState(true);

  return (
    <section className="sensor-location" aria-label="Sensor location">
      <h3>Sensor Location</h3>
      <div className="sensor-location-art">
        {photoOk ? (
          <img
            className="greenhouse-photo"
            src="/iot/greenhouse-location.jpg"
            alt=""
            onError={() => setPhotoOk(false)}
          />
        ) : (
          <div className="greenhouse-photo greenhouse-photo-fallback" aria-hidden />
        )}
        <div className={`zone-chip ${active ? 'is-live' : 'is-idle'}`}>
          <span className="zone-chip-pin" aria-hidden>
            <svg width="18" height="18" viewBox="0 0 24 24">
              <path
                fill={active ? '#16a34a' : '#9ca3af'}
                d="M12 2a7 7 0 0 0-7 7c0 5.25 7 13 7 13s7-7.75 7-13a7 7 0 0 0-7-7Zm0 9.5A2.5 2.5 0 1 1 12 6a2.5 2.5 0 0 1 0 5.5Z"
              />
            </svg>
          </span>
          <div>
            <strong>{greenhouseName || 'Greenhouse'}</strong>
            <span className="zone-chip-zone">{zoneLabel}</span>
            <small>{active ? 'All sensors active' : 'Waiting for ESP32'}</small>
          </div>
        </div>
      </div>
    </section>
  );
};
