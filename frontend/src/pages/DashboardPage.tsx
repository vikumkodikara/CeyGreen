import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import {
  IconArrow, IconBeaker, IconChart, IconChat, IconDrop, IconGauge,
  IconScan, IconShield, IconStore, IconSun, IconThermo,
} from '../components/icons/Icons';
import './DashboardPage.css';

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
    { label: 'Temperature', value: '27.4 °C', hint: 'Ideal 24–30 °C', icon: <IconThermo /> },
    { label: 'Humidity', value: '68%', hint: 'Ideal 60–75%', icon: <IconDrop /> },
    { label: 'Soil Moisture', value: '42%', hint: 'Ideal 35–50%', icon: <IconDrop /> },
    { label: 'Light Intensity', value: '580 lux', hint: 'Ideal 400–800', icon: <IconSun /> },
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
