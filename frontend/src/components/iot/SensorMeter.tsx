import React from 'react';

type SensorMeterProps = {
  label: string;
  value: number;
  unit: string;
  min: number;
  max: number;
  color: string;
  hint: string;
  icon: React.ReactNode;
  status: string;
  statusTone?: 'ok' | 'watch' | 'alert' | 'idle';
};

function clamp(n: number, min: number, max: number) {
  return Math.min(max, Math.max(min, n));
}

export const SensorMeter: React.FC<SensorMeterProps> = ({
  label,
  value,
  unit,
  min,
  max,
  color,
  hint,
  icon,
  status,
  statusTone = 'idle',
}) => {
  const span = Math.max(0.0001, max - min);
  const pct = clamp(((Number.isFinite(value) ? value : min) - min) / span, 0, 1);
  const display = Number.isFinite(value)
    ? value.toFixed(value >= 10 || unit === '%' ? 0 : 1)
    : '0';

  return (
    <article className="sensor-meter" style={{ '--meter': color } as React.CSSProperties}>
      <div className="sensor-meter-top">
        <span className="sensor-meter-ico">{icon}</span>
        <div>
          <p className="sensor-meter-label">{label}</p>
          <small>{hint}</small>
        </div>
      </div>

      <div className="gauge-wrap" aria-hidden>
        <svg viewBox="0 0 200 128" className="gauge-svg">
          <path
            className="gauge-track"
            d="M28 108 A 72 72 0 0 1 172 108"
            fill="none"
            strokeWidth="14"
            strokeLinecap="round"
          />
          <path
            className="gauge-fill"
            d="M28 108 A 72 72 0 0 1 172 108"
            fill="none"
            stroke={color}
            strokeWidth="14"
            strokeLinecap="round"
            pathLength={100}
            strokeDasharray={`${pct * 100} 100`}
          />
        </svg>
        <div className="gauge-center">
          <strong>
            {display}
            <span>{unit}</span>
          </strong>
          <em className={`gauge-status ${statusTone}`}>{status}</em>
        </div>
        <span className="gauge-min">{min}{unit === '°C' ? '°' : ''}</span>
        <span className="gauge-max">{max}{unit === '°C' ? '°' : ''}</span>
      </div>
    </article>
  );
};
