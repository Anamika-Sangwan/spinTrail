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
├── src/main/java/com/anamika/spintrail/
│   ├── config/
│   │   ├── CorsConfig.java              
│   │   └── GlobalExceptionHandler.java  
│   ├── controller/
│   │   ├── LocationController.java      
│   │   └── TrailController.java          (save endpoints added)
│   ├── dto/
│   │   ├── LocationSuggestionDto.java   
│   │   ├── RouteRequestDto.java         
│   │   └── RouteResponseDto.java             
│   ├── entity/
│   │   ├── RouteOption.java             
│   │   ├── SavedRoute.java                 
│   │   ├── Trail.java                   
│   │   └── Waypoint.java                
│   ├── repository/
│   │   ├── RouteOptionRepository.java   
│   │   ├── SavedRouteRepository.java    
│   │   ├── TrailRepository.java         
│   │   └── WaypointRepository.java      
│   ├── service/
│   │   ├── GeocodingService.java        
│   │   ├── RoutingService.java          
│   │   └── TrailGeneratorService.java   
│   └── util/
│       ├── PolylineDecoder.java         
│       └── RouteMapper.java             
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── LocationSearch/          
│   │   │   ├── MapView/                 
│   │   │   ├── RouteCard/                (save button added)
│   │   │   └── Sidebar/                 
│   │   ├── services/
│   │   │   └── apiService.js             (save calls added)
│   │   ├── App.js                        (edge cases handled)
│   │   ├── App.css                      
│   │   └── index.js                     
│   ├── .env                             
│   ├── .env.production                  
│   └── package.json                     
│
├── docker-compose.yml                   
├── init.sql                             
└── pom.xml                              
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

## APIs

* OpenRouteService

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
* Redis Cache
* Session Management
