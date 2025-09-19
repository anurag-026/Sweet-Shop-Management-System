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
  },

  // Analytics: Sales Overview
  getSalesOverview: async (range = '30d') => {
    try {
      const response = await axiosInstance.get('/api/admin/analytics/sales-overview', {
        params: { range }
      })
      return response.data
    } catch (error) {
      console.error('Error fetching sales overview:', error)
      throw error
    }
  },

  // Analytics: Monthly Sales
  getMonthlySales: async (months = 12) => {
    try {
      const response = await axiosInstance.get('/api/admin/analytics/monthly-sales', {
        params: { months }
      })
      return response.data
    } catch (error) {
      console.error('Error fetching monthly sales:', error)
      throw error
    }
  },

  // Analytics: Top Products
  getTopProducts: async (limit = 5) => {
    try {
      const response = await axiosInstance.get('/api/admin/analytics/top-products', {
        params: { limit }
      })
      return response.data
    } catch (error) {
      console.error('Error fetching top products:', error)
      throw error
    }
  },

  // Analytics: Sales by Category
  getSalesByCategory: async (range = '30d') => {
    try {
      const response = await axiosInstance.get('/api/admin/analytics/sales-by-category', {
        params: { range }
      })
      return response.data
    } catch (error) {
      console.error('Error fetching sales by category:', error)
      throw error
    }
  },

  // Analytics: Customer Summary
  getCustomerSummary: async (range = '30d') => {
    try {
      const response = await axiosInstance.get('/api/admin/analytics/customers/summary', {
        params: { range }
      })
      return response.data
    } catch (error) {
      console.error('Error fetching customer summary:', error)
      throw error
    }
  },

  // Analytics: Customer Segments
  getCustomerSegments: async (range = '30d') => {
    try {
      const response = await axiosInstance.get('/api/admin/analytics/customers/segments', {
        params: { range }
      })
      return response.data
    } catch (error) {
      console.error('Error fetching customer segments:', error)
      throw error
    }
  },

  // Analytics: Website Traffic
  getWebsiteTraffic: async (range = '30d') => {
    try {
      const response = await axiosInstance.get('/api/admin/analytics/performance/traffic', {
        params: { range }
      })
      return response.data
    } catch (error) {
      console.error('Error fetching website traffic:', error)
      throw error
    }
  },

  // Analytics: Conversion Funnel
  getConversionFunnel: async (range = '30d') => {
    try {
      const response = await axiosInstance.get('/api/admin/analytics/performance/conversion-funnel', {
        params: { range }
      })
      return response.data
    } catch (error) {
      console.error('Error fetching conversion funnel:', error)
      throw error
    }
  },

  // Analytics: Shipping Metrics
  getShippingMetrics: async (range = '30d') => {
    try {
      const response = await axiosInstance.get('/api/admin/analytics/shipping-metrics', {
        params: { range }
      })
      return response.data
    } catch (error) {
      console.error('Error fetching shipping metrics:', error)
      throw error
    }
  },

  // Admin: Alerts
  getAlerts: async (limit = 20) => {
    try {
      const response = await axiosInstance.get('/api/admin/alerts', {
        params: { limit }
      })
      return response.data
    } catch (error) {
      console.error('Error fetching alerts:', error)
      throw error
    }
  },

  // Inventory: Status
  getInventoryStatus: async () => {
    try {
      const response = await axiosInstance.get('/api/admin/inventory/status')
      return response.data
    } catch (error) {
      console.error('Error fetching inventory status:', error)
      throw error
    }
  },

  // Inventory: Low Stock Items
  getLowStockItems: async (threshold = 10) => {
    try {
      const response = await axiosInstance.get('/api/admin/inventory/low-stock', {
        params: { threshold }
      })
      return response.data
    } catch (error) {
      console.error('Error fetching low stock items:', error)
      throw error
    }
  },

  // Admin Orders: List Orders
  listOrders: async ({ status, page = 0, size = 20, sort = 'createdAt,desc' } = {}) => {
    try {
      const response = await axiosInstance.get('/api/admin/orders', {
        params: { status, page, size, sort }
      })
      return response.data
    } catch (error) {
      console.error('Error listing admin orders:', error)
      throw error
    }
  },

  // Admin Orders: Recent Orders
  getRecentOrders: async (limit = 20) => {
    try {
      const response = await axiosInstance.get('/api/admin/orders/recent', {
        params: { limit }
      })
      return response.data
    } catch (error) {
      console.error('Error fetching recent admin orders:', error)
      throw error
    }
  }
}

