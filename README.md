# spinTrail
spinTrail/
│
├── backend/                          # Spring Boot Application
│   ├── src/main/java/com/spintrail/
│   │   ├── BicycleTrailApplication.java
│   │   │
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   ├── CorsConfig.java
│   │   │   └── MapApiConfig.java          # API keys, routing service config
│   │   │
│   │   ├── controller/
│   │   │   ├── TrailController.java       # REST endpoints
│   │   │   └── LocationController.java    # Geocoding / search endpoints
│   │   │
│   │   ├── service/
│   │   │   ├── TrailGeneratorService.java # Core circular route logic
│   │   │   ├── RoutingService.java        # Calls ORS/GraphHopper API
│   │   │   └── GeocodingService.java      # Converts address → coordinates
│   │   │
│   │   ├── repository/
│   │   │   ├── TrailRepository.java       # JPA + PostGIS queries
│   │   │   └── WaypointRepository.java
|     |     |      └── RouteOptionRepository.java

│   │   │
│   │   ├── model/
│   │   │   ├── Trail.java                 # Trail entity
│   │   │   ├── RouteOption.java           # A generated circular route
│   │   │   ├── Waypoint.java              # Lat/lng point on a route
│   │   │   └── SavedRoute.java            # User-saved routes
│   │   │
│   │   └── dto/
│   │       ├── RouteRequestDTO.java       # { lat, lng, desiredDistance }
│   │       └── RouteResponseDTO.java      # { routes: [...], distances: [...] }
│   │
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/                  # Flyway migrations
│   │       ├── V1__init_postgis.sql
│   │       └── V2__create_trails_table.sql
│   │
│   └── pom.xml
│
├── frontend/                         # React Application
│   ├── src/
│   │   ├── components/
│   │   │   ├── MapView/               # Leaflet/MapBox map component
│   │   │   ├── RouteSelector/         # Cards showing route options
│   │   │   ├── LocationSearch/        # Search bar with autocomplete
│   │   │   └── RouteDetails/          # Distance, elevation, duration
│   │   │
│   │   ├── services/
│   │   │   └── apiService.js          # Axios calls to Spring Boot
│   │   │
│   │   ├── store/                     # State management (Redux or Zustand)
│   │   └── App.jsx
│   │
│   └── package.json
│
└── docker-compose.yml                # PostgreSQL + PostGIS container
