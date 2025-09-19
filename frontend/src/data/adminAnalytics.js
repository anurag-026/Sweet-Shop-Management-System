// Mock analytics data for admin dashboard
export const adminAnalytics = {
  // Sales Overview
  salesOverview: {
    totalRevenue: 45680.50,
    totalOrders: 1247,
    averageOrderValue: 36.65,
    conversionRate: 3.2,
    revenueGrowth: 12.5,
    ordersGrowth: 8.3,
    avgOrderGrowth: 4.1,
    conversionGrowth: -0.8
  },

  // Inventory Status
  inventoryStatus: {
    totalProducts: 45,
    inStock: 38,
    lowStock: 5,
    outOfStock: 2,
    totalValue: 125430.75,
    lowStockThreshold: 10
  },

  // Top Selling Products
  topSellingProducts: [
    {
      id: 1,
      name: "Artisan Gummy Bears",
      category: "Gummies",
      unitsSold: 245,
      revenue: 3675.00,
      profit: 1470.00,
      profitMargin: 40.0,
      stock: 15,
      image: "/src/assets/artisan-gummy-bears-colorful.jpg"
    },
    {
      id: 2,
      name: "Chocolate Truffles",
      category: "Chocolate",
      unitsSold: 198,
      revenue: 4950.00,
      profit: 1980.00,
      profitMargin: 40.0,
      stock: 8,
      image: "/src/assets/elegant-dark-chocolate-truffles.jpg"
    },
    {
      id: 3,
      name: "French Macarons",
      category: "Macarons",
      unitsSold: 156,
      revenue: 3120.00,
      profit: 1248.00,
      profitMargin: 40.0,
      stock: 22,
      image: "/src/assets/colorful-french-macarons-assortment.jpg"
    },
    {
      id: 4,
      name: "Salted Caramel Bonbons",
      category: "Caramel",
      unitsSold: 134,
      revenue: 2680.00,
      profit: 1072.00,
      profitMargin: 40.0,
      stock: 12,
      image: "/src/assets/salted-caramel-bonbons-chocolate.jpg"
    },
    {
      id: 5,
      name: "Honey Lavender Lollipops",
      category: "Lollipops",
      unitsSold: 98,
      revenue: 1470.00,
      profit: 588.00,
      profitMargin: 40.0,
      stock: 6,
      image: "/src/assets/honey-lavender-lollipops-purple.jpg"
    }
  ],

  // Low Stock Products
  lowStockProducts: [
    {
      id: 6,
      name: "Pistachio Rose Turkish Delight",
      category: "Turkish Delight",
      currentStock: 3,
      minThreshold: 10,
      lastRestocked: "2024-01-15",
      supplier: "Premium Sweets Co.",
      image: "/src/assets/pistachio-rose-turkish-delight-pink.jpg"
    },
    {
      id: 7,
      name: "Mint Chocolate Chip Brittle",
      category: "Brittle",
      currentStock: 7,
      minThreshold: 10,
      lastRestocked: "2024-01-20",
      supplier: "Artisan Confections",
      image: "/src/assets/mint-chocolate-chip-brittle-green.jpg"
    },
    {
      id: 8,
      name: "Strawberry Cream Fudge",
      category: "Fudge",
      currentStock: 5,
      minThreshold: 10,
      lastRestocked: "2024-01-18",
      supplier: "Sweet Dreams Ltd",
      image: "/src/assets/strawberry-cream-fudge-pink-white.jpg"
    },
    {
      id: 9,
      name: "Coconut Lime Macaroons",
      category: "Macaroons",
      currentStock: 4,
      minThreshold: 10,
      lastRestocked: "2024-01-22",
      supplier: "Tropical Treats",
      image: "/src/assets/coconut-lime-macaroons-white-green.jpg"
    },
    {
      id: 10,
      name: "Champagne Gummy Rings",
      category: "Gummies",
      currentStock: 8,
      minThreshold: 10,
      lastRestocked: "2024-01-25",
      supplier: "Luxury Gummies Inc",
      image: "/src/assets/champagne-gummy-rings-gold-elegant.jpg"
    }
  ],

  // Sales by Category
  salesByCategory: [
    { category: "Chocolate", revenue: 15680.50, percentage: 34.3, unitsSold: 456, profit: 6272.20 },
    { category: "Gummies", revenue: 12340.25, percentage: 27.0, unitsSold: 789, profit: 4936.10 },
    { category: "Macarons", revenue: 8920.75, percentage: 19.5, unitsSold: 234, profit: 3568.30 },
    { category: "Caramel", revenue: 4560.00, percentage: 10.0, unitsSold: 156, profit: 1824.00 },
    { category: "Lollipops", revenue: 2340.00, percentage: 5.1, unitsSold: 98, profit: 936.00 },
    { category: "Fudge", revenue: 1859.00, percentage: 4.1, unitsSold: 67, profit: 743.60 }
  ],

  // Recent Orders
  recentOrders: [
    {
      id: "ORD-2024-001",
      customer: "Sarah Johnson",
      email: "sarah.j@email.com",
      total: 89.50,
      status: "Delivered",
      date: "2024-01-28",
      items: 3,
      paymentMethod: "Credit Card"
    },
    {
      id: "ORD-2024-002",
      customer: "Michael Chen",
      email: "m.chen@email.com",
      total: 156.75,
      status: "Processing",
      date: "2024-01-28",
      items: 5,
      paymentMethod: "PayPal"
    },
    {
      id: "ORD-2024-003",
      customer: "Emily Rodriguez",
      email: "emily.r@email.com",
      total: 67.25,
      status: "Shipped",
      date: "2024-01-27",
      items: 2,
      paymentMethod: "Credit Card"
    },
    {
      id: "ORD-2024-004",
      customer: "David Wilson",
      email: "d.wilson@email.com",
      total: 234.00,
      status: "Delivered",
      date: "2024-01-27",
      items: 7,
      paymentMethod: "Bank Transfer"
    },
    {
      id: "ORD-2024-005",
      customer: "Lisa Anderson",
      email: "lisa.a@email.com",
      total: 45.50,
      status: "Pending",
      date: "2024-01-26",
      items: 1,
      paymentMethod: "Credit Card"
    }
  ],

  // Monthly Sales Data (last 12 months)
  monthlySales: [
    { month: "Feb 2023", revenue: 32150.00, orders: 856, growth: 5.2 },
    { month: "Mar 2023", revenue: 34520.00, orders: 923, growth: 7.4 },
    { month: "Apr 2023", revenue: 28950.00, orders: 745, growth: -16.1 },
    { month: "May 2023", revenue: 36780.00, orders: 987, growth: 27.0 },
    { month: "Jun 2023", revenue: 41230.00, orders: 1105, growth: 12.1 },
    { month: "Jul 2023", revenue: 38950.00, orders: 1023, growth: -5.5 },
    { month: "Aug 2023", revenue: 42340.00, orders: 1156, growth: 8.7 },
    { month: "Sep 2023", revenue: 45680.00, orders: 1247, growth: 7.9 },
    { month: "Oct 2023", revenue: 48920.00, orders: 1324, growth: 7.1 },
    { month: "Nov 2023", revenue: 52340.00, orders: 1456, growth: 7.0 },
    { month: "Dec 2023", revenue: 67890.00, orders: 1890, growth: 29.7 },
    { month: "Jan 2024", revenue: 45680.50, orders: 1247, growth: -32.7 }
  ],

  // Customer Analytics
  customerAnalytics: {
    totalCustomers: 2847,
    newCustomers: 156,
    returningCustomers: 89,
    averageCustomerValue: 65.40,
    customerRetentionRate: 78.5,
    topCustomerSegments: [
      { segment: "Premium Buyers", count: 234, avgOrderValue: 125.50 },
      { segment: "Regular Customers", count: 1456, avgOrderValue: 45.20 },
      { segment: "Occasional Buyers", count: 1157, avgOrderValue: 28.90 }
    ]
  },

  // Performance Metrics
  performanceMetrics: {
    websiteTraffic: {
      totalVisits: 45680,
      uniqueVisitors: 23450,
      bounceRate: 32.5,
      avgSessionDuration: "3:45",
      pageViews: 123450
    },
    conversionFunnel: {
      visitors: 45680,
      productViews: 23450,
      addToCart: 3456,
      checkout: 1247,
      completed: 1156
    },
    shippingMetrics: {
      avgShippingTime: "2.3 days",
      onTimeDelivery: 94.2,
      shippingCost: 8.50,
      freeShippingThreshold: 75.00
    }
  },

  // Alerts and Notifications
  alerts: [
    {
      id: 1,
      type: "warning",
      title: "Low Stock Alert",
      message: "5 products are running low on stock",
      timestamp: "2024-01-28T10:30:00Z",
      priority: "high"
    },
    {
      id: 2,
      type: "info",
      title: "New Order Received",
      message: "Order #ORD-2024-002 worth $156.75 received",
      timestamp: "2024-01-28T09:15:00Z",
      priority: "medium"
    },
    {
      id: 3,
      type: "success",
      title: "Monthly Target Achieved",
      message: "January revenue target exceeded by 12%",
      timestamp: "2024-01-28T08:00:00Z",
      priority: "low"
    },
    {
      id: 4,
      type: "warning",
      title: "Payment Issue",
      message: "Payment failed for Order #ORD-2024-005",
      timestamp: "2024-01-27T16:45:00Z",
      priority: "high"
    }
  ]
}

export default adminAnalytics
