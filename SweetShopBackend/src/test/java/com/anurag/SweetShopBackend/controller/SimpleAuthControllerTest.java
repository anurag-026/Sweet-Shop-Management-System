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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class SimpleAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

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

    private AuthRequest authRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;
    private ProfileResponse profileResponse;
    private UpdateProfileRequest updateProfileRequest;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
        authRequest.setFullName("Test User");
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        profileResponse = new ProfileResponse();
        profileResponse.setName("Test User");
        profileResponse.setEmail("test@example.com");
        profileResponse.setRole("ROLE_USER");

        authResponse = new AuthResponse();
        authResponse.setToken("jwt-token");
        authResponse.setEmail("test@example.com");
        authResponse.setRole("ROLE_USER");

        updateProfileRequest = new UpdateProfileRequest();
        updateProfileRequest.setName("Updated Name");
    }

    @Test
    void testRegister_Success() throws Exception {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(jwtTokenUtil.generateToken(any(org.springframework.security.core.userdetails.UserDetails.class))).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));

        verify(userRepository).existsByEmail(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testLogin_Success() throws Exception {
        User user = new User();
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtTokenUtil.generateToken(any(org.springframework.security.core.userdetails.UserDetails.class))).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetProfile_Success() throws Exception {
        User user = new User();
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testUpdateProfile_Success() throws Exception {
        User user = new User();
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(patch("/api/auth/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProfileRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));

        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
    }
}
