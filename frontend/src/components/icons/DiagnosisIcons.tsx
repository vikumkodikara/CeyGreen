import React from 'react';

type IconProps = { size?: number; className?: string; color?: string };

const stroke = {
  fill: 'none',
  stroke: 'currentColor',
  strokeWidth: 1.85,
  strokeLinecap: 'round' as const,
  strokeLinejoin: 'round' as const,
};

// Microscope for Pathology & Etiology
export const IconMicroscope: React.FC<IconProps> = ({ size = 20, className }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
    <path {...stroke} d="M6 18h8M10 22v-4M9 14h2M14 9l3 3M9 4l5 5-2 2-5-5 2-2Z" />
    <path {...stroke} d="M12 12a5 5 0 0 0-5 5v1" />
  </svg>
);

// Eye / Search Scan for Visual Symptoms
export const IconEyeScan: React.FC<IconProps> = ({ size = 20, className }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
    <path {...stroke} d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" />
    <circle cx="12" cy="12" r="3" {...stroke} />
    <path {...stroke} d="M12 9v6M9 12h6" />
  </svg>
);

// Climate / Thermometer Gauge for Microclimate
export const IconThermometerGauge: React.FC<IconProps> = ({ size = 20, className }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
    <path {...stroke} d="M14 4v10.54a4 4 0 1 1-4 0V4a2 2 0 0 1 4 0Z" />
    <circle cx="12" cy="17" r="1.5" fill="currentColor" />
  </svg>
);

// Shield Check for Quarantine & IPM
export const IconShieldCheck: React.FC<IconProps> = ({ size = 20, className }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
    <path {...stroke} d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10Z" />
    <path {...stroke} d="m9 12 2 2 4-4" />
  </svg>
);

// Brain / Sparkle for AI Agronomist Action Report
export const IconBrainSparkle: React.FC<IconProps> = ({ size = 20, className }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
    <path {...stroke} d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83" />
    <circle cx="12" cy="12" r="4" {...stroke} />
  </svg>
);

// Neural Network Grid Node Icon
export const IconNeuralNet: React.FC<IconProps> = ({ size = 20, className }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
    <circle cx="6" cy="6" r="2.5" {...stroke} />
    <circle cx="6" cy="18" r="2.5" {...stroke} />
    <circle cx="18" cy="12" r="2.5" {...stroke} />
    <path {...stroke} d="M8.5 7.5l7 3.5M8.5 16.5l7-3.5M6 8.5v7" />
  </svg>
);

// Camera / Upload Scan Icon
export const IconCameraScan: React.FC<IconProps> = ({ size = 24, className }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
    <path {...stroke} d="M4 8V6a2 2 0 0 1 2-2h2M16 4h2a2 2 0 0 1 2 2v2M20 16v2a2 2 0 0 1-2 2h-2M8 20H6a2 2 0 0 1-2-2v-2" />
    <circle cx="12" cy="12" r="3.5" {...stroke} />
    <path {...stroke} d="M3 12h18" strokeDasharray="2 2" stroke="currentColor" />
  </svg>
);

// Plant Leaf Icon
export const IconBotanyLeaf: React.FC<IconProps> = ({ size = 20, className }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
    <path {...stroke} d="M11 20A7 7 0 0 1 4 13C4 6 11 3 20 3c0 9-3 16-9 17Z" />
    <path {...stroke} d="M20 3L11 12" />
  </svg>
);

// Copy Icon
export const IconCopyDoc: React.FC<IconProps> = ({ size = 16, className }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
    <rect x="9" y="9" width="13" height="13" rx="2" ry="2" {...stroke} />
    <path {...stroke} d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1" />
  </svg>
);

// Print / Export Icon
export const IconPrintExport: React.FC<IconProps> = ({ size = 16, className }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
    <path {...stroke} d="M6 9V2h12v7M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2" />
    <path {...stroke} d="M6 14h12v8H6z" />
  </svg>
);

