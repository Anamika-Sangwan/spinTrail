package com.anamika.spintrail.controller;

import com.anamika.spintrail.dto.LocationSuggestionDto;
import com.anamika.spintrail.service.GeocodingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = "http://localhost:3000")   // React dev server
public class LocationController {

    private final GeocodingService geocodingService;

    public LocationController(GeocodingService geocodingService) {
        this.geocodingService = geocodingService;
    }

    // ─── Search Locations ──────────────────────────────────────────────────
    // Called as user types in the search bar
    // GET /api/locations/search?q=Cubbon Park Bangalore
    @GetMapping("/search")
    public ResponseEntity<List<LocationSuggestionDto>> searchLocations(
            @RequestParam("q") String query) {
        if (query == null || query.trim().length() < 3) {
            return ResponseEntity.badRequest().build();
        }
        List<LocationSuggestionDto> suggestions =
                geocodingService.searchLocations(query.trim());
        return ResponseEntity.ok(suggestions);
    }
}