import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import {
  IconArrow, IconBeaker, IconChart, IconChat, IconDrop, IconGauge,
  IconScan, IconShield, IconStore, IconSun, IconThermo,
} from '../components/icons/Icons';
import './DashboardPage.css';

const TEMP = [24, 24.6, 25.2, 26, 26.8, 27.1, 27.4, 27.2, 26.9, 26.4, 26, 25.6];
const HUM = [62, 64, 65, 66, 67, 68, 69, 68, 67, 66, 65, 64];
const SOIL = [38, 39, 40, 41, 42, 42, 41, 42, 43, 42, 41, 42];
const LIGHT = [120, 220, 340, 420, 510, 560, 580, 570, 490, 360, 210, 90];

function Spark({ values, color }: { values: number[]; color: string }) {
  const min = Math.min(...values);
  const max = Math.max(...values);
  const pts = values
    .map((v, i) => {
      const x = (i / (values.length - 1)) * 100;
      const y = 26 - ((v - min) / (max - min || 1)) * 20;
      return `${x},${y}`;
    })
    .join(' ');
  return (
    <svg viewBox="0 0 100 32" className="spark" preserveAspectRatio="none" aria-hidden>
      <polyline fill="none" stroke={color} strokeWidth="2.2" strokeLinejoin="round" points={pts} />
    </svg>
  );
}

function greeting() {
  const h = new Date().getHours();
  if (h < 12) return 'Good morning';
  if (h < 17) return 'Good afternoon';
  return 'Good evening';
}

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const first = (user?.fullName || user?.name || 'Grower').split(' ')[0];
  const isBuyer = user?.role === 'BUYER';

  const metrics = [
    { label: 'Temperature', value: '27.4 °C', hint: 'Ideal 24–30 °C', color: '#2f6b3a', series: TEMP, icon: <IconThermo /> },
    { label: 'Humidity', value: '68%', hint: 'Ideal 60–75%', color: '#2563eb', series: HUM, icon: <IconDrop /> },
    { label: 'Soil Moisture', value: '42%', hint: 'Ideal 35–50%', color: '#0f766e', series: SOIL, icon: <IconDrop /> },
    { label: 'Light Intensity', value: '580 lux', hint: 'Ideal 400–800', color: '#d97706', series: LIGHT, icon: <IconSun /> },
  ];

  const actions = [
    { to: '/diagnosis', label: 'Diagnose Plant', icon: <IconScan size={22} /> },
    { to: '/treatments', label: 'View Treatments', icon: <IconBeaker size={22} /> },
    { to: '/marketplace', label: 'Go to Marketplace', icon: <IconStore size={22} /> },
    { to: '/forum', label: 'Community Forum', icon: <IconChat size={22} /> },
    { to: '/analytics', label: 'Sales Analytics', icon: <IconChart size={22} /> },
  ];

  return (
    <div className="dash-page">
      <div className="dash-center">
        <section className="dash-hero" style={{ backgroundImage: 'url(/dashboard/hero.png)' }}>
          <div className="hero-copy">
            <p className="hero-hi">{greeting()}, {first}</p>
            <h1>Healthy crops, better tomorrow.</h1>
            <p>Monitor climate, catch leaf disease early, and grow with the market from one house.</p>
            <Link to={isBuyer ? '/marketplace' : '/greenhouse'} className="hero-btn">
              {isBuyer ? 'View marketplace' : 'View greenhouse'} <IconArrow />
            </Link>
          </div>
        </section>

        <div className="section-head">
          <h2>Current conditions</h2>
          <span className="live"><i /> Live data</span>
        </div>
        <div className="cond-row">
          {metrics.map((m) => (
            <article key={m.label} className="metric-card">
              <div className="metric-ico">{m.icon}</div>
              <span className="metric-label">{m.label}</span>
              <strong>{m.value}</strong>
              <small>{m.hint}</small>
              <Spark values={m.series} color={m.color} />
            </article>
          ))}
          <article className="status-card">
            <IconShield size={28} />
            <strong>All Systems Normal</strong>
            <p>ZONE1 climate is in range for tomato.</p>
          </article>
        </div>

        <h2 className="dash-h">Quick actions</h2>
        <div className="action-row">
          {actions.map((a) => (
            <Link key={a.to} to={a.to} className="action-tile">
              <span>{a.icon}</span>
              {a.label}
            </Link>
          ))}
        </div>

        <div className="story-row">
          <Link to="/diagnosis" className="story-card">
            <h3>Recent diagnoses</h3>
            <div className="story-item">
              <img src="/dashboard/blight.png" alt="Tomato leaf with early blight" />
              <div>
                <strong>Tomato — Early Blight</strong>
                <p>ZONE1 · 2 hours ago</p>
              </div>
              <span className="badge risk">High Risk</span>
            </div>
          </Link>
          <Link to={isBuyer ? '/marketplace/orders' : '/farmer/orders'} className="story-card">
            <h3>Recent orders</h3>
            <div className="story-item">
              <img src="/dashboard/tomatoes.png" alt="Basket of fresh tomatoes" />
              <div>
                <strong>Fresh Tomatoes</strong>
                <p>4 kg · Kandy</p>
              </div>
              <span className="badge ok">Delivered</span>
            </div>
          </Link>
          <article className="story-card">
            <h3>Market trends</h3>
            <p className="price">Tomato <b>Rs. 320</b> /kg</p>
            <Spark values={[280, 290, 295, 300, 310, 320]} color="#2f6b3a" />
            <span className="up">+12.5%</span>
          </article>
        </div>
      </div>

      <aside className="dash-rail">
        <article className="panel wx">
          <h3>Weather</h3>
          <p className="wx-place">Kandy, Sri Lanka</p>
          <div className="weather-now">
            <span className="wx-temp">27°</span>
            <div>
              <p>Partly Cloudy</p>
              <small>Humidity 68% · Wind 8 km/h</small>
            </div>
          </div>
        </article>

        <article className="panel">
          <h3>My greenhouse</h3>
          <select defaultValue="alpha" aria-label="Select greenhouse">
            <option value="alpha">Greenhouse Alpha · ZONE1</option>
          </select>
          <img className="gh-photo" src="/dashboard/greenhouse.jpg" alt="Rows of healthy greenhouse greens" />
          <p className="gh-ok">Excellent conditions</p>
          <Link to="/greenhouse" className="text-link">
            Open house <IconGauge size={14} />
          </Link>
        </article>

        <article className="panel">
          <h3>Today’s tasks</h3>
          <ul className="task-list">
            <li className="done"><input type="checkbox" checked readOnly /> Check for leaf diseases</li>
            <li className="done"><input type="checkbox" checked readOnly /> Apply Neem Oil Spray</li>
            <li><input type="checkbox" readOnly /> Calibrate soil sensors</li>
          </ul>
        </article>

        <article className="community-cta">
          <img src="/dashboard/community.png" alt="Farmers in the CeyGreen community" />
          <div>
            <h3>Join the grower community</h3>
            <p>Ask about blight, share harvest notes, and learn from nearby farms.</p>
            <Link to="/forum" className="hero-btn small">Go to Forum</Link>
          </div>
        </article>
      </aside>
    </div>
  );
};
