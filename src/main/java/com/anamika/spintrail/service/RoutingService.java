package com.anamika.spintrail.service;

import com.anamika.spintrail.constants.enums.DifficultyLevel;
import com.anamika.spintrail.entity.RouteOption;
import com.anamika.spintrail.entity.Trail;
import com.anamika.spintrail.util.PolylineDecoder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoutingService {

    private static final GeometryFactory GEOMETRY_FACTORY =
            new GeometryFactory(new PrecisionModel(), 4326);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final TrailGeneratorService trailGeneratorService;

    @Value("${routing.openrouteservice.api-key}")
    private String apiKey;

    @Value("${routing.openrouteservice.base-url}")
    private String baseUrl;

    public RoutingService(TrailGeneratorService trailGeneratorService) {
        this.restClient = RestClient.create();
        this.objectMapper = new ObjectMapper();
        this.trailGeneratorService = trailGeneratorService;
    }

    // ─── Main Entry Point ─────────────────────────────────────────────────
    // Takes candidate routes from TrailGeneratorService,
    // calls ORS for each one, fills in real road geometry + distance
    public List<RouteOption> buildRealRoutes(List<RouteOption> candidates) {
        List<RouteOption> realRoutes = new ArrayList<>();

        for (RouteOption candidate : candidates) {
            try {
                RouteOption realRoute = snapToRealRoads(candidate);
                realRoutes.add(realRoute);
            } catch (Exception e) {
                // If ORS fails for one candidate, skip it — don't fail all routes
                System.err.println("ORS failed for candidate: "
                        + candidate.getRouteName() + " — " + e.getMessage());
            }
        }

        return realRoutes;
    }

    // ─── Snap One Candidate Route to Real Roads ────────────────────────────
    private RouteOption snapToRealRoads(RouteOption candidate) throws Exception {

        // 1. Extract waypoint coordinates from the candidate
        List<double[]> waypointCoords =
                trailGeneratorService.extractCoordinates(candidate);

        // 2. Build the JSON body ORS expects
        String requestBody = buildOrsRequestBody(waypointCoords);

        // 3. Call ORS API
        String responseJson = callOrsApi(requestBody);

        // 4. Parse the response
        JsonNode root = objectMapper.readTree(responseJson);
        JsonNode route = root.path("routes").get(0);   // take first route

        if (route == null || route.isMissingNode()) {
            throw new RuntimeException("ORS returned no routes for this candidate");
        }

        // 5. Extract distance and duration
        JsonNode summary = route.path("summary");
        double distanceMetres = summary.path("distance").asDouble();
        double durationSeconds = summary.path("duration").asDouble();

        // 6. Decode the polyline geometry into a JTS LineString
        String encodedGeometry = route.path("geometry").asText();
        List<double[]> decodedPoints = PolylineDecoder.decode(encodedGeometry);
        Coordinate[] coordinates = PolylineDecoder.toJtsCoordinates(decodedPoints);
        LineString routePath = GEOMETRY_FACTORY.createLineString(coordinates);

        // 7. Write real data back into the RouteOption
        candidate.setRoutePath(routePath);
        candidate.setTotalDistance(distanceMetres / 1000.0);  // convert to km
        candidate.setEstimatedDuration((int)(durationSeconds / 60.0)); // convert to mins
        candidate.setDifficulty(determineDifficulty(distanceMetres / 1000.0));

        return candidate;
    }

    // ─── Build ORS Request Body ────────────────────────────────────────────
    private String buildOrsRequestBody(List<double[]> waypointCoords) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        ArrayNode coordinates = objectMapper.createArrayNode();

        for (double[] coord : waypointCoords) {
            ArrayNode point = objectMapper.createArrayNode();
            point.add(coord[1]);   // lng first — ORS expects [lng, lat]
            point.add(coord[0]);   // lat second
            coordinates.add(point);
        }

        body.set("coordinates", coordinates);

        // Tell ORS to return encoded polyline (smaller payload than GeoJSON)
        body.put("geometry", true);
        body.put("instructions", false);   // we don't need turn-by-turn

        return objectMapper.writeValueAsString(body);
    }

    // ─── Call ORS API ──────────────────────────────────────────────────────
    private String callOrsApi(String requestBody) {
        return restClient.post()
                .uri(baseUrl)
                .header("Authorization", apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json, application/geo+json")
                .body(requestBody)
                .retrieve()
                .body(String.class);
    }

    // ─── Auto-determine Difficulty From Distance ───────────────────────────
    // Simple heuristic — can be refined later with elevation data
    private DifficultyLevel determineDifficulty(double distanceKm) {
        if (distanceKm <= 15) return DifficultyLevel.EASY;
        if (distanceKm <= 35) return DifficultyLevel.MODERATE;
        return DifficultyLevel.HARD;
    }
}