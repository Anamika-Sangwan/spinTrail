package com.anamika.spintrail.repository;

import com.anamika.spintrail.entity.Waypoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WaypointRepository extends JpaRepository<Waypoint,Long> {
    // Get all waypoints for a route, in order — critical for rendering the path
    List<Waypoint> findByRouteOptionIdOrderBySequenceOrderAsc(Long routeOptionId);

    // Get all waypoints belonging to a specific trail segment
    List<Waypoint> findByTrailId(Long trailId);

    // Find waypoints near a coordinate — useful for snapping user clicks to routes
    @Query(value = """
        SELECT * FROM waypoints
        WHERE ST_DWithin(
            location::geography,
            ST_MakePoint(:lng, :lat)::geography,
            :radiusMetres
        )
        ORDER BY ST_Distance(
            location::geography,
            ST_MakePoint(:lng, :lat)::geography
        ) ASC
        LIMIT 10
        """, nativeQuery = true)
    List<Waypoint> findWaypointsNearLocation(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMetres") double radiusMetres
    );

    // Delete all waypoints for a route — used when regenerating a route
    void deleteByRouteOptionId(Long routeOptionId);
}
