package com.anamika.spintrail.service;

import com.anamika.spintrail.dto.RouteRequestDto;
import com.anamika.spintrail.entity.RouteOption;
import com.anamika.spintrail.entity.Waypoint;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class TrailGeneratorService {
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);
    private static final double EARTH_RADIUS_METRES = 6_371_000.0;

    public List<RouteOption> generateCandidateRoutes(RouteRequestDto request) {
        List<RouteOption> candidates = new ArrayList<>();
        double lat = request.getLatitude();
        double lng = request.getLongitude();
        double desiredDistanceKm = request.getDesiredDistanceKm();
        int[] waypointCounts = {3, 4, 4, 5};          // different shapes
        double[] angleOffsets = {0, 45, 22.5, 15};    // different rotations
        for (int i = 0; i < 4; i++) {
            List<double[]> waypoints = generateCircularWaypoints(
                    lat, lng,
                    desiredDistanceKm,
                    waypointCounts[i],
                    angleOffsets[i]
            );
            RouteOption candidate = buildCandidateRouteOption(
                    waypoints, lat, lng,
                    "Route Option " + (i + 1),
                    desiredDistanceKm
            );
            candidates.add(candidate);
        }
        return candidates;
    }

    private List<double[]> generateCircularWaypoints( double startLat, double startLng,  double desiredDistanceKm, int numWaypoints, double angleOffsetDegrees) {
        List<double[]> waypoints = new ArrayList<>();
        // Radius of the circle so that cycling around it ≈ desired distance
        // Circumference = 2πr → r = distance / (2π)
        double radiusKm = (desiredDistanceKm / (2 * Math.PI));
        double radiusMetres = radiusKm * 1000;
        // First point is always the start
        waypoints.add(new double[]{startLat, startLng});
        // Place intermediate waypoints evenly around the circle
        for (int i = 1; i <= numWaypoints; i++) {
            double angleDegrees = angleOffsetDegrees + ((360.0 / numWaypoints) * i);
            double[] point = calculateDestinationPoint(
                    startLat, startLng, radiusMetres, angleDegrees
            );
            waypoints.add(point);
        }
        // Close the loop — end point = start point
        waypoints.add(new double[]{startLat, startLng});
        return waypoints;
    }

    private double[] calculateDestinationPoint(
            double lat, double lng,
            double distanceMetres,
            double bearingDegrees) {

        double latRad = Math.toRadians(lat);
        double lngRad = Math.toRadians(lng);
        double bearingRad = Math.toRadians(bearingDegrees);
        double angularDistance = distanceMetres / EARTH_RADIUS_METRES;

        double destLatRad = Math.asin(
                Math.sin(latRad) * Math.cos(angularDistance) +
                        Math.cos(latRad) * Math.sin(angularDistance) * Math.cos(bearingRad)
        );

        double destLngRad = lngRad + Math.atan2(
                Math.sin(bearingRad) * Math.sin(angularDistance) * Math.cos(latRad),
                Math.cos(angularDistance) - Math.sin(latRad) * Math.sin(destLatRad)
        );

        return new double[]{
                Math.toDegrees(destLatRad),
                Math.toDegrees(destLngRad)
        };
    }

    private RouteOption buildCandidateRouteOption(List<double[]> waypointCoords, double startLat, double startLng, String routeName, double estimatedDistanceKm) {
        RouteOption routeOption = new RouteOption();
        routeOption.setRouteName(routeName);
        routeOption.setTotalDistance(estimatedDistanceKm);  // placeholder until ORS confirms real distance
        // Set start point as PostGIS geometry
        Point startPoint = GEOMETRY_FACTORY.createPoint(
                new Coordinate(startLng, startLat)  // NOTE: JTS is (lng, lat) not (lat, lng)
        );
        routeOption.setStartPoint(startPoint);
        // Build ordered Waypoint entities
        List<Waypoint> waypoints = new ArrayList<>();
        for (int i = 0; i < waypointCoords.size(); i++) {
            double[] coord = waypointCoords.get(i);
            Waypoint waypoint = new Waypoint();
            Point location = GEOMETRY_FACTORY.createPoint(
                    new Coordinate(coord[1], coord[0])  // (lng, lat)
            );
            waypoint.setLocation(location);
            waypoint.setSequenceOrder(i);
            waypoint.setRouteOption(routeOption);
            // Label first and last waypoints
            if (i == 0 || i == waypointCoords.size() - 1) {
                waypoint.setLabel("Start / Finish");
            } else {
                waypoint.setLabel("Checkpoint " + i);
            }
            waypoints.add(waypoint);
        }
        routeOption.setWaypoints(waypoints);
        return routeOption;
    }

    public List<double[]> extractCoordinates(RouteOption routeOption) {
        return routeOption.getWaypoints().stream()
                .sorted((a, b) -> a.getSequenceOrder() - b.getSequenceOrder())
                .map(wp -> new double[]{
                        wp.getLocation().getY(),   // lat  (JTS stores as Y)
                        wp.getLocation().getX()    // lng  (JTS stores as X)
                })
                .toList();
    }

}