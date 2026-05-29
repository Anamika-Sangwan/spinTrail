import axios from 'axios';

// All API calls go to your Spring Boot backend on port 8080
const BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// ─── Location Search ───────────────────────────────────────────────────────
// Called as the user types in the search bar
// Returns a list of LocationSuggestionDto objects from your GeocodingService
export const searchLocations = async (query) => {
  const response = await api.get('/locations/search', {
    params: { q: query },
  });
  return response.data;
};

// ─── Generate Routes ───────────────────────────────────────────────────────
// Core call — sends start coordinates + desired distance to your backend
// Returns 3-4 RouteResponseDto objects from your TrailController
export const generateRoutes = async (latitude, longitude, desiredDistanceKm) => {
  const response = await api.post('/trails/generate', {
    latitude,
    longitude,
    desiredDistanceKm,
  });
  return response.data;
};

// ─── Get Single Route ──────────────────────────────────────────────────────
// Fetches one saved route by its ID
export const getRouteById = async (id) => {
  const response = await api.get(`/trails/route/${id}`);
  return response.data;
};

// ─── Get Nearby Saved Routes ───────────────────────────────────────────────
// Fetches previously generated routes near a location
export const getNearbyRoutes = async (lat, lng, radiusMetres = 5000) => {
  const response = await api.get('/trails/nearby', {
    params: { lat, lng, radius: radiusMetres },
  });
  return response.data;
};

// ── Save a route ───────────────────────────────────────────────────────────
export const saveRoute = async (routeId, label = 'My saved route') => {
  const response = await api.post(`/trails/save/${routeId}`, null, {
    params: { label },
  });
  return response.data;
};

// ── Unsave a route ─────────────────────────────────────────────────────────
export const unsaveRoute = async (routeId) => {
  const response = await api.delete(`/trails/save/${routeId}`);
  return response.data;
};

// ── Get all saved routes ───────────────────────────────────────────────────
export const getSavedRoutes = async () => {
  const response = await api.get('/trails/saved');
  return response.data;
};