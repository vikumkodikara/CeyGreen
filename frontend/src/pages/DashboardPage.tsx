import React from 'react';
import { Card } from '../components/ui/Card';
import { useAuth } from '../hooks/useAuth';
import { Link } from 'react-router-dom';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();

  const services = [
    { title: 'Plant Disease Diagnosis', desc: 'AI-assisted disease detection from crop images', link: '/diagnosis', icon: '🔬' },
    { title: 'Treatment Catalog', desc: 'Curated organic and chemical remedies', link: '/treatments', icon: '💊' },
    { title: 'IoT Telemetry & Control', desc: 'Live greenhouse sensor readings & rule engines', link: '/greenhouse', icon: '🌡️' },
    { title: 'Marketplace', desc: 'Direct farmer-to-buyer crop marketplace', link: '/marketplace', icon: '🛒' },
    { title: 'Community Forum', desc: 'Knowledge sharing and discussions', link: '/forum', icon: '💬' },
    { title: 'Sales Analytics', desc: 'Revenue tracking & performance leaderboards', link: '/analytics', icon: '📈' },
  ];

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>Welcome back, {user?.fullName || 'Farmer'} 👋</h1>
        <p style={{ color: 'var(--text-secondary)' }}>Microservices-based Greenhouse Operations Overview</p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '1.5rem' }}>
        {services.map((svc) => (
          <Link key={svc.link} to={svc.link} style={{ textDecoration: 'none' }}>
            <Card style={{ height: '100%', cursor: 'pointer' }}>
              <div style={{ fontSize: '2.5rem', marginBottom: '1rem' }}>{svc.icon}</div>
              <h3 style={{ fontSize: '1.2rem', marginBottom: '0.5rem', color: 'var(--text-main)' }}>{svc.title}</h3>
              <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>{svc.desc}</p>
            </Card>
          </Link>
        ))}
      </div>
    </div>
  );
};
