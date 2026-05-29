import { useState } from 'react';
import L from 'leaflet';
import markerIcon from 'leaflet/dist/images/marker-icon.png';
import markerShadow from 'leaflet/dist/images/marker-shadow.png';
import Sidebar from './components/Sidebar/Sidebar';
import MapView from './components/MapView/MapView';
import { generateRoutes } from './services/apiService';
import './App.css';

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
});

function App() {
  const [selectedLocation, setSelectedLocation]     = useState(null);
  const [desiredDistance, setDesiredDistance]       = useState(20);
  const [routes, setRoutes]                         = useState([]);
  const [selectedRouteIndex, setSelectedRouteIndex] = useState(0);
  const [isLoading, setIsLoading]                   = useState(false);
  const [error, setError]                           = useState(null);

  // ── Called when user hits Generate routes ──────────────────────────────
  const handleGenerate = async () => {
    if (!selectedLocation) return;

    setIsLoading(true);
    setError(null);
    setRoutes([]);

    try {
      const result = await generateRoutes(
        selectedLocation.lat,
        selectedLocation.lng,
        desiredDistance
      );
      setRoutes(result);
      setSelectedRouteIndex(0);   // auto-select first route
    } catch (err) {
      console.error('Route generation failed:', err);
      setError('Could not generate routes for this location. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="app-container">
      <Sidebar
        selectedLocation={selectedLocation}
        onLocationSelect={setSelectedLocation}
        desiredDistance={desiredDistance}
        onDistanceChange={setDesiredDistance}
        routes={routes}
        selectedRouteIndex={selectedRouteIndex}
        onRouteSelect={setSelectedRouteIndex}
        onGenerate={handleGenerate}
        isLoading={isLoading}
        error={error}
      />

      <MapView
        selectedLocation={selectedLocation}
        routes={routes}
        selectedRouteIndex={selectedRouteIndex}
      />
    </div>
  );
}

export default App;