package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.dto.CheckoutRequestDto;
import com.anurag.SweetShopBackend.dto.OrderDto;
import com.anurag.SweetShopBackend.model.Order;
import com.anurag.SweetShopBackend.model.User;
import com.anurag.SweetShopBackend.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class SimpleOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private Order testOrder;
    private OrderDto orderDto;
    private CheckoutRequestDto checkoutRequest;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(UUID.randomUUID());
        testOrder.setTotalAmount(50.00);
        testOrder.setStatus(Order.OrderStatus.PENDING);
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setPaymentMode(Order.PaymentMode.CASH_ON_DELIVERY);

        orderDto = new OrderDto();
        orderDto.setId(UUID.randomUUID());
        orderDto.setCustomerId(UUID.randomUUID());
        orderDto.setTotalAmount(50.00);
        orderDto.setStatus(Order.OrderStatus.PENDING);
        orderDto.setOrderDate(LocalDateTime.now());
        orderDto.setPaymentMode(Order.PaymentMode.CASH_ON_DELIVERY);

        checkoutRequest = new CheckoutRequestDto();
        checkoutRequest.setPaymentMode(Order.PaymentMode.CASH_ON_DELIVERY);
        checkoutRequest.setShippingAddress("123 Test St, Test City");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testCheckout_Success() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        when(orderService.checkout(any(User.class), checkoutRequest)).thenReturn(orderDto);

        mockMvc.perform(post("/api/orders/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkoutRequest))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalAmount").value(50.00));

        verify(orderService).checkout(any(User.class), checkoutRequest);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetUserOrders_Success() throws Exception {
        List<OrderDto> orders = Arrays.asList(orderDto);
        when(orderService.getUserOrders(any(User.class))).thenReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].totalAmount").value(50.00));

        verify(orderService).getUserOrders(any(User.class));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetOrderById_Success() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.getOrderById(orderId, any(User.class))).thenReturn(orderDto);

        mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(50.00));

        verify(orderService).getOrderById(orderId, any(User.class));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetOrderById_NotFound() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.getOrderById(orderId, any(User.class))).thenReturn(null);

        mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isNotFound());

        verify(orderService).getOrderById(orderId, any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateOrderStatus_Success() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.updateOrderStatus(orderId, Order.OrderStatus.CONFIRMED)).thenReturn(orderDto);

        mockMvc.perform(patch("/api/orders/" + orderId + "/status")
                .param("status", "CONFIRMED")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(50.00));

        verify(orderService).updateOrderStatus(orderId, Order.OrderStatus.CONFIRMED);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateOrderTracking_Success() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.updateOrderTracking(orderId, "TRK123")).thenReturn(orderDto);

        mockMvc.perform(patch("/api/orders/" + orderId + "/tracking")
                .param("trackingNumber", "TRK123")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(50.00));

        verify(orderService).updateOrderTracking(orderId, "TRK123");
    }
}
