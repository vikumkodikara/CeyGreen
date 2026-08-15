import React, { useState } from 'react';
import { getTreatmentsForDisease, searchTreatments } from '../api/treatments';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Button } from '../components/ui/Button';
import { Treatment } from '../types/treatment';
import { PageHeader } from '../components/layout/PageHeader';

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
    <div className="page-wrap">
      <PageHeader
        title="Treatments"
        subtitle="Search remedies by disease or crop name."
      />

      <Card title="Find a remedy">
        <form onSubmit={handleSearch} className="row">
          <div className="grow">
            <Input
              label="Disease or crop"
              placeholder="e.g. Early blight, Tomato"
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

      <div className="stack" style={{ marginTop: '1.25rem' }}>
        {treatments.map((t) => (
          <Card key={t.id} title={t.productName} subtitle={`For ${t.diseaseName}`}>
            <div className="treatment-meta">
              <span className="pill">{t.type}</span>
              <span className="pill">Dosage: {t.dosage}</span>
              <span className="pill">{t.frequency}</span>
            </div>
            {t.safetyNotes && (
              <p style={{ marginTop: '0.85rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                Safety: {t.safetyNotes}
              </p>
            )}
          </Card>
        ))}
      </div>
    </div>
  );
};
