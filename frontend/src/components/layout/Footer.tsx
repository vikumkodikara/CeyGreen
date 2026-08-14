import React from 'react';

export const Footer: React.FC = () => {
  return (
    <footer
      style={{
        padding: '1.5rem 2rem',
        borderTop: '1px solid var(--border-color)',
        textAlign: 'center',
        color: 'var(--text-secondary)',
        fontSize: '0.875rem',
        marginTop: 'auto',
      }}
    >
      © 2026 CeyGreen — Microservices-Based Greenhouse Management Platform. All rights reserved.
    </footer>
  );
};
