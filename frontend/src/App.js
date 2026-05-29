import { useState, useEffect } from 'react';
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

  // Clear routes when location changes —
  // old routes belong to a different start point
  useEffect(() => {
    setRoutes([]);
    setError(null);
    setSelectedRouteIndex(0);
  }, [selectedLocation]);

  // Clear routes when distance changes —
  // prompt user to regenerate at new distance
  useEffect(() => {
    setRoutes([]);
    setError(null);
  }, [desiredDistance]);

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

      if (!result || result.length === 0) {
        setError(
          'No cycling routes found for this area. ' +
          'Try a different location or distance.'
        );
        return;
      }

      setRoutes(result);
      setSelectedRouteIndex(0);

    } catch (err) {
      console.error('Route generation failed:', err);

      // Give a helpful message depending on the error type
      if (err.response?.status === 503) {
        setError('Routing service is unavailable. Please try again shortly.');
      } else if (err.response?.status === 400) {
        setError('Invalid location. Please search for a different start point.');
      } else if (err.code === 'ERR_NETWORK') {
        setError('Cannot reach the server. Make sure Spring Boot is running on port 8080.');
      } else {
        setError('Something went wrong generating routes. Please try again.');
      }
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