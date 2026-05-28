package com.anamika.spintrail.repository;

import com.anamika.spintrail.entity.Trail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrailRepository extends JpaRepository<Trail,Long> {

    // ─── Core Spatial Query ───────────────────────────────────────────────
    // Find all trails whose startPoint is within X metres of a given lat/lng
    // ST_DWithin on ::geography uses metres (not degrees) — always use this
    @Query(value = """
        SELECT * FROM trails
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
    List<Trail> findTrailsNearLocation(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMetres") double radiusMetres
    );

    // ─── Find trails by distance range ────────────────────────────────────
    // Used when user selects "I want a 20-30km route"
    @Query(value = """
        SELECT * FROM trails
        WHERE ST_DWithin(
            start_point::geography,
            ST_MakePoint(:lng, :lat)::geography,
            :radiusMetres
        )
        AND distance BETWEEN :minKm AND :maxKm
        ORDER BY distance ASC
        """, nativeQuery = true)
    List<Trail> findTrailsNearLocationWithinDistanceRange(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMetres") double radiusMetres,
            @Param("minKm") double minKm,
            @Param("maxKm") double maxKm
    );

    // ─── Find trails by difficulty ─────────────────────────────────────────
    @Query(value = """
        SELECT * FROM trails
        WHERE ST_DWithin(
            start_point::geography,
            ST_MakePoint(:lng, :lat)::geography,
            :radiusMetres
        )
        AND difficulty = :difficulty
        ORDER BY ST_Distance(
            start_point::geography,
            ST_MakePoint(:lng, :lat)::geography
        ) ASC
        """, nativeQuery = true)
    List<Trail> findTrailsNearLocationByDifficulty(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMetres") double radiusMetres,
            @Param("difficulty") String difficulty
    );

    // ─── Check if a trail path intersects a bounding box ──────────────────
    // Useful later for filtering trails visible in the current map viewport
    @Query(value = """
        SELECT * FROM trails
        WHERE ST_Intersects(
            path,
            ST_MakeEnvelope(:minLng, :minLat, :maxLng, :maxLat, 4326)
        )
        """, nativeQuery = true)
    List<Trail> findTrailsWithinBoundingBox(
            @Param("minLat") double minLat,
            @Param("minLng") double minLng,
            @Param("maxLat") double maxLat,
            @Param("maxLng") double maxLng
    );
}
