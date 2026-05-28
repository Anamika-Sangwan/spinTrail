package com.anamika.spintrail.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trails")
public class TrailController {

    @GetMapping("")
    public ResponseEntity<String> getTrail(){
        return ResponseEntity.ok("Trail project is working!");
    }
}
