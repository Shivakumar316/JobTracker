package com.jobtrackerapp.jobtracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("app", "AI Job Application Tracker API");
        response.put("status", "running");
        response.put("version", "1.0.0");
        response.put("message", "Welcome! Use the endpoints below to interact with the API.");
        response.put("endpoints", Map.of(
            "register", "POST /api/auth/register",
            "login",    "POST /api/auth/login",
            "applications", "GET /api/applications (requires JWT)",
            "analyze",  "POST /api/applications/{id}/analyze (requires JWT)",
            "dashboard", "GET /api/dashboard/stats (requires JWT)"
        ));
        response.put("documentation", "https://github.com/yourusername/jobtracker");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("message", "AI Job Tracker API is healthy and running!");
        return ResponseEntity.ok(response);
    }
}