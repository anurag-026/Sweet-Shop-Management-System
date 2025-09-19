import axiosInstance from './axiosInstance'

// Cart Management APIs
export const cartService = {
  // Get all cart items
  getCartItems: async () => {
    try {
      const response = await axiosInstance.get('/api/cart')
      return response.data
    } catch (error) {
      console.error('Error fetching cart items:', error)
      throw error
    }
  },

  // Add item to cart
  addToCart: async (sweetId, quantity) => {
    try {
      const response = await axiosInstance.post('/api/cart/add', {
        sweetId,
        quantity
      })
      return response.data
    } catch (error) {
      console.error('Error adding to cart:', error)
      throw error
    }
  },

  // Update cart item quantity
  updateCartItem: async (cartItemId, quantity) => {
    try {
      const response = await axiosInstance.put(`/api/cart/${cartItemId}?quantity=${quantity}`)
      return response.data
    } catch (error) {
      console.error('Error updating cart item:', error)
      throw error
    }
  },

  // Remove item from cart
  removeFromCart: async (cartItemId) => {
    try {
      const response = await axiosInstance.delete(`/api/cart/${cartItemId}`)
      return response.data
    } catch (error) {
      console.error('Error removing from cart:', error)
      throw error
    }
  },

  // Clear entire cart
  clearCart: async () => {
    try {
      const response = await axiosInstance.delete('/api/cart/clear')
      return response.data
    } catch (error) {
      console.error('Error clearing cart:', error)
      throw error
    }
  }
}

