package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.exception.AdminAccessDeniedException;
import com.anurag.SweetShopBackend.model.User;
import com.anurag.SweetShopBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")  // Class-level security
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Admin dashboard endpoint - only accessible by users with ROLE_ADMIN
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getAdminDashboard(Authentication authentication) {
        // Double-check role for extra security
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AdminAccessDeniedException("Only admins can access the dashboard");
        }
        
        // Get system statistics
        List<User> users = userRepository.findAll();
        int totalUsers = users.size();
        int adminUsers = (int) users.stream()
                .filter(user -> "ROLE_ADMIN".equals(user.getRole()))
                .count();
        int regularUsers = totalUsers - adminUsers;
        
        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("totalUsers", totalUsers);
        dashboardData.put("adminUsers", adminUsers);
        dashboardData.put("regularUsers", regularUsers);
        dashboardData.put("adminEmail", authentication.getName());
        
        return ResponseEntity.ok(dashboardData);
    }

    /**
     * System status endpoint - only accessible by users with ROLE_ADMIN
     */
    @GetMapping("/system-status")
    public ResponseEntity<?> getSystemStatus() {
        Map<String, Object> systemStatus = new HashMap<>();
        systemStatus.put("status", "healthy");
        systemStatus.put("version", "1.0.0");
        systemStatus.put("environment", "production");
        systemStatus.put("serverTime", System.currentTimeMillis());
        
        return ResponseEntity.ok(systemStatus);
    }
}
