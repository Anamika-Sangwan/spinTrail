package com.anamika.spintrail.service;

import com.anamika.spintrail.dto.LocationSuggestionDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class GeocodingService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${geocoding.nominatim.base-url}")
    private String baseUrl;

    @Value("${geocoding.nominatim.user-agent}")
    private String userAgent;

    public GeocodingService() {
        this.restClient = RestClient.create();
        this.objectMapper = new ObjectMapper();
    }

    // ─── Main Entry Point ──────────────────────────────────────────────────
    // Called when user types in the search bar
    // Returns up to 5 matching location suggestions
    public List<LocationSuggestionDto> searchLocations(String query) {
        try {
            String url = buildNominatimUrl(query);
            String responseJson = callNominatimApi(url);
            return parseNominatimResponse(responseJson);
        } catch (Exception e) {
            System.err.println("Geocoding failed for query: " + query
                    + " — " + e.getMessage());
            return List.of();   // return empty list, don't crash the app
        }
    }

    // ─── Get a Single Coordinate from a Place Name ─────────────────────────
    // Used internally when you just need one lat/lng (e.g. the best match)
    public double[] getCoordinates(String placeName) {
        List<LocationSuggestionDto> results = searchLocations(placeName);

        if (results.isEmpty()) {
            throw new RuntimeException(
                    "Could not find coordinates for: " + placeName
            );
        }

        // Nominatim returns results sorted by importance — first is best match
        LocationSuggestionDto best = results.get(0);
        return new double[]{best.getLatitude(), best.getLongitude()};
    }

    // ─── Build Nominatim URL ───────────────────────────────────────────────
    private String buildNominatimUrl(String query) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("q", query)
                .queryParam("format", "json")
                .queryParam("limit", 5)
                .queryParam("addressdetails", 1)    // returns structured address breakdown
                .build()
                .toUriString();
    }

    // ─── Call Nominatim API ────────────────────────────────────────────────
    private String callNominatimApi(String url) {
        return restClient.get()
                .uri(url)
                // Nominatim REQUIRES a User-Agent — requests without it get blocked
                .header("User-Agent", userAgent)
                // Also good practice to accept JSON explicitly
                .header("Accept", "application/json")
                .retrieve()
                .body(String.class);
    }

    // ─── Parse Nominatim Response ──────────────────────────────────────────
    private List<LocationSuggestionDto> parseNominatimResponse(String json)
            throws Exception {

        JsonNode root = objectMapper.readTree(json);
        List<LocationSuggestionDto> suggestions = new ArrayList<>();

        for (JsonNode node : root) {
            String displayName = node.path("display_name").asText();
            double lat = Double.parseDouble(node.path("lat").asText());
            double lon = Double.parseDouble(node.path("lon").asText());
            String type = node.path("type").asText();

            suggestions.add(new LocationSuggestionDto(
                    displayName, lat, lon, type
            ));
        }

        return suggestions;
    }
}