import React, { useState } from 'react';
import { uploadDiagnosisImage } from '../api/diagnosis';
import { getTreatmentsForDisease } from '../api/treatments';
import { useAuth } from '../hooks/useAuth';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Treatment } from '../types/treatment';

export const DiagnosisPage: React.FC = () => {
  const { user } = useAuth();
  const [file, setFile] = useState<File | null>(null);
  const [cropType, setCropType] = useState('Tomato');
  const [loading, setLoading] = useState(false);
  const [diagnosisResult, setDiagnosisResult] = useState<any>(null);
  const [treatments, setTreatments] = useState<Treatment[]>([]);
  const [loadingTreatments, setLoadingTreatments] = useState(false);

  const handleUpload = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!file) return;
    setLoading(true);
    setDiagnosisResult(null);
    setTreatments([]);

    try {
      const farmerId = user?.farmerId || user?.id || 'farmer-1';
      const result = await uploadDiagnosisImage(file, farmerId, cropType);
      setDiagnosisResult(result);

      // Client-orchestrated cross-service call: Client takes predicted disease name and calls Treatment service
      if (result.predictedDisease && !result.isUncertain) {
        setLoadingTreatments(true);
        try {
          const tResult = await getTreatmentsForDisease(result.predictedDisease);
          setTreatments(tResult);
        } catch {
          console.warn('No treatments found for:', result.predictedDisease);
        } finally {
          setLoadingTreatments(false);
        }
      }
    } catch (err: any) {
      alert(err.response?.data?.message || 'Diagnosis upload failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '800px' }}>
      <h1 style={{ fontSize: '1.8rem', marginBottom: '1.5rem' }}>🔬 AI Plant Disease Diagnosis</h1>
      
      <Card title="Upload Crop Image" subtitle="Upload a high-resolution photo of the affected plant leaf">
        <form onSubmit={handleUpload}>
          <Input
            label="Crop Type"
            value={cropType}
            onChange={(e) => setCropType(e.target.value)}
            placeholder="e.g. Tomato, Potato, Pepper"
            required
          />
          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ display: 'block', marginBottom: '0.4rem', fontSize: '0.875rem', color: 'var(--text-muted)' }}>
              Plant Photo
            </label>
            <input
              type="file"
              accept="image/*"
              onChange={(e) => setFile(e.target.files?.[0] || null)}
              required
              style={{ background: 'rgba(255,255,255,0.05)', padding: '0.5rem' }}
            />
          </div>
          <Button type="submit" isLoading={loading} disabled={!file}>
            Run AI Diagnosis
          </Button>
        </form>
      </Card>

      {diagnosisResult && (
        <Card title="Diagnosis Result" style={{ marginTop: '2rem' }}>
          <div style={{ display: 'flex', gap: '2rem', alignItems: 'center' }}>
            {diagnosisResult.imageUrl && (
              <img
                src={diagnosisResult.imageUrl}
                alt="Diagnosis Crop"
                style={{ width: '150px', height: '150px', objectFit: 'cover', borderRadius: '12px' }}
              />
            )}
            <div>
              <p style={{ fontSize: '1.2rem', fontWeight: 600, color: 'var(--accent-green)' }}>
                {diagnosisResult.predictedDisease}
              </p>
              <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
                Confidence Score: {(diagnosisResult.confidence * 100).toFixed(1)}%
              </p>
              {diagnosisResult.isUncertain && (
                <p style={{ color: 'var(--warning)', marginTop: '0.5rem' }}>
                  ⚠️ Confidence below threshold. Please consult an expert.
                </p>
              )}
            </div>
          </div>
        </Card>
      )}

      {loadingTreatments && <p style={{ marginTop: '2rem', color: 'var(--text-muted)' }}>Fetching recommended treatments...</p>}

      {treatments.length > 0 && (
        <Card title="Recommended Treatments (Client Orchestrated)" style={{ marginTop: '2rem' }}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {treatments.map((t) => (
              <div
                key={t.id}
                style={{
                  padding: '1rem',
                  borderRadius: '10px',
                  background: 'rgba(255,255,255,0.03)',
                  border: '1px solid var(--border-color)',
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                  <h4 style={{ color: 'var(--accent-green)' }}>{t.productName}</h4>
                  <span style={{ fontSize: '0.8rem', padding: '0.2rem 0.6rem', borderRadius: '6px', background: t.type === 'ORGANIC' ? 'rgba(16, 185, 129, 0.2)' : 'rgba(59, 130, 246, 0.2)', color: t.type === 'ORGANIC' ? 'var(--success)' : 'var(--info)' }}>
                    {t.type}
                  </span>
                </div>
                <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>Dosage: {t.dosage} | Frequency: {t.frequency}</p>
                {t.safetyNotes && <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginTop: '0.4rem' }}>Note: {t.safetyNotes}</p>}
              </div>
            ))}
          </div>
        </Card>
      )}
    </div>
  );
};
