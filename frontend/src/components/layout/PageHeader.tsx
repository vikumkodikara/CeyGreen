import React from 'react';

interface PageHeaderProps {
  eyebrow?: string;
  title: string;
  subtitle?: string;
}

export const PageHeader: React.FC<PageHeaderProps> = ({ title, subtitle }) => (
  <header className="page-hero">
    <h1>{title}</h1>
    {subtitle && <p className="page-subtitle">{subtitle}</p>}
  </header>
);
