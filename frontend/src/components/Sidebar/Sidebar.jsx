import LocationSearch from '../LocationSearch/LocationSearch';
import RouteCard, { RouteCardSkeleton } from '../RouteCard/RouteCard';
import './Sidebar.css';

// Distance options shown as pills
const DISTANCE_OPTIONS = [
  { label: '10 km',  value: 10  },
  { label: '20 km',  value: 20  },
  { label: '35 km',  value: 35  },
  { label: '50 km',  value: 50  },
];

function Sidebar({
  selectedLocation,
  onLocationSelect,
  desiredDistance,
  onDistanceChange,
  routes,
  selectedRouteIndex,
  onRouteSelect,
  onGenerate,
  isLoading,
  error,
}) {

  // ── Decide what to show in the routes section ─────────────────────────
  const hasRoutes = routes && routes.length > 0;
  const showSkeletons = isLoading;
  const showEmpty = !isLoading && !hasRoutes && !error;
  const showError = !isLoading && !!error;
  const showRoutes = !isLoading && hasRoutes;

  // ── Generate button state ─────────────────────────────────────────────
  // Disabled if no location is selected or if already loading
  const canGenerate = !!selectedLocation && !isLoading;

  return (
    <div className="sidebar">

      {/* ── Header: Logo + Search ── */}
      <div className="sidebar-header">
        <div className="sidebar-logo">
          <span className="sidebar-logo-icon">🚴</span>
          <span className="sidebar-logo-text">SpinTrail</span>
        </div>
        <LocationSearch onLocationSelect={onLocationSelect} />
      </div>

      {/* ── Distance Selector ── */}
      <div className="distance-section">
        <div className="distance-label">Desired ride distance</div>
        <div className="distance-pills">
          {DISTANCE_OPTIONS.map((option) => (
            <button
              key={option.value}
              className={`distance-pill ${desiredDistance === option.value ? 'active' : ''}`}
              onClick={() => onDistanceChange(option.value)}
            >
              {option.label}
            </button>
          ))}
        </div>
      </div>

      {/* ── Routes Section ── */}
      <div className="routes-section">
        <div className="routes-section-header">
          <span className="routes-section-label">Route options</span>
          {showRoutes && (
            <span className="routes-count">{routes.length} found</span>
          )}
        </div>

        <div className="route-cards-list">

          {/* Loading skeletons */}
          {showSkeletons && (
            <>
              <RouteCardSkeleton />
              <RouteCardSkeleton />
              <RouteCardSkeleton />
              <RouteCardSkeleton />
            </>
          )}

          {/* Empty state */}
          {showEmpty && (
            <div className="routes-empty-state">
              <span className="routes-empty-icon">🗺️</span>
              <p className="routes-empty-text">
                {selectedLocation
                  ? 'Select a distance and hit Generate to see route options'
                  : 'Search for a start location to get started'
                }
              </p>
            </div>
          )}

          {/* Error state */}
          {showError && (
            <div className="routes-error">
              ⚠️ {error}
            </div>
          )}

          {/* Route cards */}
          {showRoutes && routes.map((route, index) => (
            <RouteCard
              key={route.id || index}
              route={route}
              index={index}
              isSelected={selectedRouteIndex === index}
              onClick={() => onRouteSelect(index)}
            />
          ))}

        </div>
      </div>

      {/* ── Footer: Generate Button ── */}
      <div className="sidebar-footer">
        <button
          className="generate-btn"
          onClick={onGenerate}
          disabled={!canGenerate}
        >
          {isLoading ? (
            <>
              <div className="btn-spinner" />
              Generating routes...
            </>
          ) : (
            <>
              🔄 Generate routes
            </>
          )}
        </button>

        {!selectedLocation && (
          <p className="generate-hint">
            Search for a location first
          </p>
        )}
      </div>

    </div>
  );
}

export default Sidebar;