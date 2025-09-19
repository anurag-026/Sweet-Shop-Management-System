package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.dto.AddToCartRequest;
import com.anurag.SweetShopBackend.dto.CartItemDto;
import com.anurag.SweetShopBackend.model.User;
import com.anurag.SweetShopBackend.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class SimpleCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    private CartItemDto testCartItem;
    private AddToCartRequest addToCartRequest;

    @BeforeEach
    void setUp() {
        testCartItem = new CartItemDto();
        testCartItem.setId(UUID.randomUUID());
        testCartItem.setSweetId(UUID.randomUUID());
        testCartItem.setSweetName("Test Sweet");
        testCartItem.setPrice(10.50);
        testCartItem.setQuantity(2);
        testCartItem.setTotalPrice(21.00);

        addToCartRequest = new AddToCartRequest();
        addToCartRequest.setSweetId(UUID.randomUUID());
        addToCartRequest.setQuantity(2);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetCartItems_Success() throws Exception {
        List<CartItemDto> cartItems = Arrays.asList(testCartItem);
        User user = new User();
        user.setEmail("test@example.com");
        when(cartService.getCartItems(any(User.class))).thenReturn(cartItems);

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].sweetName").value("Test Sweet"))
                .andExpect(jsonPath("$[0].totalPrice").value(21.00));

        verify(cartService).getCartItems(any(User.class));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testAddToCart_Success() throws Exception {
        when(cartService.addToCart(any(User.class), addToCartRequest)).thenReturn(testCartItem);

        mockMvc.perform(post("/api/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addToCartRequest))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sweetName").value("Test Sweet"))
                .andExpect(jsonPath("$.totalPrice").value(21.00));

        verify(cartService).addToCart(any(User.class), addToCartRequest);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testUpdateCartItem_Success() throws Exception {
        UUID cartItemId = UUID.randomUUID();
        doNothing().when(cartService).updateCartItemQuantity(any(User.class), cartItemId, 3);

        mockMvc.perform(put("/api/cart/" + cartItemId)
                .param("quantity", "3")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sweetName").value("Test Sweet"));

        verify(cartService).updateCartItemQuantity(any(User.class), cartItemId, 3);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testRemoveFromCart_Success() throws Exception {
        UUID cartItemId = UUID.randomUUID();
        doNothing().when(cartService).removeFromCart(any(User.class), cartItemId);

        mockMvc.perform(delete("/api/cart/" + cartItemId)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(cartService).removeFromCart(any(User.class), cartItemId);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testClearCart_Success() throws Exception {
        doNothing().when(cartService).clearCart(any(User.class));

        mockMvc.perform(delete("/api/cart")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(cartService).clearCart(any(User.class));
    }
}