// Danger Warning Triangle
export const IconAlertSign: React.FC<IconProps> = ({ size = 18, className }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
    <path {...stroke} d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z" />
    <path {...stroke} d="M12 9v4M12 17h.01" />
  </svg>
);

// Clean Crop Icons
export const CropIcons: Record<string, React.FC<IconProps>> = {
  Tomato: ({ size = 20, className }) => (
    <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
      <circle cx="12" cy="14" r="8" {...stroke} />
      <path {...stroke} d="M12 6V2M9 5c2 1 4 1 6 0M8 3c1 2 3 3 4 3s3-1 4-3" />
    </svg>
  ),
  Potato: ({ size = 20, className }) => (
    <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
      <path {...stroke} d="M12 4c5 0 9 3.5 9 8s-4 8.5-9 8.5S3 17 3 12 7 4 12 4Z" />
      <circle cx="8" cy="11" r="1" fill="currentColor" />
      <circle cx="15" cy="10" r="1" fill="currentColor" />
      <circle cx="12" cy="15" r="1" fill="currentColor" />
    </svg>
  ),
  Pepper: ({ size = 20, className }) => (
    <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
      <path {...stroke} d="M12 3v3M8 6h8c2 0 3 2 3 4 0 6-3 10-7 10s-7-4-7-10c0-2 1-4 3-4Z" />
      <path {...stroke} d="M10 6c1 3 1 10 0 13" />
    </svg>
  ),
  Grape: ({ size = 20, className }) => (
    <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
      <path {...stroke} d="M12 2v4M10 3c2 1 4 1 5 0" />
      <circle cx="9" cy="8" r="2.5" {...stroke} />
      <circle cx="15" cy="8" r="2.5" {...stroke} />
      <circle cx="12" cy="12" r="2.5" {...stroke} />
      <circle cx="7" cy="13" r="2.5" {...stroke} />
      <circle cx="17" cy="13" r="2.5" {...stroke} />
      <circle cx="10" cy="17" r="2.5" {...stroke} />
      <circle cx="14" cy="17" r="2.5" {...stroke} />
      <circle cx="12" cy="21" r="2" {...stroke} />
    </svg>
  ),
  Strawberry: ({ size = 20, className }) => (
    <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
      <path {...stroke} d="M12 3v3M7 5c3 1 7 1 10 0" />
      <path {...stroke} d="M6 7c-2 4-1 9 6 14 7-5 8-10 6-14-3 1-9 1-12 0Z" />
      <circle cx="10" cy="11" r="0.8" fill="currentColor" />
      <circle cx="14" cy="11" r="0.8" fill="currentColor" />
      <circle cx="12" cy="15" r="0.8" fill="currentColor" />
    </svg>
  ),
  Chillie: ({ size = 20, className }) => (
    <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
      <path {...stroke} d="M16 3c-1 2-2 3-4 3M12 6c-2 0-4 1-5 3-2 3-2 7 1 10 3 3 8 3 10-1 2-4 1-8-2-10-1-1-3-2-4-2Z" />
    </svg>
  ),
  Chilli: ({ size = 20, className }) => (
    <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
      <path {...stroke} d="M16 3c-1 2-2 3-4 3M12 6c-2 0-4 1-5 3-2 3-2 7 1 10 3 3 8 3 10-1 2-4 1-8-2-10-1-1-3-2-4-2Z" />
    </svg>
  ),
  Apple: ({ size = 20, className }) => (
    <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
      <path {...stroke} d="M12 2c1 2 1 4-1 6M12 6c-2-2-5-1-6 2-2 5 0 12 6 12s8-7 6-12c-1-3-4-4-6-2Z" />
    </svg>
  ),
  Corn: ({ size = 20, className }) => (
    <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
      <path {...stroke} d="M12 2C8 2 7 6 7 12c0 6 3 9 5 10 2-1 5-4 5-10 0-6-1-10-5-10Z" />
      <path {...stroke} d="M7 8h10M7 12h10M7 16h10" />
      <path {...stroke} d="M12 2v20" />
    </svg>
  ),
};
