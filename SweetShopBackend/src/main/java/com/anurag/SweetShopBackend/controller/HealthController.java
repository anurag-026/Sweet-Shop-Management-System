package com.anurag.SweetShopBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> checkDatabase() {
        try {
            // Test database connection
            String result = jdbcTemplate.queryForObject("SELECT 'Database connection successful' as status", String.class);
            
            return ResponseEntity.ok(Map.of(
                "status", "UP",
                "database", "Connected",
                "message", result,
                "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", "DOWN",
                "database", "Disconnected",
                "error", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "message", "Sweet Shop Backend is running"
        ));
    }
}
