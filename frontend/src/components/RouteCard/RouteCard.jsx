import './RouteCard.css';

// These must match ROUTE_COLORS in MapView.jsx exactly
// so the card bar color matches the drawn route color on the map
const ROUTE_COLORS = ['#378ADD', '#E85D3A', '#2DAA72', '#9B59B6'];

function RouteCard({ route, index, isSelected, onClick }) {

  // ── Format distance ───────────────────────────────────────────────────
  const formatDistance = (km) => {
    if (!km && km !== 0) return '—';
    return `${km.toFixed(1)} km`;
  };

  // ── Format duration ───────────────────────────────────────────────────
  const formatDuration = (minutes) => {
    if (!minutes && minutes !== 0) return '—';
    if (minutes < 60) return `${minutes} min`;
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return m > 0 ? `${h}h ${m}m` : `${h}h`;
  };

  // ── Format elevation ──────────────────────────────────────────────────
  const formatElevation = (metres) => {
    if (!metres && metres !== 0) return '—';
    return `${Math.round(metres)} m`;
  };

  // ── Difficulty badge class ────────────────────────────────────────────
  const getBadgeClass = (difficulty) => {
    switch (difficulty?.toUpperCase()) {
      case 'EASY':     return 'badge-easy';
      case 'MODERATE': return 'badge-moderate';
      case 'HARD':     return 'badge-hard';
      default:         return 'badge-easy';
    }
  };

  const routeColor = ROUTE_COLORS[index % ROUTE_COLORS.length];

  return (
    <div
      className={`route-card ${isSelected ? 'selected' : ''}`}
      onClick={onClick}
    >
      {/* Color bar on left edge matches the map polyline color */}
      <div
        className="route-color-bar"
        style={{ background: routeColor }}
      />

      {/* ── Top row ── */}
      <div className="route-card-top">
        <span className="route-name">
          {route.routeName || `Route ${index + 1}`}
        </span>
        <span className={`difficulty-badge ${getBadgeClass(route.difficulty)}`}>
          {route.difficulty
            ? route.difficulty.charAt(0) + route.difficulty.slice(1).toLowerCase()
            : 'Easy'
          }
        </span>
      </div>

      {/* ── Stats row ── */}
      <div className="route-stats">
        <div className="stat-item">
          <span className="stat-value">
            {formatDistance(route.totalDistanceKm)}
          </span>
          <span className="stat-label">Distance</span>
        </div>

        <div className="stat-item">
          <span className="stat-value">
            {formatDuration(route.estimatedDurationMinutes)}
          </span>
          <span className="stat-label">Duration</span>
        </div>

        <div className="stat-item">
          <span className="stat-value">
            {formatElevation(route.elevationGain)}
          </span>
          <span className="stat-label">Elevation</span>
        </div>
      </div>

    </div>
  );
}

// ── Skeleton shown while routes are loading ────────────────────────────────
// Export separately so Sidebar can use it during loading state
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