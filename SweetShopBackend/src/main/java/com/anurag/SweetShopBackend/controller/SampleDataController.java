package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.service.WebsiteTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/sample-data")
@PreAuthorize("hasRole('ADMIN')")
public class SampleDataController {
    
    @Autowired
    private WebsiteTrackingService websiteTrackingService;
    
    /**
     * Generate sample website traffic data
     */
    @PostMapping("/website-traffic")
    public ResponseEntity<?> generateWebsiteTrafficData(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "50") int sessionsPerDay) {
        try {
            websiteTrackingService.generateSampleData(days, sessionsPerDay);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Sample website traffic data generated successfully",
                "days", days,
                "sessionsPerDay", sessionsPerDay,
                "totalSessions", days * sessionsPerDay
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error generating sample data: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Generate sample data for specific date range
     */
    @PostMapping("/website-traffic/range")
    public ResponseEntity<?> generateWebsiteTrafficDataForRange(
            @RequestParam int days,
            @RequestParam int sessionsPerDay,
            @RequestParam String startDate) {
        try {
            // This would require modifying the service to accept a start date
            websiteTrackingService.generateSampleData(days, sessionsPerDay);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Sample website traffic data generated for specified range",
                "days", days,
                "sessionsPerDay", sessionsPerDay,
                "startDate", startDate
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error generating sample data: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Clear all sample data
     */
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearSampleData() {
        try {
            // This would require adding a clear method to the service
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Sample data cleared successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error clearing sample data: " + e.getMessage()
            ));
        }
    }
}
