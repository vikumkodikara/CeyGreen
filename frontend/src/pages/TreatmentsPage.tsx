import React, { useState, useEffect } from 'react';
import { getTreatmentsForDisease, searchTreatments } from '../api/treatments';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Button } from '../components/ui/Button';
import { Treatment } from '../types/treatment';

export const TreatmentsPage: React.FC = () => {
  const [diseaseSearch, setDiseaseSearch] = useState('');
  const [treatments, setTreatments] = useState<Treatment[]>([]);
  const [loading, setLoading] = useState(false);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!diseaseSearch) return;
    setLoading(true);

    try {
      const res = await getTreatmentsForDisease(diseaseSearch);
      setTreatments(res);
    } catch {
      try {
        const res = await searchTreatments(diseaseSearch);
        setTreatments(res);
      } catch {
        setTreatments([]);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '900px' }}>
      <h1 style={{ fontSize: '1.8rem', marginBottom: '1.5rem' }}>💊 Treatment & Suggestion Catalog</h1>

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
            <div style={{ display: 'flex', gap: '1.5rem', marginTop: '0.5rem' }}>
              <div><strong>Type:</strong> <span style={{ color: 'var(--accent-green)' }}>{t.type}</span></div>
              <div><strong>Dosage:</strong> {t.dosage}</div>
              <div><strong>Frequency:</strong> {t.frequency}</div>
            </div>
            {t.safetyNotes && (
              <p style={{ marginTop: '0.75rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                🛡️ {t.safetyNotes}
              </p>
            )}
          </Card>
        ))}
      </div>
    </div>
  );
};
