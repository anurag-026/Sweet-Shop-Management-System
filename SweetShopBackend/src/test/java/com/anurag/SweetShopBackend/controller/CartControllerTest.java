package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.dto.AddToCartRequest;
import com.anurag.SweetShopBackend.dto.CartItemDto;
import com.anurag.SweetShopBackend.model.User;
import com.anurag.SweetShopBackend.repository.UserRepository;
import com.anurag.SweetShopBackend.service.CartService;
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

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private CartItemDto testCartItem;
    private List<CartItemDto> testCartItems;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole("ROLE_USER");

        testCartItem = new CartItemDto();
        testCartItem.setId(UUID.randomUUID());
        testCartItem.setSweetId(UUID.randomUUID());
        testCartItem.setSweetName("Test Sweet");
        testCartItem.setPrice(10.50);
        testCartItem.setQuantity(2);
        testCartItem.setTotalPrice(21.00);

        CartItemDto cartItem2 = new CartItemDto();
        cartItem2.setId(UUID.randomUUID());
        cartItem2.setSweetId(UUID.randomUUID());
        cartItem2.setSweetName("Test Sweet 2");
        cartItem2.setPrice(15.75);
        cartItem2.setQuantity(1);
        cartItem2.setTotalPrice(15.75);

        testCartItems = Arrays.asList(testCartItem, cartItem2);

        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@example.com");
        when(authentication.isAuthenticated()).thenReturn(true);
    }

    @Test
    void testGetCartItems_Success() throws Exception {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(cartService.getCartItems(testUser)).thenReturn(testCartItems);

        // When & Then
        mockMvc.perform(get("/api/cart")
                .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].sweetName").value("Test Sweet"))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[0].totalPrice").value(21.00))
                .andExpect(jsonPath("$[1].sweetName").value("Test Sweet 2"))
                .andExpect(jsonPath("$[1].quantity").value(1))
                .andExpect(jsonPath("$[1].totalPrice").value(15.75));

        verify(cartService).getCartItems(testUser);
    }

    @Test
    void testGetCartItems_EmptyCart() throws Exception {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(cartService.getCartItems(testUser)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/cart")
                .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(cartService).getCartItems(testUser);
    }

    @Test
    void testGetCartItems_UserNotFound() throws Exception {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/cart")
                .with(authentication(authentication)))
                .andExpect(status().isInternalServerError());

        verify(cartService, never()).getCartItems(any());
    }

    @Test
    void testAddToCart_Success() throws Exception {
        // Given
        AddToCartRequest request = new AddToCartRequest();
        request.setSweetId(UUID.randomUUID());
        request.setQuantity(3);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(cartService.addToCart(eq(testUser), any(AddToCartRequest.class))).thenReturn(testCartItem);

        // When & Then
        mockMvc.perform(post("/api/cart/add")
                .with(authentication(authentication))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sweetName").value("Test Sweet"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(21.00));

        verify(cartService).addToCart(eq(testUser), any(AddToCartRequest.class));
    }

    @Test
    void testAddToCart_InvalidInput() throws Exception {
        // Given
        AddToCartRequest request = new AddToCartRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/cart/add")
                .with(authentication(authentication))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(cartService, never()).addToCart(any(), any());
    }

    @Test
    void testAddToCart_UserNotFound() throws Exception {
        // Given
        AddToCartRequest request = new AddToCartRequest();
        request.setSweetId(UUID.randomUUID());
        request.setQuantity(3);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/cart/add")
                .with(authentication(authentication))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(cartService, never()).addToCart(any(), any());
    }

    @Test
    void testUpdateCartItemQuantity_Success() throws Exception {
        // Given
        UUID cartItemId = testCartItem.getId();
        Integer newQuantity = 5;

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        doNothing().when(cartService).updateCartItemQuantity(testUser, cartItemId, newQuantity);

        // When & Then
        mockMvc.perform(put("/api/cart/{cartItemId}", cartItemId)
                .with(authentication(authentication))
                .param("quantity", "5"))
                .andExpect(status().isOk());

        verify(cartService).updateCartItemQuantity(testUser, cartItemId, newQuantity);
    }

    @Test
    void testUpdateCartItemQuantity_UserNotFound() throws Exception {
        // Given
        UUID cartItemId = testCartItem.getId();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/cart/{cartItemId}", cartItemId)
                .with(authentication(authentication))
                .param("quantity", "5"))
                .andExpect(status().isInternalServerError());

        verify(cartService, never()).updateCartItemQuantity(any(), any(), anyInt());
    }

    @Test
    void testUpdateCartItemQuantity_MissingQuantity() throws Exception {
        // Given
        UUID cartItemId = testCartItem.getId();

        // When & Then
        mockMvc.perform(put("/api/cart/{cartItemId}", cartItemId)
                .with(authentication(authentication)))
                .andExpect(status().isBadRequest());

        verify(cartService, never()).updateCartItemQuantity(any(), any(), anyInt());
    }

    @Test
    void testRemoveFromCart_Success() throws Exception {
        // Given
        UUID cartItemId = testCartItem.getId();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        doNothing().when(cartService).removeFromCart(testUser, cartItemId);

        // When & Then
        mockMvc.perform(delete("/api/cart/{cartItemId}", cartItemId)
                .with(authentication(authentication)))
                .andExpect(status().isOk());

        verify(cartService).removeFromCart(testUser, cartItemId);
    }

    @Test
    void testRemoveFromCart_UserNotFound() throws Exception {
        // Given
        UUID cartItemId = testCartItem.getId();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(delete("/api/cart/{cartItemId}", cartItemId)
                .with(authentication(authentication)))
                .andExpect(status().isInternalServerError());

        verify(cartService, never()).removeFromCart(any(), any());
    }

    @Test
    void testClearCart_Success() throws Exception {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        doNothing().when(cartService).clearCart(testUser);

        // When & Then
        mockMvc.perform(delete("/api/cart/clear")
                .with(authentication(authentication)))
                .andExpect(status().isOk());

        verify(cartService).clearCart(testUser);
    }

    @Test
    void testClearCart_UserNotFound() throws Exception {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(delete("/api/cart/clear")
                .with(authentication(authentication)))
                .andExpect(status().isInternalServerError());

        verify(cartService, never()).clearCart(any());
    }

    @Test
    void testGetCartItems_NotAuthenticated() throws Exception {
        // Given
        Authentication unauthenticated = mock(Authentication.class);
        when(unauthenticated.isAuthenticated()).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/cart")
                .with(authentication(unauthenticated)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAddToCart_NotAuthenticated() throws Exception {
        // Given
        AddToCartRequest request = new AddToCartRequest();
        request.setSweetId(UUID.randomUUID());
        request.setQuantity(3);

        Authentication unauthenticated = mock(Authentication.class);
        when(unauthenticated.isAuthenticated()).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/cart/add")
                .with(authentication(unauthenticated))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
