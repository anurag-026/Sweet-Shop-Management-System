package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.dto.CheckoutRequestDto;
import com.anurag.SweetShopBackend.dto.OrderDto;
import com.anurag.SweetShopBackend.model.Order;
import com.anurag.SweetShopBackend.model.User;
import com.anurag.SweetShopBackend.repository.UserRepository;
import com.anurag.SweetShopBackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> checkout(
            Authentication authentication,
            @RequestBody(required = false) CheckoutRequestDto checkoutRequest) {
        User user = getCurrentUser(authentication);
        OrderDto order;
        
        if (checkoutRequest != null) {
            order = orderService.checkout(user, checkoutRequest);
        } else {
            order = orderService.checkout(user);
        }
        
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getUserOrders(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<OrderDto> orders = orderService.getUserOrders(user);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(
            @PathVariable UUID orderId,
            Authentication authentication) {
        
        User user = getCurrentUser(authentication);
        OrderDto order = orderService.getOrderById(orderId, user);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam Order.OrderStatus status) {
        
        OrderDto order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(order);
    }
    
    @PutMapping("/{orderId}/tracking")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDto> updateOrderTracking(
            @PathVariable UUID orderId,
            @RequestParam String trackingNumber) {
        
        OrderDto order = orderService.updateOrderTracking(orderId, trackingNumber);
        return ResponseEntity.ok(order);
    }

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }
}
