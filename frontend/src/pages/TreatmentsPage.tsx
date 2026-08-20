import React, { useState } from 'react';
import { getTreatmentsForDisease, searchTreatments, rateTreatment, getTreatmentAlternatives, getTreatmentsByCrop, createTreatment, deleteTreatment } from '../api/treatments';
import { Card } from '../components/ui/Card';
import { Input } from '../components/ui/Input';
import { Button } from '../components/ui/Button';
import { Treatment } from '../types/treatment';
import { useAuth } from '../hooks/useAuth';
import { PageHeader } from '../components/layout/PageHeader';

const CROP_DISEASES: Record<string, string[]> = {
  'Tomato': ['Tomato Bacterial Spot', 'Tomato Early Blight', 'Tomato Late Blight', 'Tomato Leaf Mold', 'Tomato Septoria Leaf Spot', 'Tomato Spider Mites', 'Tomato Target Spot', 'Tomato Yellow Leaf Curl Virus', 'Tomato Mosaic Virus'],
  'Potato': ['Potato Early Blight', 'Potato Late Blight'],
  'Pepper': ['Pepper Bacterial Spot', 'Pepper Powdery Mildew'],
  'Chili': ['Chili Anthracnose', 'Chili Leaf Curl Virus'],
  'Strawberry': ['Strawberry Leaf Scorch', 'Strawberry Gray Mold', 'Strawberry Powdery Mildew'],
  'Grape': ['Grape Black Rot', 'Grape Esca', 'Grape Leaf Blight']
};
const ALL_DISEASES = Object.values(CROP_DISEASES).flat();

const RatingForm: React.FC<{
  treatmentId: number;
  existingRating?: number;
  existingComment?: string;
  onSubmit: (rating: number, comment: string) => Promise<void>;
}> = ({ existingRating = 0, existingComment = '', onSubmit }) => {
  const [rating, setRating] = useState(existingRating);
  const [comment, setComment] = useState(existingComment);
  const [hover, setHover] = useState(0);
  const [loading, setLoading] = useState(false);
  const [isEditing, setIsEditing] = useState(existingRating === 0);

  if (!isEditing && existingRating > 0) {
    return (
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', width: '100%' }}>
        <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Your Rating:</span>
        <div style={{ display: 'flex', gap: '0.25rem' }}>
          {[1, 2, 3, 4, 5].map((star) => (
             <span key={star} style={{ color: star <= existingRating ? '#FBBF24' : '#4B5563', fontSize: '1.2rem' }}>★</span>
          ))}
        </div>
        <button onClick={() => setIsEditing(true)} style={{ background: 'none', border: '1px solid var(--accent-green)', color: 'var(--accent-green)', padding: '0.2rem 0.6rem', borderRadius: '4px', fontSize: '0.8rem', cursor: 'pointer' }}>
          Edit Review
        </button>
      </div>
    );
  }

  const handleSubmit = async () => {
    if (rating === 0) return alert('Please select a rating');
    setLoading(true);
    try {
      await onSubmit(rating, comment);
      setIsEditing(false);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', width: '100%' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Rate this:</span>
        <div style={{ display: 'flex', gap: '0.25rem' }}>
          {[1, 2, 3, 4, 5].map((star) => (
            <button
              key={star}
              onClick={() => setRating(star)}
              onMouseEnter={() => setHover(star)}
              onMouseLeave={() => setHover(0)}
              style={{
                background: 'none', border: 'none', cursor: 'pointer', padding: 0, fontSize: '1.2rem',
                color: star <= (hover || rating) ? '#FBBF24' : '#4B5563', transition: 'color 0.2s',
              }}
            >
              ★
            </button>
          ))}
        </div>
      </div>
      <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'flex-start' }}>
        <div style={{ flex: 1 }}>
           <textarea
             placeholder="Add a comment... (optional)"
             value={comment}
             onChange={e => setComment(e.target.value)}
             style={{ width: '100%', padding: '0.6rem', borderRadius: '6px', background: 'rgba(0,0,0,0.2)', border: '1px solid var(--border-color)', color: 'var(--text-main)', minHeight: '60px', resize: 'vertical', fontSize: '0.9rem', outline: 'none' }}
           />
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
          <Button onClick={handleSubmit} isLoading={loading} style={{ padding: '0.4rem 1rem' }}>Submit</Button>
          {existingRating > 0 && <button onClick={() => { setIsEditing(false); setRating(existingRating); setComment(existingComment); }} style={{ background: 'transparent', border: 'none', color: 'var(--text-muted)', fontSize: '0.85rem', cursor: 'pointer' }}>Cancel</button>}
        </div>
      </div>
    </div>
  );
};

