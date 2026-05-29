import { useState } from 'react';
import L from 'leaflet';
import markerIcon from 'leaflet/dist/images/marker-icon.png';
import markerShadow from 'leaflet/dist/images/marker-shadow.png';
import LocationSearch from './components/LocationSearch/LocationSearch';
import MapView from './components/MapView/MapView';
import RouteCard, { RouteCardSkeleton } from './components/RouteCard/RouteCard';
import './App.css';

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
});

// Mock routes — same shape as your RouteResponseDto from the backend
const MOCK_ROUTES = [
  {
    id: 1,
    routeName: 'Route Option 1',
    totalDistanceKm: 18.4,
    estimatedDurationMinutes: 62,
    elevationGain: 85,
    difficulty: 'EASY',
    routeCoordinates: [],
  },
  {
    id: 2,
    routeName: 'Route Option 2',
    totalDistanceKm: 21.1,
    estimatedDurationMinutes: 71,
    elevationGain: 140,
    difficulty: 'MODERATE',
    routeCoordinates: [],
  },
  {
    id: 3,
    routeName: 'Route Option 3',
    totalDistanceKm: 19.7,
    estimatedDurationMinutes: 66,
    elevationGain: 110,
    difficulty: 'MODERATE',
    routeCoordinates: [],
  },
  {
    id: 4,
    routeName: 'Route Option 4',
    totalDistanceKm: 23.8,
    estimatedDurationMinutes: 80,
    elevationGain: 210,
    difficulty: 'HARD',
    routeCoordinates: [],
  },
];

function App() {
  const [selectedLocation, setSelectedLocation] = useState(null);
  const [selectedRouteIndex, setSelectedRouteIndex] = useState(0);
  const [isLoading, setIsLoading] = useState(false);

  return (
    <div className="app-container">

      {/* Temporary sidebar for testing cards */}
      <div style={{
        width: '300px',
        background: '#fff',
        borderRight: '1px solid #e8e8e8',
        padding: '16px',
        display: 'flex',
        flexDirection: 'column',
        gap: '10px',
        overflowY: 'auto',
      }}>
        <LocationSearch onLocationSelect={setSelectedLocation} />

        <p style={{ fontSize: '12px', color: '#999', marginTop: '8px' }}>
          Route options
        </p>

        {/* Show skeletons when loading */}
        {isLoading && (
          <>
            <RouteCardSkeleton />
            <RouteCardSkeleton />
            <RouteCardSkeleton />
          </>
        )}

        {/* Show route cards */}
        {!isLoading && MOCK_ROUTES.map((route, index) => (
          <RouteCard
            key={route.id}
            route={route}
            index={index}
            isSelected={selectedRouteIndex === index}
            onClick={() => setSelectedRouteIndex(index)}
          />
        ))}
      </div>

      {/* Map */}
      <MapView
        selectedLocation={selectedLocation}
        routes={MOCK_ROUTES}
        selectedRouteIndex={selectedRouteIndex}
      />

    </div>
  );
}

export default App;