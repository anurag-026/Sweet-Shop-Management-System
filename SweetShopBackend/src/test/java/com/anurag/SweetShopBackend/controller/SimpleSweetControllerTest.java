package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.model.Sweet;
import com.anurag.SweetShopBackend.service.SweetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SweetController.class)
class SimpleSweetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SweetService sweetService;

    @Autowired
    private ObjectMapper objectMapper;

    private Sweet testSweet;

    @BeforeEach
    void setUp() {
        testSweet = new Sweet();
        testSweet.setId(UUID.randomUUID());
        testSweet.setName("Test Sweet");
        testSweet.setDescription("A delicious test sweet");
        testSweet.setPrice(10.50);
        testSweet.setCategory("Test Category");
        testSweet.setQuantity(100);
        testSweet.setImage("test-image.jpg");
    }

    @Test
    void testGetAllSweets_Success() throws Exception {
        List<Sweet> sweets = Arrays.asList(testSweet);
        
        when(sweetService.getAllSweets(any(), any(), any(), any())).thenReturn(sweets);

        mockMvc.perform(get("/api/sweets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Sweet"))
                .andExpect(jsonPath("$[0].price").value(10.50));

        verify(sweetService).getAllSweets(any(), any(), any(), any());
    }

    @Test
    void testGetSweetById_Success() throws Exception {
        UUID sweetId = UUID.randomUUID();
        when(sweetService.getSweetById(sweetId)).thenReturn(testSweet);

        mockMvc.perform(get("/api/sweets/" + sweetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Sweet"))
                .andExpect(jsonPath("$.price").value(10.50));

        verify(sweetService).getSweetById(sweetId);
    }

    @Test
    void testGetSweetById_NotFound() throws Exception {
        UUID sweetId = UUID.randomUUID();
        when(sweetService.getSweetById(sweetId)).thenReturn(null);

        mockMvc.perform(get("/api/sweets/" + sweetId))
                .andExpect(status().isNotFound());

        verify(sweetService).getSweetById(sweetId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateSweet_Success() throws Exception {
        when(sweetService.createSweet(any(Sweet.class))).thenReturn(testSweet);

        mockMvc.perform(post("/api/sweets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSweet))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Sweet"))
                .andExpect(jsonPath("$.price").value(10.50));

        verify(sweetService).createSweet(any(Sweet.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateSweet_Success() throws Exception {
        UUID sweetId = UUID.randomUUID();
        when(sweetService.updateSweet(eq(sweetId), any(Sweet.class))).thenReturn(testSweet);

        mockMvc.perform(put("/api/sweets/" + sweetId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSweet))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Sweet"));

        verify(sweetService).updateSweet(eq(sweetId), any(Sweet.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteSweet_Success() throws Exception {
        UUID sweetId = UUID.randomUUID();
        doNothing().when(sweetService).deleteSweet(sweetId);

        mockMvc.perform(delete("/api/sweets/" + sweetId)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(sweetService).deleteSweet(sweetId);
    }

    @Test
    void testPurchaseSweet_Success() throws Exception {
        UUID sweetId = UUID.randomUUID();
        when(sweetService.purchaseSweet(sweetId, 5)).thenReturn(testSweet);

        mockMvc.perform(post("/api/sweets/" + sweetId + "/purchase")
                .param("quantity", "5")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Sweet"));

        verify(sweetService).purchaseSweet(sweetId, 5);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRestockSweet_Success() throws Exception {
        UUID sweetId = UUID.randomUUID();
        when(sweetService.restockSweet(sweetId, 25)).thenReturn(testSweet);

        mockMvc.perform(post("/api/sweets/" + sweetId + "/restock")
                .param("quantity", "25")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Sweet"));

        verify(sweetService).restockSweet(sweetId, 25);
    }
}
