package com.anamika.spintrail.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_routes")
@Getter
@Setter
public class SavedRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The route the user saved
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_option_id", nullable = false)
    private RouteOption routeOption;

    @Column(nullable = false)
    private String userLabel;             // user's custom name e.g. "Sunday morning ride"

    @Column(name = "saved_at", updatable = false)
    private LocalDateTime savedAt;

    @PrePersist
    protected void onCreate() {
        savedAt = LocalDateTime.now();
    }

    public SavedRoute() {}
}