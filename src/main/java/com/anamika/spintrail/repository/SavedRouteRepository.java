package com.anamika.spintrail.repository;

import com.anamika.spintrail.entity.SavedRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedRouteRepository extends JpaRepository<SavedRoute, Long> {

    // Get all saved routes, newest first
    List<SavedRoute> findAllByOrderBySavedAtDesc();

    // Check if a route is already saved
    boolean existsByRouteOptionId(Long routeOptionId);

    // Delete a saved route by its route option id
    void deleteByRouteOptionId(Long routeOptionId);
}