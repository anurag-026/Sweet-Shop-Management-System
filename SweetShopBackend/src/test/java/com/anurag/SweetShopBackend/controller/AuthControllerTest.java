package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.BaseTestClass;
import com.anurag.SweetShopBackend.dto.AuthRequest;
import com.anurag.SweetShopBackend.dto.LoginRequest;
import com.anurag.SweetShopBackend.dto.UpdateProfileRequest;
import com.anurag.SweetShopBackend.model.User;
import com.anurag.SweetShopBackend.repository.UserRepository;
import com.anurag.SweetShopBackend.security.JwtTokenUtil;
import com.anurag.SweetShopBackend.security.TokenBlacklistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private UserDetails userDetails;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(java.util.UUID.randomUUID());
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole("ROLE_USER");
        testUser.setPhone("1234567890");
        testUser.setAddress("Test Address");

        userDetails = new org.springframework.security.core.userdetails.User(
            "test@example.com",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        // Given
        AuthRequest authRequest = new AuthRequest("New User", "newuser@example.com", "password123");
        
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));

        verify(userRepository).existsByEmail("newuser@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() throws Exception {
        // Given
        AuthRequest authRequest = new AuthRequest("New User", "test@example.com", "password123");
        
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Email is already registered!"));

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_InvalidInput() throws Exception {
        // Given
        AuthRequest authRequest = new AuthRequest("", "", "");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginUser_Success() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenUtil.generateToken(authentication)).thenReturn("jwt-token");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenUtil).generateToken(authentication);
    }

    @Test
    void testLoginUser_InvalidCredentials() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("test@example.com", "wrongpassword");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.core.AuthenticationException("Invalid credentials") {});

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLogoutUser_Success() throws Exception {
        // Given
        String token = "jwt-token";
        String authHeader = "Bearer " + token;
        
        doNothing().when(jwtTokenUtil).blacklistToken(token);

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(content().string("User logged out successfully!"));

        verify(jwtTokenUtil).blacklistToken(token);
    }

    @Test
    void testLogoutUser_InvalidTokenFormat() throws Exception {
        // Given
        String authHeader = "InvalidToken";

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", authHeader))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid token format"));
    }

    @Test
    void testLogoutUser_NoToken() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid token format"));
    }

    @Test
    void testRefreshToken_Success() throws Exception {
        // Given
        String token = "jwt-token";
        String authHeader = "Bearer " + token;
        String newToken = "new-jwt-token";
        
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn("test@example.com");
        when(jwtTokenUtil.getRoleFromToken(token)).thenReturn("ROLE_USER");
        when(tokenBlacklistService.isTokenBlacklisted(token)).thenReturn(false);
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn(newToken);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(newToken))
                .andExpect(jsonPath("$.username").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));

        verify(jwtTokenUtil).blacklistToken(token);
        verify(jwtTokenUtil).generateToken(userDetails);
    }

    @Test
    void testRefreshToken_ExpiredToken() throws Exception {
        // Given
        String token = "expired-jwt-token";
        String authHeader = "Bearer " + token;
        String newToken = "new-jwt-token";
        
        io.jsonwebtoken.ExpiredJwtException expiredException = new io.jsonwebtoken.ExpiredJwtException(
            null, null, "Token expired"
        );
        expiredException.getClaims().put("sub", "test@example.com");
        expiredException.getClaims().put("role", "ROLE_USER");
        
        when(jwtTokenUtil.getUsernameFromToken(token)).thenThrow(expiredException);
        when(tokenBlacklistService.isTokenBlacklisted(token)).thenReturn(false);
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn(newToken);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(newToken))
                .andExpect(jsonPath("$.username").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    void testRefreshToken_BlacklistedToken() throws Exception {
        // Given
        String token = "blacklisted-jwt-token";
        String authHeader = "Bearer " + token;
        
        when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn("test@example.com");
        when(jwtTokenUtil.getRoleFromToken(token)).thenReturn("ROLE_USER");
        when(tokenBlacklistService.isTokenBlacklisted(token)).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", authHeader))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Token has been invalidated. Please login again."));
    }

    @Test
    void testRefreshToken_InvalidToken() throws Exception {
        // Given
        String authHeader = "InvalidToken";

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", authHeader))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("No valid token provided"));
    }

    @Test
    void testGetUserProfile_Success() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/auth/profile")
                .principal(() -> "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andExpect(jsonPath("$.address").value("Test Address"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    void testGetUserProfile_UserNotFound() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/auth/profile")
                .principal(() -> "test@example.com"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetUserProfile_NotAuthenticated() throws Exception {
        // Given
        SecurityContextHolder.clearContext();

        // When & Then
        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User not authenticated"));
    }

    @Test
    void testUpdateUserProfile_Success() throws Exception {
        // Given
        UpdateProfileRequest updateRequest = new UpdateProfileRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setPhone("9876543210");
        updateRequest.setAddress("Updated Address");
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(patch("/api/auth/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .principal(() -> "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Name"))
                .andExpect(jsonPath("$.phone").value("9876543210"))
                .andExpect(jsonPath("$.address").value("Updated Address"));

        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUserProfile_PartialUpdate() throws Exception {
        // Given
        UpdateProfileRequest updateRequest = new UpdateProfileRequest();
        updateRequest.setName("Updated Name");
        // phone and address are null
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(patch("/api/auth/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .principal(() -> "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Name"))
                .andExpect(jsonPath("$.phone").value("1234567890")) // unchanged
                .andExpect(jsonPath("$.address").value("Test Address")); // unchanged
    }

    @Test
    void testUpdateUserProfile_NotAuthenticated() throws Exception {
        // Given
        UpdateProfileRequest updateRequest = new UpdateProfileRequest();
        updateRequest.setName("Updated Name");
        
        SecurityContextHolder.clearContext();

        // When & Then
        mockMvc.perform(patch("/api/auth/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User not authenticated"));
    }
}
