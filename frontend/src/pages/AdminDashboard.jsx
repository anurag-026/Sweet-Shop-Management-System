"use client";

import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { useAuth } from "../context/AuthContext";
import { adminService } from "../services/adminService";
import { sweetService } from "../services/sweetService";
import { orderService } from "../services/orderService";
import "./AdminDashboard.css";

const AdminDashboard = () => {
  const { user } = useAuth();
  const [selectedTimeRange, setSelectedTimeRange] = useState("30d");
  const [activeTab, setActiveTab] = useState("overview");
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [realTimeData, setRealTimeData] = useState({
    dashboardStats: null,
    systemStatus: null,
    recentOrders: [],
  });

  // Fetch analytics on component mount and when time range changes
  useEffect(() => {
    fetchAnalytics(selectedTimeRange);
  }, [selectedTimeRange]);


  const fetchAnalytics = async (range) => {
    try {
      setLoading(true);
      setError("");

      // Use Promise.allSettled instead of Promise.all to handle individual failures
      const results = await Promise.allSettled([
        adminService.getSalesOverview(range),
        adminService.getMonthlySales(12),
        adminService.getTopProducts(5),
        adminService.getSalesByCategory(range),
        adminService.getCustomerSummary(range),
        adminService.getCustomerSegments(range),
        adminService.getWebsiteTraffic(range),
        adminService.getConversionFunnel(range),
        adminService.getShippingMetrics(range),
        adminService.getAlerts(20),
        adminService.getInventoryStatus(),
        adminService.getLowStockItems(10),
        adminService.getRecentOrders(20),
      ]);

      // Extract successful results and log failures
      const [
        salesOverviewResult,
        monthlySalesResult,
        topProductsResult,
        salesByCategoryResult,
        customerSummaryResult,
        customerSegmentsResult,
        websiteTrafficResult,
        conversionFunnelResult,
        shippingMetricsResult,
        alertsResult,
        inventoryStatusResult,
        lowStockItemsResult,
        recentOrdersResult,
      ] = results;

      // Log individual API results
      console.log("=== INDIVIDUAL API RESULTS ===");
      results.forEach((result, index) => {
        const apiNames = [
          "Sales Overview", "Monthly Sales", "Top Products", "Sales by Category",
          "Customer Summary", "Customer Segments", "Website Traffic", "Conversion Funnel",
          "Shipping Metrics", "Alerts", "Inventory Status", "Low Stock Items", "Recent Orders"
        ];
        if (result.status === 'fulfilled') {
          console.log(`${apiNames[index]}: SUCCESS`, result.value);
        } else {
          console.log(`${apiNames[index]}: FAILED`, result.reason);
          // Log detailed error information
          if (result.reason?.response) {
            console.log(`${apiNames[index]} Error Details:`, {
              status: result.reason.response.status,
              statusText: result.reason.response.statusText,
              data: result.reason.response.data,
              url: result.reason.config?.url
            });
          }
        }
      });
      console.log("=== END INDIVIDUAL API RESULTS ===");

      // Check if we have any successful APIs
      const successfulAPIs = results.filter(result => result.status === 'fulfilled');
      const failedAPIs = results.filter(result => result.status === 'rejected');
      
      console.log(`Successful APIs: ${successfulAPIs.length}/${results.length}`);
      console.log(`Failed APIs: ${failedAPIs.length}/${results.length}`);
      
      if (failedAPIs.length > 0) {
        const failedNames = failedAPIs.map((_, index) => {
          const apiNames = [
            "Sales Overview", "Monthly Sales", "Top Products", "Sales by Category",
            "Customer Summary", "Customer Segments", "Website Traffic", "Conversion Funnel",
            "Shipping Metrics", "Alerts", "Inventory Status", "Low Stock Items", "Recent Orders"
          ];
          return apiNames[index];
        });
        console.log("Failed APIs:", failedNames);
        setError(`Some analytics failed to load: ${failedNames.join(', ')}. Showing available data.`);
      }

      // Extract data from successful results, use null for failed ones
      const salesOverview = salesOverviewResult.status === 'fulfilled' ? salesOverviewResult.value : null;
      const monthlySales = monthlySalesResult.status === 'fulfilled' ? monthlySalesResult.value : null;
      const topProducts = topProductsResult.status === 'fulfilled' ? topProductsResult.value : null;
      const salesByCategory = salesByCategoryResult.status === 'fulfilled' ? salesByCategoryResult.value : null;
      const customerSummary = customerSummaryResult.status === 'fulfilled' ? customerSummaryResult.value : null;
      const customerSegments = customerSegmentsResult.status === 'fulfilled' ? customerSegmentsResult.value : null;
      const websiteTrafficResp = websiteTrafficResult.status === 'fulfilled' ? websiteTrafficResult.value : null;
      const conversionFunnel = conversionFunnelResult.status === 'fulfilled' ? conversionFunnelResult.value : null;
      const shippingMetrics = shippingMetricsResult.status === 'fulfilled' ? shippingMetricsResult.value : null;
      const alerts = alertsResult.status === 'fulfilled' ? alertsResult.value : null;
      const inventoryStatus = inventoryStatusResult.status === 'fulfilled' ? inventoryStatusResult.value : null;
      const lowStockItems = lowStockItemsResult.status === 'fulfilled' ? lowStockItemsResult.value : null;
      const recentOrders = recentOrdersResult.status === 'fulfilled' ? recentOrdersResult.value : null;

      // Comprehensive API Response Logging
      console.log("=== ANALYTICS API RESPONSES ===");
      console.log("1. Sales Overview:", salesOverview);
      console.log("2. Monthly Sales:", monthlySales);
      console.log("3. Top Products:", topProducts);
      console.log("4. Sales by Category:", salesByCategory);
      console.log("5. Customer Summary:", customerSummary);
      console.log("6. Customer Segments:", customerSegments);
      console.log("7. Website Traffic:", websiteTrafficResp);
      console.log("8. Conversion Funnel:", conversionFunnel);
      console.log("9. Shipping Metrics:", shippingMetrics);
      console.log("10. Alerts:", alerts);
      console.log("11. Inventory Status:", inventoryStatus);
      console.log("12. Low Stock Items:", lowStockItems);
      console.log("13. Recent Orders:", recentOrders);
      console.log("=== END API RESPONSES ===");
      
      const processedAnalytics = {
        salesOverview: {
          totalRevenue: salesOverview ? Number(salesOverview.totalRevenue) : 0,
          totalOrders: salesOverview ? Number(salesOverview.totalOrders) : 0,
          averageOrderValue: salesOverview ? Number(salesOverview.averageOrderValue) : 0,
          conversionRate: salesOverview ? Number(salesOverview.conversionRate) : 0,
          revenueGrowth: salesOverview ? Number(salesOverview.revenueGrowth) : 0,
          ordersGrowth: salesOverview ? Number(salesOverview.ordersGrowth) : 0,
          avgOrderGrowth: salesOverview ? Number(salesOverview.avgOrderGrowth) : 0,
          conversionGrowth: salesOverview ? Number(salesOverview.conversionGrowth) : 0,
        },
        monthlySales: monthlySales || [],
        topSellingProducts: topProducts || [],
        salesByCategory: salesByCategory || [],
        customerAnalytics: {
          totalCustomers: customerSummary?.totalCustomers || 0,
          newCustomers: customerSummary?.newCustomers || 0,
          returningCustomers: customerSummary?.returningCustomers || 0,
          averageCustomerValue: customerSummary?.averageCustomerValue || 0,
          customerRetentionRate: customerSummary?.customerRetentionRate || 0,
          topCustomerSegments: customerSegments || [],
        },
        performanceMetrics: {
          websiteTraffic: websiteTrafficResp?.websiteTraffic || {
            totalVisits: 0,
            uniqueVisitors: 0,
            bounceRate: 0,
            avgSessionDuration: "0:00",
            pageViews: 0,
          },
          conversionFunnel: conversionFunnel || {
            visitors: 0,
            productViews: 0,
            addToCart: 0,
            checkout: 0,
            completed: 0,
          },
          shippingMetrics: shippingMetrics || {
            avgShippingTime: "0.0 days",
            onTimeDelivery: 0,
            shippingCost: 0,
            freeShippingThreshold: 0,
          },
        },
        inventoryStatus: inventoryStatus || {
          totalProducts: 0,
          inStock: 0,
          lowStock: 0,
          outOfStock: 0,
          totalValue: 0,
          lowStockThreshold: 10,
        },
        lowStockProducts: lowStockItems || [],
        recentOrders: recentOrders || [],
        alerts: alerts || [],
      };

      console.log("=== PROCESSED ANALYTICS OBJECT ===");
      console.log("Processed Analytics:", processedAnalytics);
      console.log("=== END PROCESSED ANALYTICS ===");

      setAnalytics(processedAnalytics);
    } catch (err) {
      console.error("Error fetching analytics:", err);
      setError("Failed to load analytics. Please try again.");
      
      // Set empty analytics instead of mock data
      setAnalytics({
        salesOverview: {
          totalRevenue: 0,
          totalOrders: 0,
          averageOrderValue: 0,
          conversionRate: 0,
          revenueGrowth: 0,
          ordersGrowth: 0,
          avgOrderGrowth: 0,
          conversionGrowth: 0,
        },
        monthlySales: [],
        topSellingProducts: [],
        salesByCategory: [],
        customerAnalytics: {
          totalCustomers: 0,
          newCustomers: 0,
          returningCustomers: 0,
          averageCustomerValue: 0,
          customerRetentionRate: 0,
          topCustomerSegments: [],
        },
        performanceMetrics: {
          websiteTraffic: {
            totalVisits: 0,
            uniqueVisitors: 0,
            bounceRate: 0,
            avgSessionDuration: "0:00",
            pageViews: 0,
          },
          conversionFunnel: {
            visitors: 0,
            productViews: 0,
            addToCart: 0,
            checkout: 0,
            completed: 0,
          },
          shippingMetrics: {
            avgShippingTime: "0.0 days",
            onTimeDelivery: 0,
            shippingCost: 0,
            freeShippingThreshold: 0,
          },
        },
        inventoryStatus: {
          totalProducts: 0,
          inStock: 0,
          lowStock: 0,
          outOfStock: 0,
          totalValue: 0,
          lowStockThreshold: 10,
        },
        lowStockProducts: [],
        recentOrders: [],
        alerts: [],
      });
    } finally {
      setLoading(false);
    }
  };

  // Robust admin role detection: supports ROLE_ADMIN, ADMIN, admin, etc.
  const isAdmin = !!(
    user &&
    typeof user.role === "string" &&
    user.role.replace(/^ROLE_/i, "").toLowerCase() === "admin"
  );

  // Redirect if not admin
  if (!user || !isAdmin) {
    return (
      <div className="admin-dashboard">
        <div className="access-denied">
          <h2>Access Denied</h2>
          <p>You need admin privileges to access this page.</p>
        </div>
      </div>
    );
  }

  // Debug logging for analytics state
  console.log("=== COMPONENT RENDER STATE ===");
  console.log("Loading:", loading);
  console.log("Analytics:", analytics);
  console.log("Error:", error);
  console.log("=== END RENDER STATE ===");

  // Show loading state
  if (loading || !analytics) {
    return (
      <div className="admin-dashboard">
        <div className="loading-state">
          <div className="loading-spinner"></div>
          <h2>Loading Dashboard...</h2>
          <p>Fetching real-time analytics data</p>
        </div>
      </div>
    );
  }

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
    }).format(amount);
  };

  const formatNumber = (num) => {
    return new Intl.NumberFormat("en-US").format(num);
  };

  const getGrowthIcon = (growth) => {
    if (growth > 0) return "📈";
    if (growth < 0) return "📉";
    return "➡️";
  };

  const getGrowthColor = (growth) => {
    if (growth > 0) return "#10b981";
    if (growth < 0) return "#ef4444";
    return "#6b7280";
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "Delivered":
        return "#10b981";
      case "Shipped":
        return "#3b82f6";
      case "Processing":
        return "#f59e0b";
      case "Pending":
        return "#6b7280";
      default:
        return "#6b7280";
    }
  };

  const getAlertIcon = (type) => {
    switch (type) {
      case "warning":
        return "⚠️";
      case "info":
        return "ℹ️";
      case "success":
        return "✅";
      case "error":
        return "❌";
      default:
        return "ℹ️";
    }
  };

  const getAlertColor = (type) => {
    switch (type) {
      case "warning":
        return "#f59e0b";
      case "info":
        return "#3b82f6";
      case "success":
        return "#10b981";
      case "error":
        return "#ef4444";
      default:
        return "#6b7280";
    }
  };

  return (
    <motion.div
      className="admin-dashboard"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 0.6 }}
    >
      {/* Header */}
      <div className="dashboard-header">
        <div className="header-content">
          <div className="header-left">
            <h1>Admin Dashboard</h1>
            <p>Welcome back, {user?.name}! Here's your business overview.</p>
          </div>
          <div className="header-right">
            <select
              value={selectedTimeRange}
              onChange={(e) => setSelectedTimeRange(e.target.value)}
              className="time-range-selector"
            >
              <option value="7d">Last 7 days</option>
              <option value="30d">Last 30 days</option>
              <option value="90d">Last 90 days</option>
              <option value="1y">Last year</option>
            </select>
          </div>
        </div>
      </div>

      {/* Navigation Tabs */}
      <div className="dashboard-nav">
        <button
          className={`nav-tab ${activeTab === "overview" ? "active" : ""}`}
          onClick={() => setActiveTab("overview")}
        >
          📊 Overview
        </button>
        <button
          className={`nav-tab ${activeTab === "inventory" ? "active" : ""}`}
          onClick={() => setActiveTab("inventory")}
        >
          📦 Inventory
        </button>
        <button
          className={`nav-tab ${activeTab === "orders" ? "active" : ""}`}
          onClick={() => setActiveTab("orders")}
        >
          🛒 Orders
        </button>
        <button
          className={`nav-tab ${activeTab === "analytics" ? "active" : ""}`}
          onClick={() => setActiveTab("analytics")}
        >
          📈 Analytics
        </button>
        <button
          className={`nav-tab ${activeTab === "alerts" ? "active" : ""}`}
          onClick={() => setActiveTab("alerts")}
        >
          🔔 Alerts
        </button>
      </div>

      {/* Overview Tab */}
      {activeTab === "overview" && (
        <motion.div
          className="tab-content"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
        >
          {/* Key Metrics Cards */}
          <div className="metrics-grid">
            {console.log("=== RENDERING METRICS ===")}
            {console.log("Sales Overview Data:", analytics?.salesOverview)}
            {console.log("Total Revenue:", analytics?.salesOverview?.totalRevenue)}
            {console.log("Total Orders:", analytics?.salesOverview?.totalOrders)}
            <motion.div
              className="metric-card revenue"
              whileHover={{ scale: 1.02 }}
              transition={{ type: "spring", stiffness: 300 }}
            >
              <div className="metric-icon">💰</div>
              <div className="metric-content">
                <h3>Total Revenue</h3>
                <div className="metric-value">
                  {formatCurrency(analytics?.salesOverview?.totalRevenue || 0)}
                </div>
                <div
                  className="metric-growth"
                  style={{
                    color: getGrowthColor(
                      analytics?.salesOverview?.revenueGrowth || 0
                    ),
                  }}
                >
                  {getGrowthIcon(analytics?.salesOverview?.revenueGrowth || 0)}{" "}
                  {analytics?.salesOverview?.revenueGrowth || 0}%
                </div>
              </div>
            </motion.div>

            <motion.div
              className="metric-card orders"
              whileHover={{ scale: 1.02 }}
              transition={{ type: "spring", stiffness: 300 }}
            >
              <div className="metric-icon">🛒</div>
              <div className="metric-content">
                <h3>Total Orders</h3>
                <div className="metric-value">
                  {formatNumber(analytics?.salesOverview?.totalOrders || 0)}
                </div>
                <div
                  className="metric-growth"
                  style={{
                    color: getGrowthColor(analytics?.salesOverview?.ordersGrowth || 0),
                  }}
                >
                  {getGrowthIcon(analytics?.salesOverview?.ordersGrowth || 0)}{" "}
                  {analytics?.salesOverview?.ordersGrowth || 0}%
                </div>
              </div>
            </motion.div>

            <motion.div
              className="metric-card avg-order"
              whileHover={{ scale: 1.02 }}
              transition={{ type: "spring", stiffness: 300 }}
            >
              <div className="metric-icon">📊</div>
              <div className="metric-content">
                <h3>Avg Order Value</h3>
                <div className="metric-value">
                  {formatCurrency(analytics?.salesOverview?.averageOrderValue || 0)}
                </div>
                <div
                  className="metric-growth"
                  style={{
                    color: getGrowthColor(
                      analytics?.salesOverview?.avgOrderGrowth || 0
                    ),
                  }}
                >
                  {getGrowthIcon(analytics?.salesOverview?.avgOrderGrowth || 0)}{" "}
                  {analytics?.salesOverview?.avgOrderGrowth || 0}%
                </div>
              </div>
            </motion.div>

            <motion.div
              className="metric-card conversion"
              whileHover={{ scale: 1.02 }}
              transition={{ type: "spring", stiffness: 300 }}
            >
              <div className="metric-icon">🎯</div>
              <div className="metric-content">
                <h3>Conversion Rate</h3>
                <div className="metric-value">
                  {analytics?.salesOverview?.conversionRate || 0}%
                </div>
                <div
                  className="metric-growth"
                  style={{
                    color: getGrowthColor(
                      analytics?.salesOverview?.conversionGrowth || 0
                    ),
                  }}
                >
                  {getGrowthIcon(analytics?.salesOverview?.conversionGrowth || 0)}{" "}
                  {analytics?.salesOverview?.conversionGrowth || 0}%
                </div>
              </div>
            </motion.div>
          </div>

          {/* Top Selling Products */}
          <div className="section">
            <h2>Top Selling Products</h2>
            <div className="products-grid">
              {(analytics?.topSellingProducts || []).length > 0 ? (
                (analytics?.topSellingProducts || []).map((product, index) => (
                <motion.div
                  key={product.id}
                  className="product-card"
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.1 }}
                  whileHover={{ scale: 1.02 }}
                >
                  <div className="product-image">
                    <img src={product.image} alt={product.name} />
                    <div className="product-rank">#{index + 1}</div>
                  </div>
                  <div className="product-info">
                    <h4>{product.name}</h4>
                    <p className="product-category">{product.category}</p>
                    <div className="product-stats">
                      <div className="stat">
                        <span className="stat-label">Units Sold:</span>
                        <span className="stat-value">
                          {formatNumber(product.unitsSold)}
                        </span>
                      </div>
                      <div className="stat">
                        <span className="stat-label">Revenue:</span>
                        <span className="stat-value">
                          {formatCurrency(product.revenue)}
                        </span>
                      </div>
                      <div className="stat">
                        <span className="stat-label">Profit:</span>
                        <span className="stat-value">
                          {formatCurrency(product.profit)}
                        </span>
                      </div>
                      <div className="stat">
                        <span className="stat-label">Stock:</span>
                        <span
                          className={`stat-value ${
                            product.stock < 10 ? "low-stock" : ""
                          }`}
                        >
                          {product.stock} units
                        </span>
                      </div>
                    </div>
                  </div>
                </motion.div>
              ))
              ) : (
                <div className="no-data-message">
                  <h3>No product data available</h3>
                  <p>There are no top selling products to display at this time.</p>
                </div>
              )}
            </div>
          </div>

          {/* Sales by Category */}
          <div className="section">
            <h2>Sales by Category</h2>
            <div className="category-chart">
              {(analytics?.salesByCategory || []).length > 0 ? (
                (analytics?.salesByCategory || []).map((category, index) => (
                <motion.div
                  key={category.category}
                  className="category-item"
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.1 }}
                >
                  <div className="category-header">
                    <span className="category-name">{category.category}</span>
                    <span className="category-percentage">
                      {category.percentage}%
                    </span>
                  </div>
                  <div className="category-bar">
                    <div
                      className="category-fill"
                      style={{ width: `${category.percentage}%` }}
                    ></div>
                  </div>
                  <div className="category-details">
                    <span>Revenue: {formatCurrency(category.revenue)}</span>
                    <span>Units: {formatNumber(category.unitsSold)}</span>
                    <span>Profit: {formatCurrency(category.profit)}</span>
                  </div>
                </motion.div>
              ))
              ) : (
                <div className="no-data-message">
                  <h3>No category data available</h3>
                  <p>There are no sales by category to display at this time.</p>
                </div>
              )}
            </div>
          </div>
        </motion.div>
      )}

      {/* Inventory Tab */}
      {activeTab === "inventory" && (
        <motion.div
          className="tab-content"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
        >
          {/* Inventory Overview */}
          <div className="inventory-overview">
            <div className="inventory-stats">
              <div className="stat-card">
                <h3>Total Products</h3>
                <div className="stat-value">
                  {analytics?.inventoryStatus?.totalProducts || 0}
                </div>
              </div>
              <div className="stat-card in-stock">
                <h3>In Stock</h3>
                <div className="stat-value">
                  {analytics?.inventoryStatus?.inStock || 0}
                </div>
              </div>
              <div className="stat-card low-stock">
                <h3>Low Stock</h3>
                <div className="stat-value">
                  {analytics?.inventoryStatus?.lowStock || 0}
                </div>
              </div>
              <div className="stat-card out-of-stock">
                <h3>Out of Stock</h3>
                <div className="stat-value">
                  {analytics?.inventoryStatus?.outOfStock || 0}
                </div>
              </div>
              <div className="stat-card">
                <h3>Total Value</h3>
                <div className="stat-value">
                  {formatCurrency(analytics?.inventoryStatus?.totalValue || 0)}
                </div>
              </div>
            </div>
          </div>

          {/* Low Stock Products */}
          <div className="section">
            <h2>Low Stock Alert</h2>
            <div className="low-stock-grid">
              {(analytics?.lowStockProducts || []).length > 0 ? (
                (analytics?.lowStockProducts || []).map((product, index) => (
                <motion.div
                  key={product.id}
                  className="low-stock-card"
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.1 }}
                  whileHover={{ scale: 1.02 }}
                >
                  <div className="product-image">
                    <img src={product.image} alt={product.name} />
                    <div className="stock-badge low">Low Stock</div>
                  </div>
                  <div className="product-info">
                    <h4>{product.name}</h4>
                    <p className="product-category">{product.category}</p>
                    <div className="stock-info">
                      <div className="stock-level">
                        <span>Current Stock: </span>
                        <span className="stock-count">
                          {product.currentStock}
                        </span>
                      </div>
                      <div className="stock-threshold">
                        <span>Min Threshold: </span>
                        <span>{product.minThreshold}</span>
                      </div>
                      <div className="supplier">
                        <span>Supplier: </span>
                        <span>{product.supplier}</span>
                      </div>
                      <div className="last-restocked">
                        <span>Last Restocked: </span>
                        <span>{product.lastRestocked}</span>
                      </div>
                    </div>
                    <button className="restock-btn">Restock Now</button>
                  </div>
                </motion.div>
              ))
              ) : (
                <div className="no-data-message">
                  <h3>No low stock items</h3>
                  <p>There are no products with low stock levels at this time.</p>
                </div>
              )}
            </div>
          </div>
        </motion.div>
      )}

      {/* Orders Tab */}
      {activeTab === "orders" && (
        <motion.div
          className="tab-content"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
        >
          <div className="section">
            <h2>Recent Orders</h2>
            <div className="orders-table">
              <div className="table-header">
                <div>Order ID</div>
                <div>Customer</div>
                <div>Total</div>
                <div>Status</div>
                <div>Date</div>
                <div>Items</div>
                <div>Payment</div>
                <div>Update Status</div>
              </div>
              {(analytics?.recentOrders || []).length > 0 ? (
                (analytics?.recentOrders || []).map((order, index) => (
                <motion.div
                  key={order.id}
                  className="table-row"
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.1 }}
                >
                  <div className="order-id">{order.id}</div>
                  <div className="customer">
                    <div className="customer-name">{order.customer}</div>
                    <div className="customer-email">{order.email}</div>
                  </div>
                  <div className="total">{formatCurrency(order.total)}</div>
                  <div className="status">
                    <span
                      className="status-badge"
                      style={{ backgroundColor: getStatusColor(order.status) }}
                    >
                      {order.status}
                    </span>
                  </div>
                  <div className="date">{order.date}</div>
                  <div className="items">{order.items} items</div>
                  <div className="payment">{order.paymentMethod}</div>
                  <div className="actions">
                    <select
                      className="status-select"
                      value={order.status}
                      onChange={async (e) => {
                        const newStatus = e.target.value;
                        try {
                          await orderService.updateOrderStatus(
                            order.id,
                            newStatus
                          );
                          // update UI state optimistically
                          setAnalytics((prev) => ({
                            ...prev,
                            recentOrders: prev.recentOrders.map((o) =>
                              o.id === order.id
                                ? { ...o, status: newStatus }
                                : o
                            ),
                          }));
                        } catch (err) {
                          console.error("Failed to update order status", err);
                          alert("Failed to update status. Please try again.");
                        }
                      }}
                    >
                      {[
                        "PENDING",
                        "CONFIRMED",
                        "SHIPPED",
                        "DELIVERED",
                        "CANCELLED",
                        // include current status if it's non-standard (e.g., PROCESSING)
                        order.status &&
                        ![
                          "PENDING",
                          "CONFIRMED",
                          "SHIPPED",
                          "DELIVERED",
                          "CANCELLED",
                        ].includes(order.status)
                          ? order.status
                          : null,
                      ]
                        .filter(Boolean)
                        .map((s) => (
                          <option key={s} value={s}>
                            {s}
                          </option>
                        ))}
                    </select>
                  </div>
                </motion.div>
              ))
              ) : (
                <div className="no-data-message">
                  <h3>No recent orders</h3>
                  <p>There are no recent orders to display at this time.</p>
                </div>
              )}
            </div>
          </div>
        </motion.div>
      )}

      {/* Analytics Tab */}
      {activeTab === "analytics" && (
        <motion.div
          className="tab-content"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
        >
          {/* Customer Analytics */}
          <div className="section">
            <h2>Customer Analytics</h2>
            <div className="customer-metrics">
              <div className="customer-stat">
                <h3>Total Customers</h3>
                <div className="stat-value">
                  {formatNumber(analytics?.customerAnalytics?.totalCustomers || 0)}
                </div>
              </div>
              <div className="customer-stat">
                <h3>New Customers</h3>
                <div className="stat-value">
                  {formatNumber(analytics?.customerAnalytics?.newCustomers || 0)}
                </div>
              </div>
              <div className="customer-stat">
                <h3>Returning Customers</h3>
                <div className="stat-value">
                  {formatNumber(analytics?.customerAnalytics?.returningCustomers || 0)}
                </div>
              </div>
              <div className="customer-stat">
                <h3>Avg Customer Value</h3>
                <div className="stat-value">
                  {formatCurrency(
                    analytics?.customerAnalytics?.averageCustomerValue || 0
                  )}
                </div>
              </div>
              <div className="customer-stat">
                <h3>Retention Rate</h3>
                <div className="stat-value">
                  {analytics?.customerAnalytics?.customerRetentionRate || 0}%
                </div>
              </div>
            </div>
          </div>

          {/* Performance Metrics */}
          <div className="section">
            <h2>Website Performance</h2>
            <div className="performance-grid">
              <div className="performance-card">
                <h3>Traffic</h3>
                <div className="performance-stats">
                  <div className="perf-stat">
                    <span>Total Visits:</span>
                    <span>
                      {formatNumber(
                        analytics?.performanceMetrics?.websiteTraffic?.totalVisits || 0
                      )}
                    </span>
                  </div>
                  <div className="perf-stat">
                    <span>Unique Visitors:</span>
                    <span>
                      {formatNumber(
                        analytics?.performanceMetrics?.websiteTraffic?.uniqueVisitors || 0
                      )}
                    </span>
                  </div>
                  <div className="perf-stat">
                    <span>Bounce Rate:</span>
                    <span>
                      {analytics?.performanceMetrics?.websiteTraffic?.bounceRate || 0}%
                    </span>
                  </div>
                  <div className="perf-stat">
                    <span>Avg Session:</span>
                    <span>
                      {
                        analytics?.performanceMetrics?.websiteTraffic?.avgSessionDuration || "0:00"
                      }
                    </span>
                  </div>
                </div>
              </div>

              <div className="performance-card">
                <h3>Conversion Funnel</h3>
                <div className="funnel-stats">
                  <div className="funnel-step">
                    <span>Visitors:</span>
                    <span>
                      {formatNumber(
                        analytics?.performanceMetrics?.conversionFunnel?.visitors || 0
                      )}
                    </span>
                  </div>
                  <div className="funnel-step">
                    <span>Product Views:</span>
                    <span>
                      {formatNumber(
                        analytics?.performanceMetrics?.conversionFunnel?.productViews || 0
                      )}
                    </span>
                  </div>
                  <div className="funnel-step">
                    <span>Add to Cart:</span>
                    <span>
                      {formatNumber(
                        analytics?.performanceMetrics?.conversionFunnel?.addToCart || 0
                      )}
                    </span>
                  </div>
                  <div className="funnel-step">
                    <span>Checkout:</span>
                    <span>
                      {formatNumber(
                        analytics?.performanceMetrics?.conversionFunnel?.checkout || 0
                      )}
                    </span>
                  </div>
                  <div className="funnel-step">
                    <span>Completed:</span>
                    <span>
                      {formatNumber(
                        analytics?.performanceMetrics?.conversionFunnel?.completed || 0
                      )}
                    </span>
                  </div>
                </div>
              </div>

              <div className="performance-card">
                <h3>Shipping</h3>
                <div className="shipping-stats">
                  <div className="ship-stat">
                    <span>Avg Shipping Time:</span>
                    <span>
                      {
                        analytics?.performanceMetrics?.shippingMetrics?.avgShippingTime || "0.0 days"
                      }
                    </span>
                  </div>
                  <div className="ship-stat">
                    <span>On-Time Delivery:</span>
                    <span>
                      {
                        analytics?.performanceMetrics?.shippingMetrics?.onTimeDelivery || 0
                      }
                      %
                    </span>
                  </div>
                  <div className="ship-stat">
                    <span>Shipping Cost:</span>
                    <span>
                      {formatCurrency(
                        analytics?.performanceMetrics?.shippingMetrics?.shippingCost || 0
                      )}
                    </span>
                  </div>
                  <div className="ship-stat">
                    <span>Free Shipping Threshold:</span>
                    <span>
                      {formatCurrency(
                        analytics?.performanceMetrics?.shippingMetrics?.freeShippingThreshold || 0
                      )}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </motion.div>
      )}

      {/* Alerts Tab */}
      {activeTab === "alerts" && (
        <motion.div
          className="tab-content"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
        >
          <div className="section">
            <h2>System Alerts & Notifications</h2>
            <div className="alerts-list">
              {(analytics?.alerts || []).length > 0 ? (
                (analytics?.alerts || []).map((alert, index) => (
                <motion.div
                  key={alert.id}
                  className={`alert-card ${alert.type}`}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.1 }}
                  whileHover={{ scale: 1.02 }}
                >
                  <div className="alert-icon">{getAlertIcon(alert.type)}</div>
                  <div className="alert-content">
                    <div className="alert-header">
                      <h4>{alert.title}</h4>
                      <span
                        className="alert-priority"
                        style={{ color: getAlertColor(alert.type) }}
                      >
                        {alert.priority.toUpperCase()}
                      </span>
                    </div>
                    <p className="alert-message">{alert.message}</p>
                    <div className="alert-timestamp">
                      {new Date(alert.timestamp).toLocaleString()}
                    </div>
                  </div>
                  {/* <div className="alert-actions">
                    <button className="alert-btn">Mark as Read</button>
                    <button className="alert-btn">Dismiss</button>
                  </div> */}
                </motion.div>
              ))
              ) : (
                <div className="no-data-message">
                  <h3>No alerts</h3>
                  <p>There are no system alerts or notifications at this time.</p>
                </div>
              )}
            </div>
          </div>
        </motion.div>
      )}
    </motion.div>
  );
};

export default AdminDashboard;
