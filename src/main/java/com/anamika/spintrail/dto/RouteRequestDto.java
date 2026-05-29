package com.anamika.spintrail.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteRequestDto {
    private double latitude;           // user's start lat
    private double longitude;          // user's start lng
    private double desiredDistanceKm;  // how far they want to ride

    private String difficulty;         // "EASY", "MODERATE", "HARD"
    private Double minDistanceKm;
    private Double maxDistanceKm;
}
