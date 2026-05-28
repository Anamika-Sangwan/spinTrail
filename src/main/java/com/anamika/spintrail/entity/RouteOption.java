package com.anamika.spintrail.entity;

import com.anamika.spintrail.constants.enums.DifficultyLevel;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "route_options")
public class RouteOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String routeName;

    @Column(columnDefinition = "geometryType(Point,4326)", nullable = false)
    private Point startPoint;

    //what gets drawn on the map
    @Column(columnDefinition = "geometry(LineString,4326")
    private LineString routePath;

    @Column(nullable = false)
    private Double totalDistance;

    @Column
    private Integer estimatedDuration;

    @Column
    private double elebationGain;

    @Column(nullable = false)
    private DifficultyLevel difficulty; //EASY, MODERATE, HARD

    @OneToMany(
            mappedBy = "routeOption",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @OrderBy("sequenceOrder ASC")
    private List<Waypoint> waypoints = new ArrayList<>();

    @Column(name = "crearted_at", updatable = false)
    private LocalDateTime createdAt;

    @Column
    private Boolean isPopular = false;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }

     public RouteOption(){}
}
