import axiosInstance from './axiosInstance'

// Order Management APIs
export const orderService = {
  // Checkout (create order from cart)
  checkout: async () => {
    try {
      const response = await axiosInstance.post('/api/orders/checkout')
      return response.data
    } catch (error) {
      console.error('Error during checkout:', error)
      throw error
    }
  },

  // Checkout with payment mode (create order from cart with payment details)
  checkoutWithPayment: async (paymentData) => {
    try {
      const response = await axiosInstance.post('/api/orders/checkout', paymentData)
      return response.data
    } catch (error) {
      console.error('Error during checkout with payment:', error)
      throw error
    }
  },

  // Get all user orders
  getUserOrders: async () => {
    try {
      const response = await axiosInstance.get('/api/orders')
      return response.data
    } catch (error) {
      console.error('Error fetching orders:', error)
      throw error
    }
  },

  // Get order by ID
  getOrderById: async (orderId) => {
    try {
      const response = await axiosInstance.get(`/api/orders/${orderId}`)
      return response.data
    } catch (error) {
      console.error('Error fetching order:', error)
      throw error
    }
  },

  // Update order status (Admin only)
  updateOrderStatus: async (orderId, status) => {
    try {
      const response = await axiosInstance.put(`/api/orders/${orderId}/status?status=${status}`)
      return response.data
    } catch (error) {
      console.error('Error updating order status:', error)
      throw error
    }
  },

  // Update order tracking number (Admin only)
  updateOrderTracking: async (orderId, trackingNumber) => {
    try {
      const response = await axiosInstance.put(`/api/orders/${orderId}/tracking`, null, {
        params: { trackingNumber }
      })
      return response.data
    } catch (error) {
      console.error('Error updating order tracking:', error)
      throw error
    }
  }
}

