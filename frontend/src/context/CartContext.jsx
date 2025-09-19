"use client"

import { createContext, useContext, useState, useEffect } from "react"
import { mockSweets } from "../data/mockData"
import { cartService } from "../services/cartService"

const CartContext = createContext()

export const useCart = () => {
  const context = useContext(CartContext)
  if (!context) {
    throw new Error("useCart must be used within a CartProvider")
  }
  return context
}

export const CartProvider = ({ children }) => {
  const [cartItems, setCartItems] = useState([])
  const [sweets, setSweets] = useState(mockSweets)

  useEffect(() => {
    // Load cart from localStorage on app start
    const savedCart = localStorage.getItem("cart")
    if (savedCart) {
      setCartItems(JSON.parse(savedCart))
    }
  }, [])

  useEffect(() => {
    // Save cart to localStorage whenever it changes
    localStorage.setItem("cart", JSON.stringify(cartItems))
  }, [cartItems])

  const addToCart = async (sweet) => {
    try {
      // Check if we can add more items (don't exceed stock)
      const existingItem = cartItems.find((item) => item.id === sweet.id)
      const currentQuantity = existingItem ? existingItem.quantity : 0

      if (currentQuantity >= sweet.quantity) {
        alert("Cannot add more items. Stock limit reached.")
        return
      }

      // Add to cart via API
      await cartService.addToCart(sweet.id, 1)
      
      // Update local state
      setCartItems((prevItems) => {
        if (existingItem) {
          return prevItems.map((item) => (item.id === sweet.id ? { ...item, quantity: item.quantity + 1 } : item))
        } else {
          return [...prevItems, { ...sweet, quantity: 1 }]
        }
      })
    } catch (error) {
      console.error('Error adding to cart:', error)
      alert("Failed to add item to cart. Please try again.")
    }
  }

  const removeFromCart = async (sweetId) => {
    try {
      // Find the cart item to get its ID for API call
      const cartItem = cartItems.find((item) => item.id === sweetId)
      if (cartItem && cartItem.cartItemId) {
        await cartService.removeFromCart(cartItem.cartItemId)
      }
      
      // Update local state
      setCartItems((prevItems) => prevItems.filter((item) => item.id !== sweetId))
    } catch (error) {
      console.error('Error removing from cart:', error)
      alert("Failed to remove item from cart. Please try again.")
    }
  }

  const updateQuantity = async (sweetId, newQuantity) => {
    if (newQuantity <= 0) {
      removeFromCart(sweetId)
      return
    }

    try {
      // Find the cart item to get its ID for API call
      const cartItem = cartItems.find((item) => item.id === sweetId)
      if (cartItem && cartItem.cartItemId) {
        await cartService.updateCartItem(cartItem.cartItemId, newQuantity)
      }

      // Find the original sweet to get stock quantity
      const originalSweet = sweets.find(sweet => sweet.id === sweetId)
      if (!originalSweet) return

      // Don't allow quantity to exceed stock
      const maxQuantity = originalSweet.quantity
      const limitedQuantity = Math.min(newQuantity, maxQuantity)

      // Update local state
      setCartItems((prevItems) =>
        prevItems.map((item) => (item.id === sweetId ? { ...item, quantity: limitedQuantity } : item))
      )
    } catch (error) {
      console.error('Error updating quantity:', error)
      alert("Failed to update quantity. Please try again.")
    }
  }

  const clearCart = async () => {
    try {
      await cartService.clearCart()
      setCartItems([])
    } catch (error) {
      console.error('Error clearing cart:', error)
      alert("Failed to clear cart. Please try again.")
    }
  }

  const getTotalPrice = () => {
    return cartItems.reduce((total, item) => total + item.price * item.quantity, 0)
  }

  const getTotalItems = () => {
    return cartItems.reduce((total, item) => total + item.quantity, 0)
  }

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
  }

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>
}

export { CartContext }
