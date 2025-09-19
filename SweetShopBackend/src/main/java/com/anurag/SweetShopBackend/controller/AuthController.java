package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.dto.AuthRequest;
import com.anurag.SweetShopBackend.dto.AuthResponse;
import com.anurag.SweetShopBackend.dto.LoginRequest;
import com.anurag.SweetShopBackend.dto.ProfileResponse;
import com.anurag.SweetShopBackend.dto.UpdateProfileRequest;
import com.anurag.SweetShopBackend.model.User;
import com.anurag.SweetShopBackend.repository.UserRepository;
import com.anurag.SweetShopBackend.security.JwtTokenUtil;
import com.anurag.SweetShopBackend.security.TokenBlacklistService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody AuthRequest authRequest) {
        if (userRepository.existsByEmail(authRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already registered!");
        }

        // Create new user
        User user = new User();
        user.setFullName(authRequest.getFullName());
        user.setEmail(authRequest.getEmail());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setRole("ROLE_USER");

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Authenticate user credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Get user details
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        
        // Generate new token
        String jwt = jwtTokenUtil.generateToken(authentication);
        
        // Return the token and user info
        return ResponseEntity.ok(new AuthResponse(jwt, username, role));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                // Add token to blacklist
                jwtTokenUtil.blacklistToken(token);
                
                // Clear security context
                SecurityContextHolder.clearContext();
                
                return ResponseEntity.ok("User logged out successfully!");
            } else {
                return ResponseEntity.badRequest().body("Invalid token format");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Logout failed: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\":\"Unauthorized\",\"message\":\"No valid token provided\"}");
            }

            String token = authHeader.substring(7);
            
            // Allow refresh even if token is expired but still valid in other aspects
            String username;
            String role;
            
            try {
                // Try to extract claims even if token is expired
                username = jwtTokenUtil.getUsernameFromToken(token);
                role = jwtTokenUtil.getRoleFromToken(token);
            } catch (ExpiredJwtException e) {
                // If token is expired but signature is valid, extract claims from expired token
                username = e.getClaims().getSubject();
                role = e.getClaims().get("role", String.class);
            } catch (Exception e) {
                // For other token errors (malformed, invalid signature, etc.)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\":\"Unauthorized\",\"message\":\"Invalid token format\"}");
            }
            
            // Check if token is blacklisted
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\":\"Unauthorized\",\"message\":\"Token has been invalidated. Please login again.\"}");
            }

            // Blacklist the old token
            jwtTokenUtil.blacklistToken(token);
            
            // Load user details to verify user exists and is valid
            UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(username);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\":\"Unauthorized\",\"message\":\"User not found or inactive\"}");
            }
            
            // Generate new token
            String newToken = jwtTokenUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(newToken, username, role));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\":\"Unauthorized\",\"message\":\"Token refresh failed: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ProfileResponse profileResponse = new ProfileResponse(
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getRole()
        );
        
        return ResponseEntity.ok(profileResponse);
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateUserProfile(
            @Valid @RequestBody UpdateProfileRequest updateRequest,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update only the provided fields
        user.setFullName(updateRequest.getName());
        if (updateRequest.getPhone() != null) {
            user.setPhone(updateRequest.getPhone());
        }
        if (updateRequest.getAddress() != null) {
            user.setAddress(updateRequest.getAddress());
        }
        
        userRepository.save(user);
        
        // Return updated profile
        ProfileResponse profileResponse = new ProfileResponse(
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getRole()
        );
        
        return ResponseEntity.ok(profileResponse);
    }
}
