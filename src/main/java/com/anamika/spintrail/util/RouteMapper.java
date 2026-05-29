package com.anamika.spintrail.util;

import com.anamika.spintrail.dto.RouteResponseDto;
import com.anamika.spintrail.entity.RouteOption;
import com.anamika.spintrail.entity.Waypoint;
import org.locationtech.jts.geom.Coordinate;

import java.util.Arrays;
import java.util.List;

public class RouteMapper {

    // Convert a RouteOption entity → RouteResponseDto for the frontend
    public static RouteResponseDto toDto(RouteOption route) {

        // Extract route path coordinates from the LineString geometry
        List<double[]> routeCoordinates = List.of();
        if (route.getRoutePath() != null) {
            routeCoordinates = Arrays.stream(
                            route.getRoutePath().getCoordinates()
                    )
                    .map(c -> new double[]{c.y, c.x})   // back to [lat, lng] for frontend
                    .toList();
        }

        // Convert Waypoint entities → WaypointDtos
        List<RouteResponseDto.WaypointDto> waypointDtos = route.getWaypoints()
                .stream()
                .sorted((a, b) -> a.getSequenceOrder() - b.getSequenceOrder())
                .map(wp -> new RouteResponseDto.WaypointDto(
                        wp.getLocation().getY(),    // lat
                        wp.getLocation().getX(),    // lng
                        wp.getSequenceOrder(),
                        wp.getLabel()
                ))
                .toList();

        return new RouteResponseDto(
                route.getId(),
                route.getRouteName(),
                route.getTotalDistance(),
                route.getEstimatedDuration() != null ? route.getEstimatedDuration() : 0,
                route.getDifficulty() != null ? route.getDifficulty().name() : "UNKNOWN",
                route.getStartPoint().getY(),   // startLat
                route.getStartPoint().getX(),   // startLng
                routeCoordinates,
                waypointDtos
        );
    }

    public static List<RouteResponseDto> toDtoList(List<RouteOption> routes) {
        return routes.stream().map(RouteMapper::toDto).toList();
    }
}