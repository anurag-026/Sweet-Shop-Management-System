"use client";

import { createContext, useContext, useState, useEffect } from "react";
import { mockSweets } from "../data/mockData";
import { cartService } from "../services/cartService";
import { useAuth } from "./AuthContext";

const CartContext = createContext();

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error("useCart must be used within a CartProvider");
  }
  return context;
};

export const CartProvider = ({ children }) => {
  const [cartItems, setCartItems] = useState([]);
  const [sweets, setSweets] = useState(mockSweets);
  const { isAuthenticated } = useAuth();

  useEffect(() => {
    // Only sync cart when authenticated
    if (isAuthenticated) {
      syncCartFromServer();
    } else {
      setCartItems([]);
    }
  }, [isAuthenticated]);

  const syncCartFromServer = async () => {
    try {
      const items = await cartService.getCartItems();
      // Map backend cart items to UI shape
      const mapped = (items || []).map((ci) => ({
        id: ci.sweetId,
        cartItemId: ci.id,
        name: ci.sweetName,
        category: ci.category,
        price: ci.price,
        quantity: ci.quantity,
        description: ci.description,
        image: ci.image,
        totalPrice: ci.totalPrice,
      }));
      setCartItems(mapped);
    } catch (error) {
      console.error("Error syncing cart:", error);
    }
  };

  const addToCart = async (sweet) => {
    try {
      // Add to cart via API; backend returns the created/updated CartItem
      const ci = await cartService.addToCart(sweet.id, 1);
      if (ci) {
        // Re-sync from server to ensure quantities are authoritative
        await syncCartFromServer();
      }
    } catch (error) {
      console.error("Error adding to cart:", error);
      alert("Failed to add item to cart. Please try again.");
    }
  };

  const removeFromCart = async (sweetId) => {
    try {
      // Find the cart item to get its ID for API call
      const cartItem = cartItems.find((item) => item.id === sweetId);
      if (cartItem && cartItem.cartItemId) {
        await cartService.removeFromCart(cartItem.cartItemId);
      }

      // Re-sync from server
      await syncCartFromServer();
    } catch (error) {
      console.error("Error removing from cart:", error);
      alert("Failed to remove item from cart. Please try again.");
    }
  };

  const updateQuantity = async (sweetId, newQuantity) => {
    try {
      if (newQuantity <= 0) {
        await removeFromCart(sweetId);
        return;
      }

      // Find the cart item to get its ID for API call
      const cartItem = cartItems.find((item) => item.id === sweetId);
      if (cartItem && cartItem.cartItemId) {
        await cartService.updateCartItem(cartItem.cartItemId, newQuantity);
      }

      await syncCartFromServer();
    } catch (error) {
      console.error("Error updating quantity:", error);
      alert("Failed to update quantity. Please try again.");
    }
  };

  const clearCart = async () => {
    try {
      await cartService.clearCart();
      await syncCartFromServer();
    } catch (error) {
      console.error("Error clearing cart:", error);
      alert("Failed to clear cart. Please try again.");
    }
  };

  const getTotalPrice = () => {
    return cartItems.reduce(
      (total, item) => total + item.price * item.quantity,
      0
    );
  };

  const getTotalItems = () => {
    return cartItems.reduce((total, item) => total + item.quantity, 0);
  };

  const value = {
    cartItems,
    addToCart,
    removeFromCart,
    updateQuantity,
    clearCart,
    getTotalPrice,
    getTotalItems,
    sweets,
    setSweets,
  };

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};

export { CartContext };
