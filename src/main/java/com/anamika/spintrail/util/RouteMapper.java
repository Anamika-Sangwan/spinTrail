package com.anamika.spintrail.util;

import com.anamika.spintrail.dto.RouteResponseDto;
import com.anamika.spintrail.entity.RouteOption;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class RouteMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Convert a RouteOption entity → RouteResponseDto for the frontend
    public static RouteResponseDto toDto(RouteOption route) {

        // Extract route path coordinates from stored JSON string
        List<double[]> routeCoordinates = List.of();

        if (route.getRoutePath() != null) {
            try {
                routeCoordinates = objectMapper.readValue(
                        route.getRoutePath(),
                        new TypeReference<List<double[]>>() {}
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Convert Waypoint entities → WaypointDtos
        List<RouteResponseDto.WaypointDto> waypointDtos = route.getWaypoints()
                .stream()
                .sorted((a, b) -> a.getSequenceOrder() - b.getSequenceOrder())
                .map(wp -> new RouteResponseDto.WaypointDto(
                        wp.getLatitude(),      // lat
                        wp.getLongitude(),     // lng
                        wp.getSequenceOrder(),
                        wp.getLabel()
                ))
                .toList();

        return new RouteResponseDto(
                route.getId(),
                route.getRouteName(),
                route.getTotalDistance(),
                route.getEstimatedDuration() != null
                        ? route.getEstimatedDuration()
                        : 0,
                route.getDifficulty() != null
                        ? route.getDifficulty().name()
                        : "UNKNOWN",

                route.getStartLatitude(),     // startLat
                route.getStartLongitude(),    // startLng

                routeCoordinates,
                waypointDtos
        );
    }

    public static List<RouteResponseDto> toDtoList(List<RouteOption> routes) {
        return routes.stream()
                .map(RouteMapper::toDto)
                .toList();
    }
}