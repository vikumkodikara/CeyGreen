import React from 'react';

type SensorMeterProps = {
  label: string;
  value: number;
  unit: string;
  min?: number;
  max?: number;
  color: string;
  hint: string;
  icon: React.ReactNode;
};

export const SensorMeter: React.FC<SensorMeterProps> = ({
  label,
  value,
  unit,
  color,
  hint,
  icon,
}) => {
  const idle = !Number.isFinite(value);
  const display = idle ? '—' : value.toFixed(value >= 10 ? 0 : 1);

  return (
    <article className="sensor-meter" style={{ '--meter': color } as React.CSSProperties}>
      <div className="sensor-meter-top">
        <span className="sensor-meter-ico">{icon}</span>
        <div>
          <p className="sensor-meter-label">{label}</p>
          <small>{hint}</small>
        </div>
      </div>
      <p className="sensor-meter-value">
        <strong>{display}</strong>
        <span>{unit}</span>
      </p>
    </article>
  );
};
