package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.model.Sweet;
import com.anurag.SweetShopBackend.service.SweetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(SweetController.class)
class SweetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SweetService sweetService;

    @Autowired
    private ObjectMapper objectMapper;

    private Sweet testSweet;
    private List<Sweet> testSweets;

    @BeforeEach
    void setUp() {
        testSweet = new Sweet();
        testSweet.setId(UUID.randomUUID());
        testSweet.setName("Test Sweet");
        testSweet.setDescription("Test Description");
        testSweet.setPrice(10.50);
        testSweet.setCategory("Test Category");
        testSweet.setQuantity(100);
        testSweet.setImage("test-image.jpg");

        Sweet sweet2 = new Sweet();
        sweet2.setId(UUID.randomUUID());
        sweet2.setName("Test Sweet 2");
        sweet2.setDescription("Test Description 2");
        sweet2.setPrice(15.75);
        sweet2.setCategory("Test Category 2");
        sweet2.setQuantity(50);
        sweet2.setImage("test-image-2.jpg");

        testSweets = Arrays.asList(testSweet, sweet2);
    }

    @Test
    void testGetAllSweets_Success() throws Exception {
        // Given
        when(sweetService.getAllSweets(null, null, null, null)).thenReturn(testSweets);

        // When & Then
        mockMvc.perform(get("/api/sweets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Test Sweet"))
                .andExpect(jsonPath("$[0].price").value(10.50))
                .andExpect(jsonPath("$[1].name").value("Test Sweet 2"))
                .andExpect(jsonPath("$[1].price").value(15.75));

        verify(sweetService).getAllSweets(null, null, null, null);
    }

    @Test
    void testGetAllSweets_WithFilters() throws Exception {
        // Given
        when(sweetService.getAllSweets("Test", "Test Category", 10.0, 20.0))
                .thenReturn(Arrays.asList(testSweet));

        // When & Then
        mockMvc.perform(get("/api/sweets")
                .param("name", "Test")
                .param("category", "Test Category")
                .param("min", "10.0")
                .param("max", "20.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Sweet"));

        verify(sweetService).getAllSweets("Test", "Test Category", 10.0, 20.0);
    }

    @Test
    void testGetSweetById_Success() throws Exception {
        // Given
        UUID sweetId = testSweet.getId();
        when(sweetService.getSweetById(sweetId)).thenReturn(testSweet);

        // When & Then
        mockMvc.perform(get("/api/sweets/{id}", sweetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sweetId.toString()))
                .andExpect(jsonPath("$.name").value("Test Sweet"))
                .andExpect(jsonPath("$.price").value(10.50))
                .andExpect(jsonPath("$.stockQuantity").value(100));

        verify(sweetService).getSweetById(sweetId);
    }

    @Test
    void testGetSweetById_NotFound() throws Exception {
        // Given
        UUID sweetId = UUID.randomUUID();
        when(sweetService.getSweetById(sweetId)).thenThrow(new RuntimeException("Sweet not found"));

        // When & Then
        mockMvc.perform(get("/api/sweets/{id}", sweetId))
                .andExpect(status().isInternalServerError());

        verify(sweetService).getSweetById(sweetId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateSweet_Success() throws Exception {
        // Given
        Sweet newSweet = new Sweet();
        newSweet.setName("New Sweet");
        newSweet.setDescription("New Description");
        newSweet.setPrice(12.99);
        newSweet.setCategory("New Category");
        newSweet.setQuantity(75);
        newSweet.setImage("new-image.jpg");

        when(sweetService.createSweet(any(Sweet.class))).thenReturn(newSweet);

        // When & Then
        mockMvc.perform(post("/api/sweets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newSweet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Sweet"))
                .andExpect(jsonPath("$.price").value(12.99))
                .andExpect(jsonPath("$.stockQuantity").value(75));

        verify(sweetService).createSweet(any(Sweet.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateSweet_AccessDenied() throws Exception {
        // Given
        Sweet newSweet = new Sweet();
        newSweet.setName("New Sweet");

        // When & Then
        mockMvc.perform(post("/api/sweets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newSweet)))
                .andExpect(status().isForbidden());

        verify(sweetService, never()).createSweet(any(Sweet.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateSweet_InvalidInput() throws Exception {
        // Given
        Sweet invalidSweet = new Sweet();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/sweets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSweet)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateSweet_Success() throws Exception {
        // Given
        UUID sweetId = testSweet.getId();
        Sweet updatedSweet = new Sweet();
        updatedSweet.setName("Updated Sweet");
        updatedSweet.setPrice(15.99);
        updatedSweet.setQuantity(150);

        when(sweetService.updateSweet(eq(sweetId), any(Sweet.class))).thenReturn(updatedSweet);

        // When & Then
        mockMvc.perform(put("/api/sweets/{id}", sweetId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedSweet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Sweet"))
                .andExpect(jsonPath("$.price").value(15.99))
                .andExpect(jsonPath("$.stockQuantity").value(150));

        verify(sweetService).updateSweet(eq(sweetId), any(Sweet.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateSweet_AccessDenied() throws Exception {
        // Given
        UUID sweetId = testSweet.getId();
        Sweet updatedSweet = new Sweet();

        // When & Then
        mockMvc.perform(put("/api/sweets/{id}", sweetId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedSweet)))
                .andExpect(status().isForbidden());

        verify(sweetService, never()).updateSweet(any(), any(Sweet.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteSweet_Success() throws Exception {
        // Given
        UUID sweetId = testSweet.getId();
        doNothing().when(sweetService).deleteSweet(sweetId);

        // When & Then
        mockMvc.perform(delete("/api/sweets/{id}", sweetId)
                .with(csrf()))
                .andExpect(status().isOk());

        verify(sweetService).deleteSweet(sweetId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDeleteSweet_AccessDenied() throws Exception {
        // Given
        UUID sweetId = testSweet.getId();

        // When & Then
        mockMvc.perform(delete("/api/sweets/{id}", sweetId)
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(sweetService, never()).deleteSweet(any());
    }

    @Test
    void testPurchaseSweet_Success() throws Exception {
        // Given
        UUID sweetId = testSweet.getId();
        Integer quantity = 5;
        Sweet updatedSweet = new Sweet();
        updatedSweet.setId(sweetId);
        updatedSweet.setName("Test Sweet");
        updatedSweet.setQuantity(95); // 100 - 5

        when(sweetService.purchaseSweet(sweetId, quantity)).thenReturn(updatedSweet);

        // When & Then
        mockMvc.perform(post("/api/sweets/{id}/purchase", sweetId)
                .param("qty", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sweetId.toString()))
                .andExpect(jsonPath("$.stockQuantity").value(95));

        verify(sweetService).purchaseSweet(sweetId, quantity);
    }

    @Test
    void testPurchaseSweet_InvalidQuantity() throws Exception {
        // Given
        UUID sweetId = testSweet.getId();

        // When & Then
        mockMvc.perform(post("/api/sweets/{id}/purchase", sweetId)
                .param("qty", "0"))
                .andExpect(status().isBadRequest());

        verify(sweetService, never()).purchaseSweet(any(), anyInt());
    }

    @Test
    void testPurchaseSweet_MissingQuantity() throws Exception {
        // Given
        UUID sweetId = testSweet.getId();

        // When & Then
        mockMvc.perform(post("/api/sweets/{id}/purchase", sweetId))
                .andExpect(status().isBadRequest());

        verify(sweetService, never()).purchaseSweet(any(), anyInt());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRestockSweet_Success() throws Exception {
        // Given
        UUID sweetId = testSweet.getId();
        Integer quantity = 25;
        Sweet updatedSweet = new Sweet();
        updatedSweet.setId(sweetId);
        updatedSweet.setName("Test Sweet");
        updatedSweet.setQuantity(125); // 100 + 25

        when(sweetService.restockSweet(sweetId, quantity)).thenReturn(updatedSweet);

        // When & Then
        mockMvc.perform(post("/api/sweets/{id}/restock", sweetId)
                .with(csrf())
                .param("qty", "25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sweetId.toString()))
                .andExpect(jsonPath("$.stockQuantity").value(125));

        verify(sweetService).restockSweet(sweetId, quantity);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRestockSweet_DefaultQuantity() throws Exception {
        // Given
        UUID sweetId = testSweet.getId();
        Integer defaultQuantity = 1;
        Sweet updatedSweet = new Sweet();
        updatedSweet.setId(sweetId);
        updatedSweet.setName("Test Sweet");
        updatedSweet.setQuantity(101); // 100 + 1

        when(sweetService.restockSweet(sweetId, defaultQuantity)).thenReturn(updatedSweet);

        // When & Then
        mockMvc.perform(post("/api/sweets/{id}/restock", sweetId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sweetId.toString()))
                .andExpect(jsonPath("$.stockQuantity").value(101));

        verify(sweetService).restockSweet(sweetId, defaultQuantity);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testRestockSweet_AccessDenied() throws Exception {
        // Given
        UUID sweetId = testSweet.getId();

        // When & Then
        mockMvc.perform(post("/api/sweets/{id}/restock", sweetId)
                .with(csrf())
                .param("qty", "25"))
                .andExpect(status().isForbidden());

        verify(sweetService, never()).restockSweet(any(), anyInt());
    }
}
