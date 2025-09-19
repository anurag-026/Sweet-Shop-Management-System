package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.model.Order;
import com.anurag.SweetShopBackend.model.User;
import com.anurag.SweetShopBackend.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(AdminOrdersController.class)
class AdminOrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Order testOrder;
    private List<Order> testOrders;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole("ROLE_USER");

        testOrder = new Order();
        testOrder.setId(UUID.randomUUID());
        testOrder.setUser(testUser);
        testOrder.setTotalAmount(50.00);
        testOrder.setStatus(Order.OrderStatus.PENDING);
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setPaymentMode(Order.PaymentMode.CREDIT_CARD);
        testOrder.setShippingAddress("Test Address");
        testOrder.setOrderItems(new ArrayList<>());

        Order order2 = new Order();
        order2.setId(UUID.randomUUID());
        order2.setUser(testUser);
        order2.setTotalAmount(75.50);
        order2.setStatus(Order.OrderStatus.CONFIRMED);
        order2.setOrderDate(LocalDateTime.now().minusDays(1));
        order2.setPaymentMode(Order.PaymentMode.BANK_TRANSFER);
        order2.setShippingAddress("Test Address 2");
        order2.setOrderItems(new ArrayList<>());

        testOrders = Arrays.asList(testOrder, order2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListOrders_Success() throws Exception {
        // Given
        when(orderRepository.findAll()).thenReturn(testOrders);

        // When & Then
        mockMvc.perform(get("/api/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(testOrder.getId().toString()))
                .andExpect(jsonPath("$[0].username").value("test@example.com"))
                .andExpect(jsonPath("$[0].totalAmount").value(50.00))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].customerName").value("Test User"))
                .andExpect(jsonPath("$[0].items").value(0))
                .andExpect(jsonPath("$[0].paymentMethod").value("CREDIT_CARD"));

        verify(orderRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListOrders_WithStatusFilter() throws Exception {
        // Given
        when(orderRepository.findAll()).thenReturn(testOrders);

        // When & Then
        mockMvc.perform(get("/api/admin/orders")
                .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(orderRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListOrders_WithPagination() throws Exception {
        // Given
        when(orderRepository.findAll()).thenReturn(testOrders);

        // When & Then
        mockMvc.perform(get("/api/admin/orders")
                .param("page", "0")
                .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(orderRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListOrders_EmptyList() throws Exception {
        // Given
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(orderRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testListOrders_AccessDenied() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/orders"))
                .andExpect(status().isForbidden());

        verify(orderRepository, never()).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRecentOrders_Success() throws Exception {
        // Given
        when(orderRepository.findAll()).thenReturn(testOrders);

        // When & Then
        mockMvc.perform(get("/api/admin/orders/recent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(testOrder.getId().toString()))
                .andExpect(jsonPath("$[0].customer").value("Test User"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"))
                .andExpect(jsonPath("$[0].total").value(50.00))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].items").value(0))
                .andExpect(jsonPath("$[0].paymentMethod").value("CREDIT_CARD"));

        verify(orderRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRecentOrders_WithCustomLimit() throws Exception {
        // Given
        when(orderRepository.findAll()).thenReturn(testOrders);

        // When & Then
        mockMvc.perform(get("/api/admin/orders/recent")
                .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(orderRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testRecentOrders_AccessDenied() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/orders/recent"))
                .andExpect(status().isForbidden());

        verify(orderRepository, never()).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateOrderStatus_Success() throws Exception {
        // Given
        String orderId = testOrder.getId().toString();
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "CONFIRMED");

        when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When & Then
        mockMvc.perform(put("/api/admin/orders/{orderId}/status", orderId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order status updated successfully"))
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.newStatus").value("CONFIRMED"))
                .andExpect(jsonPath("$.updatedAt").exists());

        verify(orderRepository).findById(testOrder.getId());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateOrderStatus_OrderNotFound() throws Exception {
        // Given
        String orderId = UUID.randomUUID().toString();
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "CONFIRMED");

        when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/admin/orders/{orderId}/status", orderId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Order not found"));

        verify(orderRepository).findById(any(UUID.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateOrderStatus_InvalidStatus() throws Exception {
        // Given
        String orderId = testOrder.getId().toString();
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "INVALID_STATUS");

        when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        // When & Then
        mockMvc.perform(put("/api/admin/orders/{orderId}/status", orderId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid status. Valid statuses are: PENDING, CONFIRMED, PROCESSING, SHIPPED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED, REFUNDED"));

        verify(orderRepository).findById(testOrder.getId());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateOrderStatus_MissingStatus() throws Exception {
        // Given
        String orderId = testOrder.getId().toString();
        Map<String, String> statusUpdate = new HashMap<>();
        // Missing status field

        when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        // When & Then
        mockMvc.perform(put("/api/admin/orders/{orderId}/status", orderId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Status is required"));

        verify(orderRepository).findById(testOrder.getId());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateOrderStatus_InvalidOrderId() throws Exception {
        // Given
        String orderId = "invalid-uuid";
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "CONFIRMED");

        // When & Then
        mockMvc.perform(put("/api/admin/orders/{orderId}/status", orderId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid order ID format"));

        verify(orderRepository, never()).findById(any());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateOrderStatus_ShippedStatus() throws Exception {
        // Given
        String orderId = testOrder.getId().toString();
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "SHIPPED");

        when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When & Then
        mockMvc.perform(put("/api/admin/orders/{orderId}/status", orderId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order status updated successfully"))
                .andExpect(jsonPath("$.newStatus").value("SHIPPED"));

        verify(orderRepository).findById(testOrder.getId());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateOrderStatus_DeliveredStatus() throws Exception {
        // Given
        String orderId = testOrder.getId().toString();
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "DELIVERED");

        when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When & Then
        mockMvc.perform(put("/api/admin/orders/{orderId}/status", orderId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order status updated successfully"))
                .andExpect(jsonPath("$.newStatus").value("DELIVERED"));

        verify(orderRepository).findById(testOrder.getId());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateOrderStatus_AccessDenied() throws Exception {
        // Given
        String orderId = testOrder.getId().toString();
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "CONFIRMED");

        // When & Then
        mockMvc.perform(put("/api/admin/orders/{orderId}/status", orderId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isForbidden());

        verify(orderRepository, never()).findById(any());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateOrderStatus_ExceptionHandling() throws Exception {
        // Given
        String orderId = testOrder.getId().toString();
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "CONFIRMED");

        when(orderRepository.findById(testOrder.getId())).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(put("/api/admin/orders/{orderId}/status", orderId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to update order status: Database error"));

        verify(orderRepository).findById(testOrder.getId());
        verify(orderRepository, never()).save(any(Order.class));
    }
}
