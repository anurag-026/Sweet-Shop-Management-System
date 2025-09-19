package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.model.*;
import com.anurag.SweetShopBackend.repository.*;
import com.anurag.SweetShopBackend.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAnalyticsController {
    
    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/analytics/sales-overview")
    public ResponseEntity<?> getSalesOverview(@RequestParam(defaultValue = "30d") String range) {
        Map<String, Object> result = analyticsService.getSalesOverview(range);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/analytics/monthly-sales")
    public ResponseEntity<?> getMonthlySales(@RequestParam(defaultValue = "12") int months) {
        List<Map<String, Object>> result = analyticsService.getMonthlySales(months);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/analytics/top-products")
    public ResponseEntity<?> getTopProducts(@RequestParam(defaultValue = "5") int limit) {
        List<Map<String, Object>> result = analyticsService.getTopProducts(limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/analytics/sales-by-category")
    public ResponseEntity<?> getSalesByCategory(@RequestParam(defaultValue = "30d") String range) {
        List<Map<String, Object>> result = analyticsService.getSalesByCategory(range);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/analytics/customers/summary")
    public ResponseEntity<?> getCustomerSummary(@RequestParam(defaultValue = "30d") String range) {
        Map<String, Object> result = analyticsService.getCustomerSummary(range);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/analytics/customers/segments")
    public ResponseEntity<?> getCustomerSegments(@RequestParam(defaultValue = "30d") String range) {
        List<Map<String, Object>> result = analyticsService.getCustomerSegments(range);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/analytics/performance/traffic")
    public ResponseEntity<?> getTraffic(@RequestParam(defaultValue = "30d") String range) {
        Map<String, Object> result = analyticsService.getWebsiteTraffic(range);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/analytics/performance/conversion-funnel")
    public ResponseEntity<?> getConversionFunnel(@RequestParam(defaultValue = "30d") String range) {
        Map<String, Object> result = analyticsService.getConversionFunnel(range);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/analytics/shipping-metrics")
    public ResponseEntity<?> getShippingMetrics(@RequestParam(defaultValue = "30d") String range) {
        Map<String, Object> result = analyticsService.getShippingMetrics(range);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/alerts")
    public ResponseEntity<?> getAlerts(@RequestParam(defaultValue = "20") int limit) {
        List<Map<String, Object>> result = analyticsService.getAlerts(limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/inventory/status")
    public ResponseEntity<?> getInventoryStatus() {
        Map<String, Object> result = analyticsService.getInventoryStatus();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/inventory/low-stock")
    public ResponseEntity<?> getLowStock(@RequestParam(defaultValue = "10") int threshold) {
        List<Map<String, Object>> result = analyticsService.getLowStockItems(threshold);
        return ResponseEntity.ok(result);
    }
}

