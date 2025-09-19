package com.anurag.SweetShopBackend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(jdbcTemplate);
    }

    @Test
    void testCheckDatabase_Success() throws Exception {
        // Given
        String expectedResult = "Database connection successful";
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class))).thenReturn(expectedResult);

        // When & Then
        mockMvc.perform(get("/api/health/database"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.database").value("Connected"))
                .andExpect(jsonPath("$.message").value(expectedResult))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(jdbcTemplate).queryForObject(anyString(), eq(String.class));
    }

    @Test
    void testCheckDatabase_ConnectionFailure() throws Exception {
        // Given
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection failed"));

        // When & Then
        mockMvc.perform(get("/api/health/database"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.database").value("Disconnected"))
                .andExpect(jsonPath("$.error").value("Connection failed"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(jdbcTemplate).queryForObject(anyString(), eq(String.class));
    }

    @Test
    void testCheckDatabase_SQLException() throws Exception {
        // Given
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class)))
                .thenThrow(new org.springframework.dao.DataAccessException("SQL Error") {});

        // When & Then
        mockMvc.perform(get("/api/health/database"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.database").value("Disconnected"))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(jdbcTemplate).queryForObject(anyString(), eq(String.class));
    }

    @Test
    void testCheckDatabase_TimeoutException() throws Exception {
        // Given
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class)))
                .thenThrow(new org.springframework.dao.QueryTimeoutException("Query timeout", null));

        // When & Then
        mockMvc.perform(get("/api/health/database"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.database").value("Disconnected"))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(jdbcTemplate).queryForObject(anyString(), eq(String.class));
    }

    @Test
    void testCheckDatabase_GenericException() throws Exception {
        // Given
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class)))
                .thenThrow(new IllegalStateException("Unexpected error"));

        // When & Then
        mockMvc.perform(get("/api/health/database"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.database").value("Disconnected"))
                .andExpect(jsonPath("$.error").value("Unexpected error"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(jdbcTemplate).queryForObject(anyString(), eq(String.class));
    }

    @Test
    void testPing_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/health/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").value("Sweet Shop Backend is running"));

        // Verify no database calls are made for ping endpoint
        verify(jdbcTemplate, never()).queryForObject(anyString(), any(Class.class));
    }

    @Test
    void testPing_MultipleCalls() throws Exception {
        // When & Then - Make multiple calls to ensure consistency
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/health/ping"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("UP"))
                    .andExpect(jsonPath("$.message").value("Sweet Shop Backend is running"));
        }

        // Verify no database calls are made for ping endpoint
        verify(jdbcTemplate, never()).queryForObject(anyString(), any(Class.class));
    }

    @Test
    void testCheckDatabase_WithDifferentQueryResults() throws Exception {
        // Given
        String[] testResults = {
            "Database connection successful",
            "Connection established",
            "DB OK",
            "Connected to PostgreSQL"
        };

        for (String result : testResults) {
            when(jdbcTemplate.queryForObject(anyString(), eq(String.class))).thenReturn(result);

            // When & Then
            mockMvc.perform(get("/api/health/database"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("UP"))
                    .andExpect(jsonPath("$.database").value("Connected"))
                    .andExpect(jsonPath("$.message").value(result))
                    .andExpect(jsonPath("$.timestamp").exists());

            verify(jdbcTemplate, atLeastOnce()).queryForObject(anyString(), eq(String.class));
        }
    }

    @Test
    void testCheckDatabase_WithNullResult() throws Exception {
        // Given
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class))).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/health/database"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.database").value("Connected"))
                .andExpect(jsonPath("$.message").isEmpty())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(jdbcTemplate).queryForObject(anyString(), eq(String.class));
    }

    @Test
    void testCheckDatabase_WithEmptyResult() throws Exception {
        // Given
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class))).thenReturn("");

        // When & Then
        mockMvc.perform(get("/api/health/database"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.database").value("Connected"))
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(jdbcTemplate).queryForObject(anyString(), eq(String.class));
    }

    @Test
    void testHealthEndpoints_NoAuthenticationRequired() throws Exception {
        // Test that health endpoints don't require authentication
        mockMvc.perform(get("/api/health/database"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/health/ping"))
                .andExpect(status().isOk());
    }

    @Test
    void testHealthEndpoints_WithHeaders() throws Exception {
        // Given
        String expectedResult = "Database connection successful";
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class))).thenReturn(expectedResult);

        // When & Then - Test with various headers
        mockMvc.perform(get("/api/health/database")
                .header("Accept", "application/json")
                .header("User-Agent", "Test Agent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));

        mockMvc.perform(get("/api/health/ping")
                .header("Accept", "application/json")
                .header("User-Agent", "Test Agent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void testHealthEndpoints_WithQueryParameters() throws Exception {
        // Given
        String expectedResult = "Database connection successful";
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class))).thenReturn(expectedResult);

        // When & Then - Test with query parameters (should be ignored)
        mockMvc.perform(get("/api/health/database?param=value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));

        mockMvc.perform(get("/api/health/ping?param=value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
