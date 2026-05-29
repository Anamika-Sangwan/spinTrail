package com.anamika.spintrail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LocationSuggestionDto {

    private String displayName;    // "Cubbon Park, Bangalore, Karnataka, India"
    private double latitude;
    private double longitude;
    private String type;           // "park", "suburb", "road" etc — from Nominatim
}