"use client"

import { createContext, useContext, useState, useEffect } from "react"
import { mockSweets } from "../data/mockData"

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

  const addToCart = (sweet) => {
    setCartItems((prevItems) => {
      const existingItem = prevItems.find((item) => item.id === sweet.id)
      const currentQuantity = existingItem ? existingItem.quantity : 0

      // Check if we can add more items (don't exceed stock)
      if (currentQuantity >= sweet.quantity) {
        return prevItems // Don't add if already at stock limit
      }

      if (existingItem) {
        return prevItems.map((item) => (item.id === sweet.id ? { ...item, quantity: item.quantity + 1 } : item))
      } else {
        return [...prevItems, { ...sweet, quantity: 1 }]
      }
    })
  }

  const removeFromCart = (sweetId) => {
    setCartItems((prevItems) => prevItems.filter((item) => item.id !== sweetId))
  }

  const updateQuantity = (sweetId, newQuantity) => {
    if (newQuantity <= 0) {
      removeFromCart(sweetId)
      return
    }

    setCartItems((prevItems) => {
      const item = prevItems.find((item) => item.id === sweetId)
      if (!item) return prevItems

      // Find the original sweet to get stock quantity
      const originalSweet = sweets.find(sweet => sweet.id === sweetId)
      if (!originalSweet) return prevItems

      // Don't allow quantity to exceed stock
      const maxQuantity = originalSweet.quantity
      const limitedQuantity = Math.min(newQuantity, maxQuantity)

      return prevItems.map((item) => (item.id === sweetId ? { ...item, quantity: limitedQuantity } : item))
    })
  }

  const clearCart = () => {
    setCartItems([])
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
