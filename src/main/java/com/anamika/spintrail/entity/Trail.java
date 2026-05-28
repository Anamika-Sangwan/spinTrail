package com.anamika.spintrail.entity;

import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Table(name = "trails")
@Setter
public class Trail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String trailName;

    private double distance;

    public Trail(){}

}
