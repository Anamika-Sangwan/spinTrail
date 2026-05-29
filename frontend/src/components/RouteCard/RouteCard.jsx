import { useState } from 'react';
import { saveRoute, unsaveRoute } from '../../services/apiService';
import './RouteCard.css';

const ROUTE_COLORS = ['#378ADD', '#E85D3A', '#2DAA72', '#9B59B6'];

function RouteCard({ route, index, isSelected, onClick }) {
  const [isSaved, setIsSaved] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  const formatDistance = (km) => {
    if (!km && km !== 0) return '—';
    return `${km.toFixed(1)} km`;
  };

  const formatDuration = (minutes) => {
    if (!minutes && minutes !== 0) return '—';
    if (minutes < 60) return `${minutes} min`;
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return m > 0 ? `${h}h ${m}m` : `${h}h`;
  };

  const formatElevation = (metres) => {
    if (!metres && metres !== 0) return '—';
    return `${Math.round(metres)} m`;
  };

  const getBadgeClass = (difficulty) => {
    switch (difficulty?.toUpperCase()) {
      case 'EASY':     return 'badge-easy';
      case 'MODERATE': return 'badge-moderate';
      case 'HARD':     return 'badge-hard';
      default:         return 'badge-easy';
    }
  };

  // ── Handle save/unsave toggle ──────────────────────────────────────────
  const handleSaveToggle = async (e) => {
    // Stop the click from also selecting the card
    e.stopPropagation();
    if (!route.id || isSaving) return;

    setIsSaving(true);
    try {
      if (isSaved) {
        await unsaveRoute(route.id);
        setIsSaved(false);
      } else {
        await saveRoute(route.id);
        setIsSaved(true);
      }
    } catch (err) {
      console.error('Save toggle failed:', err);
    } finally {
      setIsSaving(false);
    }
  };

  const routeColor = ROUTE_COLORS[index % ROUTE_COLORS.length];

  return (
    <div
      className={`route-card ${isSelected ? 'selected' : ''}`}
      onClick={onClick}
    >
      <div className="route-color-bar" style={{ background: routeColor }} />

      <div className="route-card-top">
        <span className="route-name">
          {route.routeName || `Route ${index + 1}`}
        </span>
        <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
          <span className={`difficulty-badge ${getBadgeClass(route.difficulty)}`}>
            {route.difficulty
              ? route.difficulty.charAt(0) + route.difficulty.slice(1).toLowerCase()
              : 'Easy'
            }
          </span>
          {/* Save button — only shows when route has an ID (i.e. saved in DB) */}
          {route.id && (
            <button
              onClick={handleSaveToggle}
              disabled={isSaving}
              title={isSaved ? 'Remove from saved' : 'Save this route'}
              style={{
                background: 'none',
                border: 'none',
                cursor: isSaving ? 'wait' : 'pointer',
                fontSize: '16px',
                padding: '0 2px',
                lineHeight: 1,
                opacity: isSaving ? 0.5 : 1,
              }}
            >
              {isSaved ? '❤️' : '🤍'}
            </button>
          )}
        </div>
      </div>

      <div className="route-stats">
        <div className="stat-item">
          <span className="stat-value">{formatDistance(route.totalDistanceKm)}</span>
          <span className="stat-label">Distance</span>
        </div>
        <div className="stat-item">
          <span className="stat-value">{formatDuration(route.estimatedDurationMinutes)}</span>
          <span className="stat-label">Duration</span>
        </div>
        <div className="stat-item">
          <span className="stat-value">{formatElevation(route.elevationGain)}</span>
          <span className="stat-label">Elevation</span>
        </div>
      </div>
    </div>
  );
}

export function RouteCardSkeleton() {
  return (
    <div className="route-card-skeleton">
      <div className="skeleton-line medium" />
      <div className="skeleton-line full" />
      <div className="skeleton-line short" />
    </div>
  );
}
export default RouteCard;