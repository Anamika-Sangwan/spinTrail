package com.anamika.spintrail.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "waypoints")
@Getter
@Setter
public class Waypoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "geometry(Point,4326)", nullable = false)
    private Point location;

    @Column(nullable = false)
    private Integer sequenceOrder; // Order of the waypoint in the trail

    @Column
    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_option_id", nullable = false)
    private RouteOption routeOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trail_id")
    private Trail trail;

     public Waypoint(){}
}
