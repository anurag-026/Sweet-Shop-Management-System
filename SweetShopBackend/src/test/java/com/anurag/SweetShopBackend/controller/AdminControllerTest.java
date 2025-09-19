package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.model.User;
import com.anurag.SweetShopBackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    private User testAdmin;
    private User testUser;
    private List<User> allUsers;
    private Authentication adminAuthentication;
    private Authentication userAuthentication;

    @BeforeEach
    void setUp() {
        testAdmin = new User();
        testAdmin.setId(UUID.randomUUID());
        testAdmin.setFullName("Test Admin");
        testAdmin.setEmail("admin@example.com");
        testAdmin.setRole("ROLE_ADMIN");

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setFullName("Test User");
        testUser.setEmail("user@example.com");
        testUser.setRole("ROLE_USER");

        allUsers = Arrays.asList(testAdmin, testUser);

        adminAuthentication = mock(Authentication.class);
        when(adminAuthentication.getName()).thenReturn("admin@example.com");
        when(adminAuthentication.isAuthenticated()).thenReturn(true);
        @SuppressWarnings({"unchecked", "rawtypes"})
        Collection adminAuthorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(adminAuthentication.getAuthorities()).thenReturn(adminAuthorities);

        userAuthentication = mock(Authentication.class);
        when(userAuthentication.getName()).thenReturn("user@example.com");
        when(userAuthentication.isAuthenticated()).thenReturn(true);
        @SuppressWarnings({"unchecked", "rawtypes"})
        Collection userAuthorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        when(userAuthentication.getAuthorities()).thenReturn(userAuthorities);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAdminDashboard_Success() throws Exception {
        // Given
        when(userRepository.findAll()).thenReturn(allUsers);

        // When & Then
        mockMvc.perform(get("/api/admin/dashboard")
                .with(authentication(adminAuthentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(2))
                .andExpect(jsonPath("$.adminUsers").value(1))
                .andExpect(jsonPath("$.regularUsers").value(1))
                .andExpect(jsonPath("$.adminEmail").value("admin@example.com"));

        verify(userRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAdminDashboard_EmptyUsers() throws Exception {
        // Given
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/admin/dashboard")
                .with(authentication(adminAuthentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(0))
                .andExpect(jsonPath("$.adminUsers").value(0))
                .andExpect(jsonPath("$.regularUsers").value(0))
                .andExpect(jsonPath("$.adminEmail").value("admin@example.com"));

        verify(userRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAdminDashboard_MultipleAdmins() throws Exception {
        // Given
        User admin2 = new User();
        admin2.setId(UUID.randomUUID());
        admin2.setFullName("Admin 2");
        admin2.setEmail("admin2@example.com");
        admin2.setRole("ROLE_ADMIN");

        List<User> usersWithMultipleAdmins = Arrays.asList(testAdmin, admin2, testUser);
        when(userRepository.findAll()).thenReturn(usersWithMultipleAdmins);

        // When & Then
        mockMvc.perform(get("/api/admin/dashboard")
                .with(authentication(adminAuthentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(3))
                .andExpect(jsonPath("$.adminUsers").value(2))
                .andExpect(jsonPath("$.regularUsers").value(1))
                .andExpect(jsonPath("$.adminEmail").value("admin@example.com"));

        verify(userRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAdminDashboard_AccessDenied() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/dashboard")
                .with(authentication(userAuthentication)))
                .andExpect(status().isForbidden());

        verify(userRepository, never()).findAll();
    }

    @Test
    void testGetAdminDashboard_NotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isUnauthorized());

        verify(userRepository, never()).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetSystemStatus_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/system-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("healthy"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.environment").value("production"))
                .andExpect(jsonPath("$.serverTime").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetSystemStatus_AccessDenied() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/system-status")
                .with(authentication(userAuthentication)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetSystemStatus_NotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/system-status"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAdminDashboard_WithUserRoleButAdminAuthority() throws Exception {
        // Given - User with ROLE_USER but admin authority (edge case)
        Authentication mixedAuth = mock(Authentication.class);
        when(mixedAuth.getName()).thenReturn("mixed@example.com");
        when(mixedAuth.isAuthenticated()).thenReturn(true);
        @SuppressWarnings({"unchecked", "rawtypes"})
        Collection mixedAuthorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        when(mixedAuth.getAuthorities()).thenReturn(mixedAuthorities);

        when(userRepository.findAll()).thenReturn(allUsers);

        // When & Then
        mockMvc.perform(get("/api/admin/dashboard")
                .with(authentication(mixedAuth)))
                .andExpect(status().isForbidden());

        verify(userRepository, never()).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAdminDashboard_ExceptionHandling() throws Exception {
        // Given
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/admin/dashboard")
                .with(authentication(adminAuthentication)))
                .andExpect(status().isInternalServerError());

        verify(userRepository).findAll();
    }
}
