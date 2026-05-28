-- Enable PostGIS extension on the spintrail database
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;

-- Verify it worked (optional, shows in docker logs)
SELECT PostGIS_Version();