package com.anurag.SweetShopBackend.service;

import com.anurag.SweetShopBackend.dto.AddToCartRequest;
import com.anurag.SweetShopBackend.dto.CartItemDto;
import com.anurag.SweetShopBackend.model.CartItem;
import com.anurag.SweetShopBackend.model.Sweet;
import com.anurag.SweetShopBackend.model.User;
import com.anurag.SweetShopBackend.repository.CartItemRepository;
import com.anurag.SweetShopBackend.repository.SweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private SweetRepository sweetRepository;

    public List<CartItemDto> getCartItems(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        return cartItems.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CartItemDto addToCart(User user, AddToCartRequest request) {
        Sweet sweet = sweetRepository.findById(request.getSweetId())
                .orElseThrow(() -> new IllegalArgumentException("Sweet not found with id: " + request.getSweetId()));

        if (sweet.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock available for sweet: " + sweet.getName());
        }

        Optional<CartItem> existingCartItem = cartItemRepository.findByUserAndSweet(user, sweet);
        
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItem.setPrice(sweet.getPrice());
            cartItemRepository.save(cartItem);
            return convertToDto(cartItem);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setUser(user);
            newCartItem.setSweet(sweet);
            newCartItem.setQuantity(request.getQuantity());
            newCartItem.setPrice(sweet.getPrice());
            cartItemRepository.save(newCartItem);
            return convertToDto(newCartItem);
        }
    }

    @Transactional
    public void updateCartItemQuantity(User user, UUID cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found with id: " + cartItemId));

        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only update your own cart items");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            if (cartItem.getSweet().getQuantity() < quantity) {
                throw new IllegalArgumentException("Not enough stock available for sweet: " + cartItem.getSweet().getName());
            }
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }

    @Transactional
    public void removeFromCart(User user, UUID cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found with id: " + cartItemId));

        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only remove your own cart items");
        }

        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }

    private CartItemDto convertToDto(CartItem cartItem) {
        CartItemDto dto = new CartItemDto();
        dto.setId(cartItem.getId());
        dto.setSweetId(cartItem.getSweet().getId());
        dto.setSweetName(cartItem.getSweet().getName());
        dto.setCategory(cartItem.getSweet().getCategory());
        dto.setPrice(cartItem.getPrice());
        dto.setQuantity(cartItem.getQuantity());
        dto.setTotalPrice(cartItem.getPrice() * cartItem.getQuantity());
        dto.setDescription(cartItem.getSweet().getDescription());
        dto.setImage(cartItem.getSweet().getImage());
        return dto;
    }
}
