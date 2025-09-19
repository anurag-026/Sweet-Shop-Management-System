import axiosInstance from './axiosInstance'

// Sweet Management APIs
export const sweetService = {
  // Get all sweets with optional filters
  getAllSweets: async (filters = {}) => {
    try {
      const params = new URLSearchParams()
      
      if (filters.name) params.append('name', filters.name)
      if (filters.category) params.append('category', filters.category)
      if (filters.min) params.append('min', filters.min)
      if (filters.max) params.append('max', filters.max)
      
      const response = await axiosInstance.get(`/api/sweets?${params.toString()}`)
      return response.data
    } catch (error) {
      console.error('Error fetching sweets:', error)
      throw error
    }
  },

  // Get sweet by ID
  getSweetById: async (id) => {
    try {
      const response = await axiosInstance.get(`/api/sweets/${id}`)
      return response.data
    } catch (error) {
      console.error('Error fetching sweet:', error)
      throw error
    }
  },

  // Create sweet (Admin only)
  createSweet: async (sweetData) => {
    try {
      const response = await axiosInstance.post('/api/sweets', sweetData)
      return response.data
    } catch (error) {
      console.error('Error creating sweet:', error)
      throw error
    }
  },

  // Update sweet (Admin only)
  updateSweet: async (id, sweetData) => {
    try {
      const response = await axiosInstance.put(`/api/sweets/${id}`, sweetData)
      return response.data
    } catch (error) {
      console.error('Error updating sweet:', error)
      throw error
    }
  },

  // Delete sweet (Admin only)
  deleteSweet: async (id) => {
    try {
      const response = await axiosInstance.delete(`/api/sweets/${id}`)
      return response.data
    } catch (error) {
      console.error('Error deleting sweet:', error)
      throw error
    }
  },

  // Purchase sweet (decrease quantity)
  purchaseSweet: async (id, quantity) => {
    try {
      const response = await axiosInstance.post(`/api/sweets/${id}/purchase?qty=${quantity}`)
      return response.data
    } catch (error) {
      console.error('Error purchasing sweet:', error)
      throw error
    }
  },

  // Restock sweet (Admin only)
  restockSweet: async (id, quantity = 1) => {
    try {
      const response = await axiosInstance.post(`/api/sweets/${id}/restock?qty=${quantity}`)
      return response.data
    } catch (error) {
      console.error('Error restocking sweet:', error)
      throw error
    }
  }
}
