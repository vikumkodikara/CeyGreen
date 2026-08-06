import React from 'react';

interface CardProps {
  children: React.ReactNode;
  title?: string;
  subtitle?: string;
  className?: string;
  style?: React.CSSProperties;
}

export const Card: React.FC<CardProps> = ({ children, title, subtitle, className = '', style }) => {
  return (
    <div className={`glass-panel ${className}`} style={{ padding: '1.5rem', ...style }}>
      {title && (
        <div style={{ marginBottom: '1rem' }}>
          <h3 style={{ fontSize: '1.25rem', color: 'var(--text-main)' }}>{title}</h3>
          {subtitle && <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginTop: '0.25rem' }}>{subtitle}</p>}
        </div>
      )}
      {children}
    </div>
  );
};
