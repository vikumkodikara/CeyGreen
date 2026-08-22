import React from 'react';

type IconProps = { size?: number; className?: string };

const stroke = {
  fill: 'none',
  stroke: 'currentColor',
  strokeWidth: 1.75,
  strokeLinecap: 'round' as const,
  strokeLinejoin: 'round' as const,
};

export const IconLeaf: React.FC<IconProps> = ({ size = 22, className }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" className={className} aria-hidden>
    <path {...stroke} d="M5 19c8-1 13-7 14-14-7 1-13 6-14 14Z" />
    <path {...stroke} d="M8 16c2-3 5-5 9-7" />
  </svg>
);

export const IconHome: React.FC<IconProps> = ({ size = 18 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M4 11.5 12 4l8 7.5" />
    <path {...stroke} d="M7 10.5V20h10v-9.5" />
  </svg>
);

export const IconScan: React.FC<IconProps> = ({ size = 18 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M7 4H5a1 1 0 0 0-1 1v2M17 4h2a1 1 0 0 1 1 1v2M7 20H5a1 1 0 0 1-1-1v-2M17 20h2a1 1 0 0 0 1-1v-2M8 12h8" />
  </svg>
);

export const IconBeaker: React.FC<IconProps> = ({ size = 18 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M9 3h6M10 3v5l-5.2 9.1A2 2 0 0 0 6.5 20h11a2 2 0 0 0 1.7-2.9L14 8V3" />
  </svg>
);

export const IconGauge: React.FC<IconProps> = ({ size = 18 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M5 19a8 8 0 1 1 14 0" />
    <path {...stroke} d="M12 13l3-3" />
    <circle cx="12" cy="13" r="1.2" fill="currentColor" />
  </svg>
);

export const IconStore: React.FC<IconProps> = ({ size = 18 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M4 10h16v9H4zM4 10l1.5-5h13L20 10M8 19v-5h8v5" />
  </svg>
);

export const IconBag: React.FC<IconProps> = ({ size = 18 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M6 8h12l-1 12H7L6 8Z" />
    <path {...stroke} d="M9 8V7a3 3 0 0 1 6 0v1" />
  </svg>
);

export const IconBox: React.FC<IconProps> = ({ size = 18 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M4 8l8-4 8 4v8l-8 4-8-4V8Z" />
    <path {...stroke} d="M4 8l8 4 8-4M12 12v8" />
  </svg>
);

export const IconChat: React.FC<IconProps> = ({ size = 18 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M5 18l-1 3 3.5-1.5A8.5 8.5 0 1 0 5 18Z" />
  </svg>
);

export const IconChart: React.FC<IconProps> = ({ size = 18 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M4 19h16M7 16V9M12 16V6M17 16v-4" />
  </svg>
);

export const IconList: React.FC<IconProps> = ({ size = 18 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M8 7h12M8 12h12M8 17h12M4 7h.01M4 12h.01M4 17h.01" />
  </svg>
);

export const IconTruck: React.FC<IconProps> = ({ size = 18 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M3 7h11v10H3zM14 10h4l3 3v4h-7V10Z" />
    <circle cx="7" cy="18" r="1.5" {...stroke} />
    <circle cx="17" cy="18" r="1.5" {...stroke} />
  </svg>
);

export const IconArrow: React.FC<IconProps> = ({ size = 16 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M5 12h14M13 6l6 6-6 6" />
  </svg>
);

export const IconMenu: React.FC<IconProps> = ({ size = 20 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M4 7h16M4 12h16M4 17h16" />
  </svg>
);

export const IconBell: React.FC<IconProps> = ({ size = 20 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M6 9a6 6 0 1 1 12 0c0 7 2 7 2 9H4c0-2 2-2 2-9Z" />
    <path {...stroke} d="M10 20a2 2 0 0 0 4 0" />
  </svg>
);

export const IconSearch: React.FC<IconProps> = ({ size = 18 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <circle cx="11" cy="11" r="6.5" {...stroke} />
    <path {...stroke} d="M16 16l5 5" />
  </svg>
);

export const IconLogout: React.FC<IconProps> = ({ size = 16 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M10 7V5a1 1 0 0 1 1-1h8v16h-8a1 1 0 0 1-1-1v-2M4 12h11M8 8l-4 4 4 4" />
  </svg>
);

export const IconShield: React.FC<IconProps> = ({ size = 22 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M12 3 5 6v6c0 5 3.2 7.8 7 9 3.8-1.2 7-4 7-9V6l-7-3Z" />
    <path {...stroke} d="M9 12l2 2 4-4" />
  </svg>
);

export const IconThermo: React.FC<IconProps> = ({ size = 18 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M10 14.5V6.5a2 2 0 1 1 4 0v8a3.5 3.5 0 1 1-4 0Z" />
  </svg>
);

export const IconDrop: React.FC<IconProps> = ({ size = 18 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M12 3s6 7 6 11a6 6 0 1 1-12 0c0-4 6-11 6-11Z" />
  </svg>
);

export const IconSun: React.FC<IconProps> = ({ size = 18 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <circle cx="12" cy="12" r="4" {...stroke} />
    <path {...stroke} d="M12 3v2M12 19v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M3 12h2M19 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4" />
  </svg>
);

export const IconSprout: React.FC<IconProps> = ({ size = 18 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden>
    <path {...stroke} d="M12 21V11" />
    <path {...stroke} d="M12 14c-4-1-6-4-6-8 5 0 6 4 6 8Z" />
    <path {...stroke} d="M12 13c4-1 6-4 6-8-5 0-6 4-6 8Z" />
  </svg>
);

