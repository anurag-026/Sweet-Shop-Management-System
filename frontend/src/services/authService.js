import axiosInstance from './axiosInstance'

export const authService = {
  // Register user
  register: async ({ fullName, email, password }) => {
    try {
      const response = await axiosInstance.post('/api/auth/register', {
        fullName,
        email,
        password
      })
      return response.data
    } catch (error) {
      console.error('Error registering user:', error)
      throw error
    }
  },

  // Login user
  login: async ({ email, password }) => {
    try {
      const response = await axiosInstance.post('/api/auth/login', {
        email,
        password
      })
      const { token } = response.data || {}
      if (token) {
        localStorage.setItem('access_token', token)
      }
      return response.data
    } catch (error) {
      console.error('Error logging in:', error)
      throw error
    }
  },

  // Logout user
  logout: async () => {
    try {
      const response = await axiosInstance.post('/api/auth/logout')
      localStorage.clear()
      return response.data
    } catch (error) {
      console.error('Error logging out:', error)
      throw error
    }
  },

  // Get profile
  getProfile: async () => {
    try {
      const response = await axiosInstance.get('/api/auth/profile')
      return response.data
    } catch (error) {
      console.error('Error fetching profile:', error)
      throw error
    }
  },

  // Update profile (PATCH)
  updateProfile: async ({ name, phone, address }) => {
    try {
      const response = await axiosInstance.patch('/api/auth/profile', {
        name,
        phone,
        address
      }, {
        headers: { 'Content-Type': 'application/json' }
      })
      return response.data
    } catch (error) {
      console.error('Error updating profile:', error)
      throw error
    }
  }
}

export default authService


