# SpinTrail

SpinTrail is a cycling trail and circular route generation platform that helps users discover biking paths based on a chosen start/end location and desired distance.

The application uses:

* Spring Boot for the backend
* React for the frontend
* PostgreSQL + PostGIS for geospatial data
* OpenRouteService / GraphHopper for routing
* Leaflet or Mapbox for map visualization

---

# Project Structure

```text
SpinTrail/
│
├── backend/                         # Spring Boot Application
│   │
│   ├── src/main/java/com/spintrail/
│   │   │
│   │   ├── SpinTrailApplication.java
│   │   │
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   ├── CorsConfig.java
│   │   │   └── MapApiConfig.java          # API keys and routing config
│   │   │
│   │   ├── controller/
│   │   │   ├── TrailController.java       # Trail REST endpoints
│   │   │   └── LocationController.java    # Geocoding/search endpoints
│   │   │
│   │   ├── service/
│   │   │   ├── TrailGeneratorService.java # Circular route generation logic
│   │   │   ├── RoutingService.java        # ORS / GraphHopper integration
│   │   │   └── GeocodingService.java      # Address → Coordinates
│   │   │
│   │   ├── repository/
│   │   │   ├── TrailRepository.java
│   │   │   ├── WaypointRepository.java
│   │   │   └── RouteOptionRepository.java
│   │   │
│   │   ├── entity/
│   │   │   ├── Trail.java
│   │   │   ├── RouteOption.java
│   │   │   ├── Waypoint.java
│   │   │   └── SavedRoute.java
│   │   │
│   │   └── dto/
│   │       ├── RouteRequestDTO.java       # { lat, lng, desiredDistance }
│   │       └── RouteResponseDTO.java      # Generated route response
│   │
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/                  # Flyway migrations
│   │       ├── V1__init_postgis.sql
│   │       └── V2__create_trails_table.sql
│   │
│   └── pom.xml
│
├── frontend/                              # React Application
│   │
│   ├── src/
│   │   │
│   │   ├── components/
│   │   │   ├── MapView/                   # Leaflet/Mapbox map component
│   │   │   ├── RouteSelector/             # Route option cards
│   │   │   ├── LocationSearch/            # Search/autocomplete
│   │   │   └── RouteDetails/              # Distance/elevation/duration
│   │   │
│   │   ├── services/
│   │   │   └── apiService.js              # Axios API calls
│   │   │
│   │   ├── store/                         # Redux/Zustand state management
│   │   │
│   │   └── App.jsx
│   │
│   └── package.json
│
└── docker-compose.yml                     # PostgreSQL + PostGIS container
```

---

# Tech Stack

## Backend

* Java
* Spring Boot
* Spring Data JPA
* Hibernate Spatial
* PostgreSQL
* PostGIS

## Frontend

* React
* Leaflet / Mapbox
* Axios
* Redux or Zustand

## APIs

* OpenRouteService
* GraphHopper
* Nominatim / Geocoding API

---

# Features

* Generate circular cycling routes
* Select preferred distance
* Route elevation and difficulty analysis
* Save favorite trails
* Interactive map visualization
* Geospatial trail storage using PostGIS
* Waypoint and route optimization

---

# Database

SpinTrail uses PostgreSQL with the PostGIS extension for handling geospatial data such as:

* Route paths
* Coordinates
* Trail geometry
* Distance calculations

Enable PostGIS:

```sql
CREATE EXTENSION postgis;
```

---

# Running the Backend

```bash
cd backend
./mvnw spring-boot:run
```

---

# Running the Frontend

```bash
cd frontend
npm install
npm run dev
```

---

# Future Improvements

* User authentication
* Real-time navigation
* GPX export/import
* AI-based route recommendations
* Weather integration
* Social sharing features
