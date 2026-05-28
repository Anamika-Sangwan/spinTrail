package com.anamika.spintrail.entity;

import com.anamika.spintrail.constants.enums.DifficultyLevel;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.LineString;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "trails")
@Setter
@Getter
public class Trail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "trail_name", nullable = false)
    private String trailName;

    @Column(nullable = false)
    private double distance;

    @Column(columnDefinition = "geometry(LineString,4326)")
    private LineString path;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficulty; //EASY, MODERATE, HARD

    @Column
    private String surfaceType; //"asphalt", "gravel", "mixed"

    @Column
    private Double elevationGain; // in meters

    @Column(name = "created_at",updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "trail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Waypoint> waypoints = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    public Trail(){}

}
