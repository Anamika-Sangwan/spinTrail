package com.anamika.spintrail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RouteResponseDto {

    private Long id;
    private String routeName;
    private double totalDistanceKm;
    private int estimatedDurationMinutes;
    private String difficulty;
    private double startLat;
    private double startLng;

    // The full route geometry as a list of [lat, lng] pairs
    // This is what Leaflet draws on the map
    private List<double[]> routeCoordinates;

    // The ordered waypoints — shown as markers on the map
    private List<WaypointDto> waypoints;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class WaypointDto {
        private double latitude;
        private double longitude;
        private int sequenceOrder;
        private String label;
    }
}