import { useEffect, useRef } from 'react';
import {
  MapContainer,
  TileLayer,
  Marker,
  Polyline,
  useMap,
} from 'react-leaflet';
import './MapView.css';

// ── Route colors — each option gets a distinct color ──────────────────────
const ROUTE_COLORS = ['#378ADD', '#E85D3A', '#2DAA72', '#9B59B6'];

// ── This inner component handles flying the map to a new location ──────────
// It has to live inside <MapContainer> to access the Leaflet map instance
function MapController({ center }) {
  const map = useMap();

  useEffect(() => {
    if (center) {
      // Smoothly animate the map to the selected location
      map.flyTo([center.lat, center.lng], 13, {
        duration: 1.2,
      });
    }
  }, [center, map]);

  return null;
}

// ── Main MapView component ─────────────────────────────────────────────────
function MapView({ selectedLocation, routes, selectedRouteIndex }) {

  // Default center — Bangalore. Map starts here before user searches
  const defaultCenter = [12.9716, 77.5946];
  const defaultZoom = 12;

  // ── Fit map bounds to show full selected route ───────────────────────────
  const MapBoundsFitter = () => {
    const map = useMap();

    useEffect(() => {
      if (
        routes &&
        routes.length > 0 &&
        selectedRouteIndex !== null &&
        routes[selectedRouteIndex]?.routeCoordinates?.length > 0
      ) {
        const coords = routes[selectedRouteIndex].routeCoordinates;
        // Convert [lat, lng] arrays to Leaflet LatLng bounds
        const bounds = coords.map(c => [c[0], c[1]]);
        map.fitBounds(bounds, { padding: [40, 40] });
      }
    }, [map]);

    return null;
  };

  return (
    <div className="map-wrapper">

      {/* ── Leaflet Map ── */}
      <MapContainer
        center={defaultCenter}
        zoom={defaultZoom}
        style={{ width: '100%', height: '100%' }}
        zoomControl={true}
      >
        {/* OpenStreetMap tiles — free, no API key needed */}
        <TileLayer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          attribution='&copy; <a href="https://openstreetmap.org">OpenStreetMap</a>'
        />

        {/* Fly to location when user selects one */}
        {selectedLocation && (
          <MapController center={selectedLocation} />
        )}

        {/* Fit bounds when selected route changes */}
        {routes && routes.length > 0 && selectedRouteIndex !== null && (
          <MapBoundsFitter key={selectedRouteIndex} />
        )}

        {/* Start point marker */}
        {selectedLocation && (
          <Marker position={[selectedLocation.lat, selectedLocation.lng]} />
        )}

        {/* Draw all routes — selected one is bold and colored,
            others are faded gray so the user can compare */}
        {routes && routes.map((route, index) => {
          if (!route.routeCoordinates || route.routeCoordinates.length === 0) {
            return null;
          }

          const isSelected = index === selectedRouteIndex;
          const positions = route.routeCoordinates.map(c => [c[0], c[1]]);

          return (
            <Polyline
              key={route.id || index}
              positions={positions}
              pathOptions={{
                color: isSelected ? ROUTE_COLORS[index % ROUTE_COLORS.length] : '#bbb',
                weight: isSelected ? 5 : 2,
                opacity: isSelected ? 0.9 : 0.5,
                dashArray: isSelected ? null : '6, 6',
              }}
            />
          );
        })}

      </MapContainer>

      {/* ── Location badge overlay ── */}
      {selectedLocation && (
        <div className="map-location-badge">
          📍 {selectedLocation.label.split(',').slice(0, 2).join(',')}
        </div>
      )}

      {/* ── Empty state before any location is chosen ── */}
      {!selectedLocation && (
        <div className="map-empty-state">
          <p>Search for a start location to generate routes</p>
        </div>
      )}

    </div>
  );
}

export default MapView;