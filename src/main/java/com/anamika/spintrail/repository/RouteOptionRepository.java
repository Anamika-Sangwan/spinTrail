package com.anamika.spintrail.repository;

import com.anamika.spintrail.entity.RouteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RouteOptionRepository extends JpaRepository<RouteOption, Long> {
    // Find all routes that start near a given point
    // This powers "show me routes near me" on the frontend
    @Query(value = """
        SELECT * FROM route_options
        WHERE ST_DWithin(
            start_point::geography,
            ST_MakePoint(:lng, :lat)::geography,
            :radiusMetres
        )
        ORDER BY ST_Distance(
            start_point::geography,
            ST_MakePoint(:lng, :lat)::geography
        ) ASC
        """, nativeQuery = true)
    List<RouteOption> findRouteOptionsNearLocation(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMetres") double radiusMetres
    );

    // Find routes within a distance range — for the user's desired ride length
    @Query(value = """
        SELECT * FROM route_options
        WHERE ST_DWithin(
            start_point::geography,
            ST_MakePoint(:lng, :lat)::geography,
            :radiusMetres
        )
        AND total_distance BETWEEN :minKm AND :maxKm
        ORDER BY total_distance ASC
        LIMIT :limitCount
        """, nativeQuery = true)
    List<RouteOption> findRouteOptionsNearLocationByDistance(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMetres") double radiusMetres,
            @Param("minKm") double minKm,
            @Param("maxKm") double maxKm,
            @Param("limitCount") int limitCount       // pass 5 to get top 5 options
    );

    // Find popular routes near a location
    List<RouteOption> findByIsPopularTrue();

    // Find by difficulty near a location
    @Query(value = """
        SELECT * FROM route_options
        WHERE ST_DWithin(
            start_point::geography,
            ST_MakePoint(:lng, :lat)::geography,
            :radiusMetres
        )
        AND difficulty = :difficulty
        """, nativeQuery = true)
    List<RouteOption> findByDifficultyNearLocation(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMetres") double radiusMetres,
            @Param("difficulty") String difficulty
    );
}