export const TreatmentsPage: React.FC = () => {
  const { user } = useAuth();
  const [diseaseSearch, setDiseaseSearch] = useState('');
  const [selectedCrop, setSelectedCrop] = useState<string | null>(null);
  const [treatments, setTreatments] = useState<Treatment[]>([]);
  const [loading, setLoading] = useState(false);
  const [organicOnly, setOrganicOnly] = useState(false);
  const [alternatives, setAlternatives] = useState<Record<number, Treatment[]>>({});
  const [showAddModal, setShowAddModal] = useState(false);
  const [newRemedy, setNewRemedy] = useState({ diseaseName: '', productName: '', type: 'ORGANIC', dosage: '', frequency: '', safetyNotes: '' });

  const performSearch = async (term: string) => {
    if (!term) return;
    setLoading(true);

    try {
      let res: Treatment[] = [];
      try {
        res = await getTreatmentsForDisease(term);
      } catch {
        try {
          res = await getTreatmentsByCrop(term);
        } catch {
          res = await searchTreatments(term, undefined, undefined);
        }
      }
      if (res.length === 0) {
        alert("No treatments found. Please check your spelling or try a different search term.");
      }
      setTreatments(res);
    } catch (error) {
      alert("Failed to connect to the server. Please try again later.");
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    performSearch(diseaseSearch);
  };

  const handleQuickSearch = (disease: string) => {
    setDiseaseSearch(disease);
    performSearch(disease);
  };

  const handleRate = async (id: number, rating: number, comment: string) => {
    if (!user) { alert('Please login to rate'); throw new Error(); }
    try {
      const farmerId = user.farmerId || user.id;
      const farmerName = user.name || user.email?.split('@')[0] || 'Anonymous';
      await rateTreatment(id, farmerId, farmerName, rating, comment);
      alert('Thanks for your review!');
      handleSearch(new Event('submit') as any); // Refresh
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to submit review');
      throw err;
    }
  };

  const loadAlternatives = async (id: number) => {
    if (alternatives[id]) {
      setAlternatives(prev => {
        const next = { ...prev };
        delete next[id];
        return next;
      });
      return;
    }
    try {
      const res = await getTreatmentAlternatives(id);
      setAlternatives(prev => ({ ...prev, [id]: res }));
    } catch {
      alert('Could not load alternatives');
    }
  };

  const displayedTreatments = organicOnly
    ? treatments.filter(t => t.type?.toUpperCase().trim() === 'ORGANIC')
    : treatments;

  const handleAddRemedy = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user) return alert('Please login to add a remedy');
    setLoading(true);
    try {
      await createTreatment({
        ...newRemedy,
        active: true,
        addedByFarmerId: user.farmerId || user.id,
        addedByFarmerName: user.name || user.email?.split('@')[0] || 'Anonymous'
      });
      alert('Remedy added successfully!');
      setShowAddModal(false);
      setNewRemedy({ diseaseName: '', productName: '', type: 'ORGANIC', dosage: '', frequency: '', safetyNotes: '' });
      if (diseaseSearch === newRemedy.diseaseName) {
        handleSearch(new Event('submit') as any);
      }
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to add remedy');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteRemedy = async (id: number) => {
    if (!user) return;
    if (!window.confirm("Are you sure you want to delete this remedy?")) return;
    setLoading(true);
    try {
      const farmerId = user.farmerId || user.id;
      await deleteTreatment(id, farmerId);
      alert('Remedy deleted successfully!');
      if (diseaseSearch) {
        handleSearch(new Event('submit') as any);
      } else {
        setTreatments(prev => prev.filter(t => t.id !== id));
      }
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to delete remedy');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-wrap" style={{ maxWidth: '1200px', margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <PageHeader
          title="Treatment & Suggestion Catalog"
          subtitle="Search remedies by disease or crop name."
        />
        <button
          type="button"
          role="switch"
          aria-checked={organicOnly}
          onClick={() => setOrganicOnly(!organicOnly)}
          style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: '0.75rem',
            cursor: 'pointer',
            background: organicOnly
              ? 'linear-gradient(135deg, rgba(16, 185, 129, 0.15) 0%, rgba(5, 150, 105, 0.08) 100%)'
              : 'rgba(255, 255, 255, 0.04)',
            border: organicOnly
              ? '1px solid #10B981'
              : '1px solid rgba(255, 255, 255, 0.15)',
            borderRadius: '30px',
            padding: '0.5rem 1.1rem',
            transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
            boxShadow: organicOnly
              ? '0 4px 15px rgba(16, 185, 129, 0.25)'
              : '0 2px 6px rgba(0,0,0,0.1)',
            outline: 'none',
            userSelect: 'none'
          }}
        >
          <span style={{
            position: 'relative',
            width: '42px',
            height: '24px',
            background: organicOnly ? '#10B981' : '#374151',
            borderRadius: '20px',
            transition: 'background-color 0.3s ease',
            display: 'block',
            flexShrink: 0
          }}>
            <span style={{
              position: 'absolute',
              top: '3px',
              left: organicOnly ? '21px' : '3px',
              width: '18px',
              height: '18px',
              background: '#FFFFFF',
              borderRadius: '50%',
              transition: 'left 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
              boxShadow: '0 2px 5px rgba(0,0,0,0.3)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: '10px'
            }}>
              {organicOnly ? '🌱' : ''}
            </span>
          </span>
          <span style={{
            fontSize: '0.9rem',
            fontWeight: 600,
            color: organicOnly ? '#10B981' : 'var(--text-secondary)',
            letterSpacing: '0.01em',
            display: 'flex',
            alignItems: 'center',
            gap: '0.35rem',
            whiteSpace: 'nowrap'
          }}>
            Organic Remedies Only
          </span>
        </button>
        {user && (
          <Button onClick={() => setShowAddModal(true)} style={{ marginLeft: '1rem', background: 'var(--accent-green)', color: '#fff' }}>
            + Add Remedy
          </Button>
        )}
      </div>

      <div style={{ display: 'flex', gap: '2rem', alignItems: 'flex-start', flexWrap: 'wrap' }}>
        
        {/* Left Sidebar: Browse by Crop */}
        <div style={{ flex: '1 1 250px', maxWidth: '350px' }}>
          <Card title="Browse Categories">
            <h6 style={{ marginBottom: '1rem', color: 'var(--text-muted)', fontSize: '0.85rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Select a Crop</h6>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
              {Object.keys(CROP_DISEASES).map(crop => (
                <button
                  key={crop}
                  onClick={() => setSelectedCrop(selectedCrop === crop ? null : crop)}
                  className="pill"
                  style={{
                    cursor: 'pointer',
                    background: selectedCrop === crop ? 'var(--accent-green)' : 'var(--surface-color)',
                    color: selectedCrop === crop ? '#fff' : 'var(--text-main)',
                    border: '1px solid',
                    borderColor: selectedCrop === crop ? 'var(--accent-green)' : 'var(--border-color)',
                    padding: '0.5rem 1rem',
                    fontWeight: selectedCrop === crop ? 600 : 400,
                    transition: 'all 0.2s',
                    width: 'calc(50% - 0.25rem)',
                    textAlign: 'center'
                  }}
                >
                  {crop}
                </button>
              ))}
            </div>
            
            {selectedCrop && (
              <div style={{ marginTop: '1.5rem', paddingTop: '1.5rem', borderTop: '1px solid var(--border-color)' }}>
                <h6 style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>{selectedCrop} Diseases</h6>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
                  {CROP_DISEASES[selectedCrop].map(disease => (
                    <button
                      key={disease}
                      onClick={() => handleQuickSearch(disease)}
                      style={{
                        textAlign: 'left',
                        background: diseaseSearch === disease ? 'rgba(16, 185, 129, 0.1)' : 'transparent',
                        border: 'none',
                        color: diseaseSearch === disease ? 'var(--accent-green)' : 'var(--text-secondary)',
                        fontWeight: diseaseSearch === disease ? 600 : 400,
                        cursor: 'pointer',
                        padding: '0.6rem 0.8rem',
                        fontSize: '0.9rem',
                        borderRadius: '6px',
                        transition: 'all 0.2s',
                        display: 'flex',
                        alignItems: 'center'
                      }}
                      onMouseEnter={(e) => {
                        if (diseaseSearch !== disease) {
                          e.currentTarget.style.background = 'rgba(255,255,255,0.05)';
                        }
                      }}
                      onMouseLeave={(e) => {
                        if (diseaseSearch !== disease) {
                          e.currentTarget.style.background = 'transparent';
                        }
                      }}
                    >
                      <span style={{ marginRight: '0.5rem', opacity: 0.5 }}>🦠</span> {disease}
                    </button>
                  ))}
                </div>
              </div>
            )}
          </Card>
        </div>

        {/* Right Content: Search and Results */}
        <div style={{ flex: '3 1 500px', display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          
          <Card title="Find a remedy">
            <form onSubmit={handleSearch} style={{ display: 'flex', gap: '1rem', alignItems: 'flex-end' }}>
              <div style={{ flex: 1 }}>
                <Input
                  label="Search by disease or crop name"
                  placeholder="e.g. Early blight, Tomato"
                  value={diseaseSearch}
                  onChange={(e) => setDiseaseSearch(e.target.value)}
                  required
                  list="disease-suggestions"
                  style={{ marginBottom: 0 }}
                />
                <datalist id="disease-suggestions">
                  {ALL_DISEASES.map(d => <option key={d} value={d} />)}
                  {Object.keys(CROP_DISEASES).map(c => <option key={c} value={c} />)}
                </datalist>
              </div>
              <Button type="submit" isLoading={loading}>
                Search
              </Button>
            </form>
          </Card>

          {displayedTreatments.length > 0 ? (
            <div className="stack">
              <h4 style={{ fontSize: '1.1rem', color: 'var(--text-main)', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                Results ({displayedTreatments.length})
                {organicOnly && (
                  <span style={{ fontSize: '0.8rem', background: 'rgba(16, 185, 129, 0.2)', color: '#10B981', border: '1px solid #10B981', padding: '0.1rem 0.6rem', borderRadius: '12px' }}>
                    Organic Only 🌱
                  </span>
                )}
              </h4>
              {displayedTreatments.map((t) => (
                <Card key={t.id} title={t.productName} subtitle={`For ${t.diseaseName}`}>
                  {t.addedByFarmerName && (
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                      <div style={{ background: 'rgba(16, 185, 129, 0.1)', color: 'var(--accent-green)', padding: '0.4rem 0.8rem', borderRadius: '4px', fontSize: '0.85rem', display: 'inline-flex', alignItems: 'center', gap: '0.5rem', border: '1px solid rgba(16, 185, 129, 0.2)' }}>
                        🧑‍🌾 Added by Community Farmer: <strong>{t.addedByFarmerName}</strong>
                      </div>
                      {user && (user.farmerId === t.addedByFarmerId || user.id === t.addedByFarmerId) && (
                        <button 
                          onClick={() => handleDeleteRemedy(t.id)}
                          style={{ background: 'rgba(239, 68, 68, 0.1)', color: '#EF4444', border: '1px solid #EF4444', padding: '0.3rem 0.6rem', borderRadius: '4px', fontSize: '0.8rem', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '0.3rem' }}
                        >
                          🗑️ Delete
                        </button>
                      )}
                    </div>
                  )}
                  <div className="treatment-meta" style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap', marginTop: '0.5rem' }}>
                    <span className="pill" style={{
                      background: t.type?.toUpperCase() === 'ORGANIC' ? 'rgba(16, 185, 129, 0.2)' : 'rgba(59, 130, 246, 0.2)',
                      color: t.type?.toUpperCase() === 'ORGANIC' ? 'var(--success)' : '#60A5FA',
                      borderColor: t.type?.toUpperCase() === 'ORGANIC' ? 'var(--success)' : '#3B82F6',
                      fontWeight: 600
                    }}>
                      {t.type?.toUpperCase() === 'ORGANIC' ? '🌱 ORGANIC' : '🧪 CHEMICAL'}
                    </span>
                    <span className="pill">Dosage: {t.dosage}</span>
                    <span className="pill">Frequency: {t.frequency}</span>
                    {t.phiDays !== undefined && <span className="pill" style={{ color: t.phiDays === 0 ? 'var(--success)' : 'var(--warning)' }}>PHI: {t.phiDays} days</span>}
                    {t.effectivenessScore && <span className="pill">Effectiveness: {t.effectivenessScore}%</span>}
                    {t.averageRating !== undefined && t.averageRating > 0 && <span className="pill">Rating: {t.averageRating.toFixed(1)} ⭐️</span>}
                  </div>
                  {t.brandNames && <div style={{ marginTop: '0.5rem', fontSize: '0.9rem' }}><strong>Brands:</strong> {t.brandNames}</div>}
                  {t.applicationMethod && <div style={{ marginTop: '0.5rem', fontSize: '0.9rem' }}><strong>Method:</strong> {t.applicationMethod}</div>}

                  {t.safetyNotes && (
                    <p style={{ marginTop: '0.85rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                      Safety: {t.safetyNotes}
                    </p>
                  )}

                  <div style={{ marginTop: '1rem', display: 'flex', flexDirection: 'column', gap: '1rem', borderTop: '1px solid var(--border-color)', paddingTop: '1rem' }}>
                    <RatingForm
                      treatmentId={t.id}
                      existingRating={t.reviews?.find(r => r.farmerId === (user?.farmerId || user?.id))?.rating}
                      existingComment={t.reviews?.find(r => r.farmerId === (user?.farmerId || user?.id))?.comment}
                      onSubmit={(rating, comment) => handleRate(t.id, rating, comment)}
                    />
                    <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                      <button onClick={() => loadAlternatives(t.id)} style={{ background: alternatives[t.id] ? 'rgba(16, 185, 129, 0.15)' : 'none', border: '1px solid var(--accent-green)', color: 'var(--accent-green)', padding: '0.4rem 1rem', borderRadius: '6px', fontSize: '0.85rem', cursor: 'pointer', fontWeight: 600, transition: 'all 0.2s' }}>
                        {alternatives[t.id] ? 'Hide Alternatives ▲' : 'View Alternatives ▼'}
                      </button>
                    </div>
                  </div>

                  {t.reviews && t.reviews.length > 0 && (
                    <div style={{ marginTop: '1.5rem', paddingTop: '1rem', borderTop: '1px dashed var(--border-color)' }}>
                      <h5 style={{ fontSize: '0.95rem', marginBottom: '1rem', color: 'var(--text-secondary)' }}>User Reviews ({t.reviews.length})</h5>
                      <div style={{ display: 'flex', flexDirection: 'column', gap: '0.8rem' }}>
                        {t.reviews.map(rev => (
                          <div key={`${rev.farmerId}-${rev.createdAt}`} style={{ background: 'rgba(255,255,255,0.03)', padding: '0.8rem', borderRadius: '8px' }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.3rem' }}>
                              <strong style={{ fontSize: '0.9rem', color: 'var(--text-main)' }}>{rev.farmerName}</strong>
                              <div style={{ display: 'flex', gap: '0.1rem' }}>
                                {[1, 2, 3, 4, 5].map(s => <span key={s} style={{ color: s <= rev.rating ? '#FBBF24' : '#4B5563', fontSize: '0.9rem' }}>★</span>)}
                              </div>
                            </div>
                            {rev.comment && <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', margin: 0, marginTop: '0.4rem', whiteSpace: 'pre-wrap' }}>{rev.comment}</p>}
                          </div>
                        ))}
                      </div>
                    </div>
                  )}

                  {alternatives[t.id] && (() => {
                    const altList = organicOnly
                      ? alternatives[t.id].filter(alt => alt.type?.toUpperCase().trim() === 'ORGANIC')
                      : alternatives[t.id];

                    return (
                      <div style={{
                        marginTop: '1.25rem',
                        background: 'rgba(16, 185, 129, 0.05)',
                        border: '1px solid rgba(16, 185, 129, 0.25)',
                        padding: '1.25rem',
                        borderRadius: '10px'
                      }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                          <h5 style={{ margin: 0, color: 'var(--accent-green)', fontSize: '1rem', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            🌱 Alternative Options for {t.diseaseName} {organicOnly && '(Organic Only)'}
                          </h5>
                          <button
                            onClick={() => setAlternatives(prev => { const n = { ...prev }; delete n[t.id]; return n; })}
                            style={{ background: 'transparent', border: 'none', color: 'var(--text-muted)', cursor: 'pointer', fontSize: '0.85rem' }}
                          >
                            ✕ Close
                          </button>
                        </div>

                        {altList.length === 0 ? (
                          <p style={{ fontSize: '0.88rem', color: 'var(--text-muted)', margin: 0 }}>
                            {organicOnly
                              ? 'No organic alternative options found for this disease.'
                              : 'No alternative options found for this disease.'}
                          </p>
                        ) : (
                          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.8rem' }}>
                            {altList.map(alt => (
                              <div key={alt.id} style={{
                                background: 'rgba(0, 0, 0, 0.25)',
                                padding: '0.85rem 1rem',
                                borderRadius: '8px',
                                border: '1px solid var(--border-color)'
                              }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '0.5rem', marginBottom: '0.4rem' }}>
                                  <strong style={{ color: 'var(--text-main)', fontSize: '0.95rem' }}>{alt.productName}</strong>
                                  <span className="pill" style={{
                                    fontSize: '0.75rem',
                                    background: alt.type?.toUpperCase() === 'ORGANIC' ? 'rgba(16, 185, 129, 0.2)' : 'rgba(59, 130, 246, 0.2)',
                                    color: alt.type?.toUpperCase() === 'ORGANIC' ? 'var(--accent-green)' : '#60A5FA',
                                    borderColor: alt.type?.toUpperCase() === 'ORGANIC' ? 'var(--accent-green)' : '#3B82F6',
                                    fontWeight: 600
                                  }}>
                                    {alt.type?.toUpperCase() === 'ORGANIC' ? '🌱 ORGANIC' : '🧪 CHEMICAL'}
                                  </span>
                                </div>
                                <div style={{ display: 'flex', gap: '0.6rem', flexWrap: 'wrap', fontSize: '0.82rem', color: 'var(--text-secondary)', marginBottom: '0.4rem' }}>
                                  <span><strong>Dosage:</strong> {alt.dosage}</span>
                                  <span>•</span>
                                  <span><strong>Frequency:</strong> {alt.frequency}</span>
                                  {alt.phiDays !== undefined && (
                                    <>
                                      <span>•</span>
                                      <span><strong>PHI:</strong> {alt.phiDays} days</span>
                                    </>
                                  )}
                                  {alt.effectivenessScore && (
                                    <>
                                      <span>•</span>
                                      <span><strong>Effectiveness:</strong> {alt.effectivenessScore}%</span>
                                    </>
                                  )}
                                </div>
                                {alt.brandNames && (
                                  <div style={{ fontSize: '0.82rem', color: 'var(--text-muted)', marginTop: '0.2rem' }}>
                                    <strong>Brands:</strong> {alt.brandNames}
                                  </div>
                                )}
                                {alt.safetyNotes && (
                                  <div style={{ fontSize: '0.82rem', color: 'var(--text-muted)', marginTop: '0.2rem' }}>
                                    <strong>Safety:</strong> {alt.safetyNotes}
                                  </div>
                                )}
                              </div>
                            ))}
                          </div>
                        )}
                      </div>
                    );
                  })()}
                </Card>
              ))}
            </div>
          ) : (
            diseaseSearch && !loading && (
              <div style={{ textAlign: 'center', padding: '3rem 1rem', background: 'rgba(0,0,0,0.1)', borderRadius: '12px' }}>
                <div style={{ fontSize: '3rem', marginBottom: '1rem', opacity: 0.5 }}>🌱</div>
                <h3 style={{ color: 'var(--text-main)', marginBottom: '0.5rem' }}>
                  {organicOnly ? 'No Organic Treatments Found' : 'No Treatments Found'}
                </h3>
                <p style={{ color: 'var(--text-muted)' }}>
                  {organicOnly
                    ? `No organic remedies found for "${diseaseSearch}". Try turning off "Organic Remedies Only" to see chemical treatments.`
                    : `We couldn't find any remedies matching "${diseaseSearch}". Try browsing by crop instead.`}
                </p>
                {organicOnly && (
                  <button
                    onClick={() => setOrganicOnly(false)}
                    style={{
                      marginTop: '1rem',
                      background: 'var(--accent-green)',
                      color: '#fff',
                      border: 'none',
                      padding: '0.5rem 1.2rem',
                      borderRadius: '6px',
                      cursor: 'pointer',
                      fontWeight: 600
                    }}
                  >
                    Show All Treatments (Chemical & Organic)
                  </button>
                )}
              </div>
            )
          )}
        </div>
      </div>

      {showAddModal && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.7)', zIndex: 999, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '1rem' }}>
          <div style={{ background: 'var(--bg-card)', padding: '2rem', borderRadius: '12px', width: '100%', maxWidth: '500px', maxHeight: '90vh', overflowY: 'auto' }}>
            <h3 style={{ marginBottom: '1.5rem', color: 'var(--text-main)' }}>Add a New Remedy</h3>
            <form onSubmit={handleAddRemedy} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <Input label="Disease Name" value={newRemedy.diseaseName} onChange={e => setNewRemedy({...newRemedy, diseaseName: e.target.value})} required list="disease-suggestions" />
              <Input label="Product/Remedy Name" value={newRemedy.productName} onChange={e => setNewRemedy({...newRemedy, productName: e.target.value})} required />
              
              <div>
                <label style={{ display: 'block', marginBottom: '0.4rem', fontSize: '0.875rem', color: 'var(--text-muted)' }}>Type</label>
                <select value={newRemedy.type} onChange={e => setNewRemedy({...newRemedy, type: e.target.value})} style={{ width: '100%', padding: '0.8rem', borderRadius: '8px', background: 'var(--bg-main)', border: '1px solid var(--border-color)', color: 'var(--text-main)' }}>
                  <option value="ORGANIC">Organic</option>
                  <option value="CHEMICAL">Chemical</option>
                </select>
              </div>

              <Input label="Dosage (Optional)" value={newRemedy.dosage} onChange={e => setNewRemedy({...newRemedy, dosage: e.target.value})} placeholder="e.g. 20ml per 10L" />
              <Input label="Frequency (Optional)" value={newRemedy.frequency} onChange={e => setNewRemedy({...newRemedy, frequency: e.target.value})} placeholder="e.g. Every 7 days" />
              <Input label="Safety Notes (Optional)" value={newRemedy.safetyNotes} onChange={e => setNewRemedy({...newRemedy, safetyNotes: e.target.value})} />

              <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
                <Button type="button" onClick={() => setShowAddModal(false)} style={{ flex: 1, background: 'transparent', border: '1px solid var(--border-color)', color: 'var(--text-main)' }}>Cancel</Button>
                <Button type="submit" isLoading={loading} style={{ flex: 1, background: 'var(--accent-green)' }}>Submit Remedy</Button>
              </div>
            </form>
          </div>
        </div>
      )}

    </div>
  );
};
