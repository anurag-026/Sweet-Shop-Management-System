import axiosInstance from './axiosInstance'

// Admin APIs
export const adminService = {
  // Get admin dashboard statistics
  getDashboardStats: async () => {
    try {
      const response = await axiosInstance.get('/api/admin/dashboard')
      return response.data
    } catch (error) {
      console.error('Error fetching dashboard stats:', error)
      throw error
    }
  },

  // Get system status
  getSystemStatus: async () => {
    try {
      const response = await axiosInstance.get('/api/admin/system-status')
      return response.data
    } catch (error) {
      console.error('Error fetching system status:', error)
      throw error
    }
  }
}

