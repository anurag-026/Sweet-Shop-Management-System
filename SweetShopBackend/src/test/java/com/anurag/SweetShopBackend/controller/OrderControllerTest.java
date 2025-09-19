package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.dto.CheckoutRequestDto;
import com.anurag.SweetShopBackend.dto.OrderDto;
import com.anurag.SweetShopBackend.model.Order;
import com.anurag.SweetShopBackend.model.User;
import com.anurag.SweetShopBackend.repository.UserRepository;
import com.anurag.SweetShopBackend.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private OrderDto testOrder;
    private List<OrderDto> testOrders;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole("ROLE_USER");

        testOrder = new OrderDto();
        testOrder.setId(UUID.randomUUID());
        testOrder.setCustomerId(testUser.getId());
        testOrder.setTotalAmount(50.00);
        testOrder.setStatus(Order.OrderStatus.PENDING);
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setPaymentMode(Order.PaymentMode.CREDIT_CARD);
        testOrder.setShippingAddress("Test Address");

        OrderDto order2 = new OrderDto();
        order2.setId(UUID.randomUUID());
        order2.setCustomerId(testUser.getId());
        order2.setTotalAmount(75.50);
        order2.setStatus(Order.OrderStatus.CONFIRMED);
        order2.setOrderDate(LocalDateTime.now().minusDays(1));
        order2.setPaymentMode(Order.PaymentMode.BANK_TRANSFER);
        order2.setShippingAddress("Test Address 2");

        testOrders = Arrays.asList(testOrder, order2);

        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@example.com");
        when(authentication.isAuthenticated()).thenReturn(true);
    }

    @Test
    void testCheckout_Success() throws Exception {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(orderService.checkout(testUser)).thenReturn(testOrder);

        // When & Then
        mockMvc.perform(post("/api/orders/checkout")
                .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testOrder.getId().toString()))
                .andExpect(jsonPath("$.totalAmount").value(50.00))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.paymentMode").value("CREDIT_CARD"));

        verify(orderService).checkout(testUser);
    }

    @Test
    void testCheckout_WithRequest() throws Exception {
        // Given
        CheckoutRequestDto checkoutRequest = new CheckoutRequestDto();
        checkoutRequest.setPaymentMode(Order.PaymentMode.CASH_ON_DELIVERY);
        checkoutRequest.setShippingAddress("Custom Address");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(orderService.checkout(eq(testUser), any(CheckoutRequestDto.class))).thenReturn(testOrder);

        // When & Then
        mockMvc.perform(post("/api/orders/checkout")
                .with(authentication(authentication))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkoutRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testOrder.getId().toString()))
                .andExpect(jsonPath("$.totalAmount").value(50.00));

        verify(orderService).checkout(eq(testUser), any(CheckoutRequestDto.class));
    }

    @Test
    void testCheckout_UserNotFound() throws Exception {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/orders/checkout")
                .with(authentication(authentication)))
                .andExpect(status().isInternalServerError());

        verify(orderService, never()).checkout(any());
    }

    @Test
    void testGetUserOrders_Success() throws Exception {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(orderService.getUserOrders(testUser)).thenReturn(testOrders);

        // When & Then
        mockMvc.perform(get("/api/orders")
                .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(testOrder.getId().toString()))
                .andExpect(jsonPath("$[0].totalAmount").value(50.00))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[1].id").value(testOrders.get(1).getId().toString()))
                .andExpect(jsonPath("$[1].totalAmount").value(75.50))
                .andExpect(jsonPath("$[1].status").value("CONFIRMED"));

        verify(orderService).getUserOrders(testUser);
    }

    @Test
    void testGetUserOrders_EmptyList() throws Exception {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(orderService.getUserOrders(testUser)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/orders")
                .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(orderService).getUserOrders(testUser);
    }

    @Test
    void testGetUserOrders_UserNotFound() throws Exception {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/orders")
                .with(authentication(authentication)))
                .andExpect(status().isInternalServerError());

        verify(orderService, never()).getUserOrders(any());
    }

    @Test
    void testGetOrderById_Success() throws Exception {
        // Given
        UUID orderId = testOrder.getId();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(orderService.getOrderById(orderId, testUser)).thenReturn(testOrder);

        // When & Then
        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.totalAmount").value(50.00))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService).getOrderById(orderId, testUser);
    }

    @Test
    void testGetOrderById_UserNotFound() throws Exception {
        // Given
        UUID orderId = testOrder.getId();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                .with(authentication(authentication)))
                .andExpect(status().isInternalServerError());

        verify(orderService, never()).getOrderById(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateOrderStatus_Success() throws Exception {
        // Given
        UUID orderId = testOrder.getId();
        Order.OrderStatus newStatus = Order.OrderStatus.CONFIRMED;
        OrderDto updatedOrder = new OrderDto();
        updatedOrder.setId(orderId);
        updatedOrder.setStatus(newStatus);

        when(orderService.updateOrderStatus(orderId, newStatus)).thenReturn(updatedOrder);

        // When & Then
        mockMvc.perform(put("/api/orders/{orderId}/status", orderId)
                .with(csrf())
                .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(orderService).updateOrderStatus(orderId, newStatus);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateOrderStatus_AccessDenied() throws Exception {
        // Given
        UUID orderId = testOrder.getId();

        // When & Then
        mockMvc.perform(put("/api/orders/{orderId}/status", orderId)
                .with(csrf())
                .param("status", "CONFIRMED"))
                .andExpect(status().isForbidden());

        verify(orderService, never()).updateOrderStatus(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateOrderTracking_Success() throws Exception {
        // Given
        UUID orderId = testOrder.getId();
        String trackingNumber = "TRK123456789";
        OrderDto updatedOrder = new OrderDto();
        updatedOrder.setId(orderId);
        updatedOrder.setTrackingNumber(trackingNumber);

        when(orderService.updateOrderTracking(orderId, trackingNumber)).thenReturn(updatedOrder);

        // When & Then
        mockMvc.perform(put("/api/orders/{orderId}/tracking", orderId)
                .with(csrf())
                .param("trackingNumber", trackingNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.trackingNumber").value(trackingNumber));

        verify(orderService).updateOrderTracking(orderId, trackingNumber);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateOrderTracking_AccessDenied() throws Exception {
        // Given
        UUID orderId = testOrder.getId();
        String trackingNumber = "TRK123456789";

        // When & Then
        mockMvc.perform(put("/api/orders/{orderId}/tracking", orderId)
                .with(csrf())
                .param("trackingNumber", trackingNumber))
                .andExpect(status().isForbidden());

        verify(orderService, never()).updateOrderTracking(any(), anyString());
    }

    @Test
    void testCheckout_NotAuthenticated() throws Exception {
        // Given
        Authentication unauthenticated = mock(Authentication.class);
        when(unauthenticated.isAuthenticated()).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/orders/checkout")
                .with(authentication(unauthenticated)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserOrders_NotAuthenticated() throws Exception {
        // Given
        Authentication unauthenticated = mock(Authentication.class);
        when(unauthenticated.isAuthenticated()).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/orders")
                .with(authentication(unauthenticated)))
                .andExpect(status().isUnauthorized());
    }
}
