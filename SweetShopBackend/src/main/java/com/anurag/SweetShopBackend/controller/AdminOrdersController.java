package com.anurag.SweetShopBackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrdersController {

    @GetMapping("/orders")
    public ResponseEntity<?> listOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        List<Map<String, Object>> orders = new ArrayList<>();
        
        // Sample orders data
        String[] orderIds = {
            "ORD-2024-001", "ORD-2024-002", "ORD-2024-003", 
            "ORD-2024-004", "ORD-2024-005"
        };
        String[] usernames = {
            "sarah.j@email.com", "m.chen@email.com", "emily.r@email.com",
            "d.wilson@email.com", "lisa.a@email.com"
        };
        double[] totals = {89.5, 156.75, 67.25, 234.0, 45.5};
        String[] statuses = {"DELIVERED", "PROCESSING", "SHIPPED", "DELIVERED", "PENDING"};
        String[] dates = {
            "2024-01-28T12:30:00Z", 
            "2024-01-28T09:15:00Z", 
            "2024-01-27T16:45:00Z",
            "2024-01-27T10:20:00Z",
            "2024-01-26T14:10:00Z"
        };
        
        // Filter by status if provided
        for (int i = 0; i < orderIds.length; i++) {
            if (status == null || status.isEmpty() || statuses[i].equalsIgnoreCase(status)) {
                Map<String, Object> order = new HashMap<>();
                order.put("id", orderIds[i]);
                order.put("username", usernames[i]);
                order.put("totalAmount", totals[i]);
                order.put("status", statuses[i]);
                order.put("orderDate", dates[i]);
                orders.add(order);
            }
        }
        
        // Simple pagination (in a real app, this would be done at the database level)
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, orders.size());
        
        if (startIndex < orders.size()) {
            return ResponseEntity.ok(orders.subList(startIndex, endIndex));
        } else {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/orders/recent")
    public ResponseEntity<?> recentOrders(@RequestParam(defaultValue = "20") int limit) {
        List<Map<String, Object>> recentOrders = new ArrayList<>();
        
        // Sample recent orders
        String[] orderIds = {
            "ORD-2024-001", "ORD-2024-002", "ORD-2024-003", 
            "ORD-2024-004", "ORD-2024-005"
        };
        String[] customers = {
            "Sarah Johnson", "Michael Chen", "Emily Rodriguez",
            "David Wilson", "Lisa Anderson"
        };
        String[] emails = {
            "sarah.j@email.com", "m.chen@email.com", "emily.r@email.com",
            "d.wilson@email.com", "lisa.a@email.com"
        };
        double[] totals = {89.5, 156.75, 67.25, 234.0, 45.5};
        String[] statuses = {"DELIVERED", "PROCESSING", "SHIPPED", "DELIVERED", "PENDING"};
        String[] dates = {"2024-01-28", "2024-01-28", "2024-01-27", "2024-01-27", "2024-01-26"};
        int[] itemCounts = {3, 5, 2, 7, 1};
        String[] paymentMethods = {"Credit Card", "PayPal", "Credit Card", "Bank Transfer", "Credit Card"};
        
        int count = Math.min(limit, orderIds.length);
        
        for (int i = 0; i < count; i++) {
            Map<String, Object> order = new HashMap<>();
            order.put("id", orderIds[i]);
            order.put("customer", customers[i]);
            order.put("email", emails[i]);
            order.put("total", totals[i]);
            order.put("status", statuses[i]);
            order.put("date", dates[i]);
            order.put("items", itemCounts[i]);
            order.put("paymentMethod", paymentMethods[i]);
            recentOrders.add(order);
        }
        
        return ResponseEntity.ok(recentOrders);
    }
}

