package com.anamika.spintrail.entity;

import com.anamika.spintrail.constants.enums.DifficultyLevel;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    private Double startLatitude;

    @Column(nullable = false)
    private Double startLongitude;

    @Column(columnDefinition = "TEXT")
    private String routePath;

    @Column(nullable = false)
    private Double totalDistance;

    @Column
    private Integer estimatedDuration;

    @Column
    private double elevationGain;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficulty;

    @OneToMany(
            mappedBy = "routeOption",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @OrderBy("sequenceOrder ASC")
    private List<Waypoint> waypoints = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column
    private Boolean isPopular = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public RouteOption() {}
}