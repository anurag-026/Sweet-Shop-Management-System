package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.model.Order;
import com.anurag.SweetShopBackend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrdersController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/orders")
    public ResponseEntity<?> listOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        // Get all orders from database
        List<Order> allOrders = orderRepository.findAll().stream()
            .sorted((a, b) -> b.getOrderDate().compareTo(a.getOrderDate()))
            .collect(Collectors.toList());
        
        // Filter by status if provided
        if (status != null && !status.isEmpty()) {
            allOrders = allOrders.stream()
                .filter(order -> order.getStatus().toString().equalsIgnoreCase(status))
                .collect(Collectors.toList());
        }
        
        // Convert to response format
        List<Map<String, Object>> orders = allOrders.stream()
            .map(order -> {
                Map<String, Object> orderData = new HashMap<>();
                orderData.put("id", order.getId().toString());
                orderData.put("username", order.getUser().getEmail());
                orderData.put("totalAmount", order.getTotalAmount());
                orderData.put("status", order.getStatus().toString());
                orderData.put("orderDate", order.getOrderDate().toString());
                orderData.put("customerName", order.getUser().getFullName());
                orderData.put("items", order.getOrderItems() != null ? order.getOrderItems().size() : 0);
                orderData.put("paymentMethod", order.getPaymentMode() != null ? order.getPaymentMode().toString() : "Unknown");
                return orderData;
            })
            .collect(Collectors.toList());
        
        // Simple pagination
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
        // Get recent orders from database, ordered by order date descending
        List<Order> orders = orderRepository.findAll().stream()
            .sorted((a, b) -> b.getOrderDate().compareTo(a.getOrderDate()))
            .limit(limit)
            .collect(Collectors.toList());
        
        List<Map<String, Object>> recentOrders = orders.stream()
            .map(order -> {
                Map<String, Object> orderData = new HashMap<>();
                orderData.put("id", order.getId().toString());
                orderData.put("customer", order.getUser().getFullName());
                orderData.put("email", order.getUser().getEmail());
                orderData.put("total", order.getTotalAmount());
                orderData.put("status", order.getStatus());
                orderData.put("date", order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                orderData.put("items", order.getOrderItems() != null ? order.getOrderItems().size() : 0);
                orderData.put("paymentMethod", order.getPaymentMode() != null ? order.getPaymentMode().toString() : "Unknown");
                return orderData;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(recentOrders);
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable String orderId,
            @RequestBody Map<String, String> statusUpdate) {
        
        try {
            UUID orderUuid = UUID.fromString(orderId);
            Optional<Order> orderOpt = orderRepository.findById(orderUuid);
            
            if (orderOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"Order not found\"}");
            }
            
            Order order = orderOpt.get();
            String newStatus = statusUpdate.get("status");
            
            if (newStatus == null || newStatus.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("{\"error\":\"Status is required\"}");
            }
            
            // Validate status
            try {
                Order.OrderStatus status = Order.OrderStatus.valueOf(newStatus.toUpperCase());
                order.setStatus(status);
                order.setLastUpdated(java.time.LocalDateTime.now());
                
                // Set delivery dates based on status
                if (status == Order.OrderStatus.SHIPPED) {
                    order.setEstimatedDeliveryDate(java.time.LocalDateTime.now().plusDays(3));
                } else if (status == Order.OrderStatus.DELIVERED) {
                    order.setActualDeliveryDate(java.time.LocalDateTime.now());
                }
                
                orderRepository.save(order);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Order status updated successfully");
                response.put("orderId", orderId);
                response.put("newStatus", newStatus);
                response.put("updatedAt", order.getLastUpdated());
                
                return ResponseEntity.ok(response);
                
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                    .body("{\"error\":\"Invalid status. Valid statuses are: PENDING, CONFIRMED, PROCESSING, SHIPPED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED, REFUNDED\"}");
            }
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body("{\"error\":\"Invalid order ID format\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"Failed to update order status: " + e.getMessage() + "\"}");
        }
    }
}

