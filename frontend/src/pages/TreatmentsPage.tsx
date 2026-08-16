import React, { useState, useEffect } from 'react';
import { getTreatmentsForDisease, searchTreatments, rateTreatment, getTreatmentAlternatives, getTreatmentsByCrop } from '../api/treatments';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Button } from '../components/ui/Button';
import { Treatment } from '../types/treatment';
import { useAuth } from '../hooks/useAuth';

  const { user } = useAuth();
  const [diseaseSearch, setDiseaseSearch] = useState('');
  const [treatments, setTreatments] = useState<Treatment[]>([]);
  const [loading, setLoading] = useState(false);
  const [organicOnly, setOrganicOnly] = useState(false);
  const [alternatives, setAlternatives] = useState<Record<number, Treatment[]>>({});

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!diseaseSearch) return;
    setLoading(true);

      let res: Treatment[] = [];
      try {
        res = await getTreatmentsForDisease(diseaseSearch);
      } catch {
        try {
          res = await getTreatmentsByCrop(diseaseSearch);
        } catch {
          res = await searchTreatments(diseaseSearch, undefined, organicOnly ? 'ORGANIC' : undefined);
        }
      }
      if (organicOnly) {
        res = res.filter(t => t.type === 'ORGANIC');
      }
      setTreatments(res);
    } finally {
      setLoading(false);
    }
  };

  const handleRate = async (id: number, rating: number) => {
    if (!user) return alert('Please login to rate');
    try {
      await rateTreatment(id, user.farmerId || user.id, rating);
      alert('Thanks for your rating!');
      handleSearch(new Event('submit') as any); // Refresh
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to submit rating');
    }
  };

  const loadAlternatives = async (id: number) => {
    try {
      const res = await getTreatmentAlternatives(id);
      setAlternatives(prev => ({ ...prev, [id]: res }));
    } catch {
      alert('Could not load alternatives');
    }
  };

  return (
    <div style={{ maxWidth: '900px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <h1 style={{ fontSize: '1.8rem' }}>💊 Treatment & Suggestion Catalog</h1>
        <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer', background: organicOnly ? 'rgba(16, 185, 129, 0.2)' : 'rgba(255,255,255,0.1)', padding: '0.5rem 1rem', borderRadius: '20px', transition: 'all 0.2s' }}>
          <input type="checkbox" checked={organicOnly} onChange={(e) => setOrganicOnly(e.target.checked)} style={{ cursor: 'pointer' }} />
          <span style={{ fontWeight: 600, color: organicOnly ? 'var(--accent-green)' : 'var(--text-muted)' }}>Show Organic Only 🌱</span>
        </label>
      </div>

      <Card title="Lookup Treatment Remedies">
        <form onSubmit={handleSearch} style={{ display: 'flex', gap: '1rem', alignItems: 'flex-end' }}>
          <div style={{ flex: 1 }}>
            <Input
              label="Disease Name or Crop Type"
              placeholder="e.g. Leaf Blight, Powdery Mildew, Tomato"
              value={diseaseSearch}
              onChange={(e) => setDiseaseSearch(e.target.value)}
              required
            />
          </div>
          <Button type="submit" isLoading={loading} style={{ marginBottom: '1rem' }}>
            Search
          </Button>
        </form>
      </Card>

      <div style={{ marginTop: '2rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        {treatments.map((t) => (
          <Card key={t.id} title={t.productName} subtitle={`Disease: ${t.diseaseName}`}>
            <div style={{ display: 'flex', gap: '1.5rem', marginTop: '0.5rem', flexWrap: 'wrap' }}>
              <div><strong>Type:</strong> <span style={{ color: t.type === 'ORGANIC' ? 'var(--success)' : 'var(--info)' }}>{t.type}</span></div>
              <div><strong>Dosage:</strong> {t.dosage}</div>
              <div><strong>Frequency:</strong> {t.frequency}</div>
              {t.phiDays !== undefined && <div><strong>PHI:</strong> <span style={{ color: t.phiDays === 0 ? 'var(--success)' : 'var(--warning)' }}>{t.phiDays} days</span></div>}
              {t.effectivenessScore && <div><strong>Effectiveness:</strong> {t.effectivenessScore}%</div>}
              {t.averageRating !== undefined && t.averageRating > 0 && <div><strong>Rating:</strong> {t.averageRating.toFixed(1)} ⭐️</div>}
            </div>

            {t.brandNames && <div style={{ marginTop: '0.5rem', fontSize: '0.9rem' }}><strong>Brands:</strong> {t.brandNames}</div>}
            {t.applicationMethod && <div style={{ marginTop: '0.5rem', fontSize: '0.9rem' }}><strong>Method:</strong> {t.applicationMethod}</div>}

            {t.safetyNotes && (
              <p style={{ marginTop: '0.75rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                🛡️ {t.safetyNotes}
              </p>
            )}

            <div style={{ marginTop: '1rem', display: 'flex', gap: '1rem', borderTop: '1px solid var(--border-color)', paddingTop: '1rem' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Rate this:</span>
                {[1,2,3,4,5].map(star => (
                  <button key={star} onClick={() => handleRate(t.id, star)} style={{ background: 'none', border: 'none', cursor: 'pointer', padding: 0 }}>⭐️</button>
                ))}
              </div>
              <button onClick={() => loadAlternatives(t.id)} style={{ background: 'none', border: '1px solid var(--border-focus)', color: 'var(--text-main)', padding: '0.3rem 0.8rem', borderRadius: '6px', fontSize: '0.85rem', cursor: 'pointer', marginLeft: 'auto' }}>
                View Alternatives
              </button>
            </div>

            {alternatives[t.id] && (
              <div style={{ marginTop: '1rem', background: 'rgba(0,0,0,0.2)', padding: '1rem', borderRadius: '8px' }}>
                <h5 style={{ marginBottom: '0.5rem', color: 'var(--text-muted)' }}>Alternative Options for {t.diseaseName}</h5>
                {alternatives[t.id].length === 0 ? <p style={{ fontSize: '0.85rem' }}>No alternatives found.</p> : (
                  <ul style={{ paddingLeft: '1.2rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                    {alternatives[t.id].map(alt => (
                      <li key={alt.id}>{alt.productName} ({alt.type}) - {alt.dosage}</li>
                    ))}
                  </ul>
                )}
              </div>
            )}
          </Card>
        ))}
      </div>
    </div>
  );
};
