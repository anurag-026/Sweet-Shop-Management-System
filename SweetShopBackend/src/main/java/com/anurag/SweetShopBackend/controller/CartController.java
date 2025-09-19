package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.dto.AddToCartRequest;
import com.anurag.SweetShopBackend.dto.CartItemDto;
import com.anurag.SweetShopBackend.model.User;
import com.anurag.SweetShopBackend.repository.UserRepository;
import com.anurag.SweetShopBackend.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<CartItemDto>> getCartItems(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<CartItemDto> cartItems = cartService.getCartItems(user);
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/add")
    public ResponseEntity<CartItemDto> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            Authentication authentication) {
        
        User user = getCurrentUser(authentication);
        CartItemDto cartItem = cartService.addToCart(user, request);
        return ResponseEntity.ok(cartItem);
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItemDto> updateCartItemQuantity(
            @PathVariable UUID cartItemId,
            @RequestParam Integer quantity,
            Authentication authentication) {
        
        User user = getCurrentUser(authentication);
        cartService.updateCartItemQuantity(user, cartItemId, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable UUID cartItemId,
            Authentication authentication) {
        
        User user = getCurrentUser(authentication);
        cartService.removeFromCart(user, cartItemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        User user = getCurrentUser(authentication);
        cartService.clearCart(user);
        return ResponseEntity.ok().build();
    }

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }
}
