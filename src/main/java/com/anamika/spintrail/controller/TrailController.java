package com.anamika.spintrail.controller;

import com.anamika.spintrail.dto.RouteRequestDto;
import com.anamika.spintrail.dto.RouteResponseDto;
import com.anamika.spintrail.entity.RouteOption;
import com.anamika.spintrail.repository.RouteOptionRepository;
import com.anamika.spintrail.service.RoutingService;
import com.anamika.spintrail.service.TrailGeneratorService;
import com.anamika.spintrail.util.RouteMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trails")
@CrossOrigin(origins = "http://localhost:3000")
public class TrailController {

    private final TrailGeneratorService trailGeneratorService;
    private final RoutingService routingService;
    private final RouteOptionRepository routeOptionRepository;

    public TrailController(
            TrailGeneratorService trailGeneratorService,
            RoutingService routingService,
            RouteOptionRepository routeOptionRepository) {
        this.trailGeneratorService = trailGeneratorService;
        this.routingService = routingService;
        this.routeOptionRepository = routeOptionRepository;
    }

    // ─── Generate Routes ───────────────────────────────────────────────────
    // Core endpoint — takes a start point + desired distance,
    // returns 3-4 real road-snapped circular route options
    //
    // POST /api/trails/generate
    // Body: { "latitude": 12.9716, "longitude": 77.5946, "desiredDistanceKm": 20 }
    @PostMapping("/generate")
    public ResponseEntity<List<RouteResponseDto>> generateRoutes(
            @RequestBody RouteRequestDto request) {
        // Validate request
        if (request.getLatitude() == 0 || request.getLongitude() == 0) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getDesiredDistanceKm() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        // Step 1: Generate mathematical circular candidates
        List<RouteOption> candidates =
                trailGeneratorService.generateCandidateRoutes(request);
        // Step 2: Snap each candidate to real roads via ORS
        List<RouteOption> realRoutes =
                routingService.buildRealRoutes(candidates);
        if (realRoutes.isEmpty()) {
            // ORS failed for all candidates — area may not have cycling data
            return ResponseEntity.noContent().build();
        }
        // Step 3: Persist the generated routes
        List<RouteOption> savedRoutes =
                routeOptionRepository.saveAll(realRoutes);

        // Step 4: Map to DTOs and return
        return ResponseEntity.ok(RouteMapper.toDtoList(savedRoutes));
    }

    // ─── Get a Single Saved Route by ID ───────────────────────────────────
    // GET /api/trails/route/42
    @GetMapping("/route/{id}")
    public ResponseEntity<RouteResponseDto> getRouteById(
            @PathVariable Long id) {

        return routeOptionRepository.findById(id)
                .map(route -> ResponseEntity.ok(RouteMapper.toDto(route)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ─── Find Saved Routes Near a Location ────────────────────────────────
    // Fetches previously generated routes near coordinates
    // GET /api/trails/nearby?lat=12.9716&lng=77.5946&radius=5000
    @GetMapping("/nearby")
    public ResponseEntity<List<RouteResponseDto>> getRoutesNearLocation(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5000") double radius) {

        List<RouteOption> routes =
                routeOptionRepository.findRouteOptionsNearLocation(lat, lng, radius);

        return ResponseEntity.ok(RouteMapper.toDtoList(routes));
    }
}