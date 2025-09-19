"use client";

import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { useAuth } from "../context/AuthContext";
import { adminAnalytics } from "../data/adminAnalytics";
import { adminService } from "../services/adminService";
import { sweetService } from "../services/sweetService";
import { orderService } from "../services/orderService";
import "./AdminDashboard.css";

const AdminDashboard = () => {
  const { user } = useAuth();
  const [selectedTimeRange, setSelectedTimeRange] = useState("30d");
  const [activeTab, setActiveTab] = useState("overview");
  const [analytics, setAnalytics] = useState(adminAnalytics);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [realTimeData, setRealTimeData] = useState({
    dashboardStats: null,
    systemStatus: null,
    recentOrders: [],
  });

  // Fetch real-time data on component mount
  useEffect(() => {
    fetchRealTimeData();
  }, []);

  const fetchRealTimeData = async () => {
    try {
      setLoading(true);
      setError("");

      const [dashboardStats, systemStatus, recentOrders] = await Promise.all([
        adminService.getDashboardStats().catch(() => null),
        adminService.getSystemStatus().catch(() => null),
        orderService.getUserOrders().catch(() => []),
      ]);

      setRealTimeData({
        dashboardStats,
        systemStatus,
        recentOrders,
      });
    } catch (err) {
      console.error("Error fetching real-time data:", err);
      setError("Failed to load some dashboard data. Using mock data.");
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
            <motion.div
              className="metric-card revenue"
              whileHover={{ scale: 1.02 }}
              transition={{ type: "spring", stiffness: 300 }}
            >
              <div className="metric-icon">💰</div>
              <div className="metric-content">
                <h3>Total Revenue</h3>
                <div className="metric-value">
                  {formatCurrency(analytics.salesOverview.totalRevenue)}
                </div>
                <div
                  className="metric-growth"
                  style={{
                    color: getGrowthColor(
                      analytics.salesOverview.revenueGrowth
                    ),
                  }}
                >
                  {getGrowthIcon(analytics.salesOverview.revenueGrowth)}{" "}
                  {analytics.salesOverview.revenueGrowth}%
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
                  {formatNumber(analytics.salesOverview.totalOrders)}
                </div>
                <div
                  className="metric-growth"
                  style={{
                    color: getGrowthColor(analytics.salesOverview.ordersGrowth),
                  }}
                >
                  {getGrowthIcon(analytics.salesOverview.ordersGrowth)}{" "}
                  {analytics.salesOverview.ordersGrowth}%
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
                  {formatCurrency(analytics.salesOverview.averageOrderValue)}
                </div>
                <div
                  className="metric-growth"
                  style={{
                    color: getGrowthColor(
                      analytics.salesOverview.avgOrderGrowth
                    ),
                  }}
                >
                  {getGrowthIcon(analytics.salesOverview.avgOrderGrowth)}{" "}
                  {analytics.salesOverview.avgOrderGrowth}%
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
                  {analytics.salesOverview.conversionRate}%
                </div>
                <div
                  className="metric-growth"
                  style={{
                    color: getGrowthColor(
                      analytics.salesOverview.conversionGrowth
                    ),
                  }}
                >
                  {getGrowthIcon(analytics.salesOverview.conversionGrowth)}{" "}
                  {analytics.salesOverview.conversionGrowth}%
                </div>
              </div>
            </motion.div>
          </div>

          {/* Top Selling Products */}
          <div className="section">
            <h2>Top Selling Products</h2>
            <div className="products-grid">
              {analytics.topSellingProducts.map((product, index) => (
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
              ))}
            </div>
          </div>

          {/* Sales by Category */}
          <div className="section">
            <h2>Sales by Category</h2>
            <div className="category-chart">
              {analytics.salesByCategory.map((category, index) => (
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
              ))}
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
                  {analytics.inventoryStatus.totalProducts}
                </div>
              </div>
              <div className="stat-card in-stock">
                <h3>In Stock</h3>
                <div className="stat-value">
                  {analytics.inventoryStatus.inStock}
                </div>
              </div>
              <div className="stat-card low-stock">
                <h3>Low Stock</h3>
                <div className="stat-value">
                  {analytics.inventoryStatus.lowStock}
                </div>
              </div>
              <div className="stat-card out-of-stock">
                <h3>Out of Stock</h3>
                <div className="stat-value">
                  {analytics.inventoryStatus.outOfStock}
                </div>
              </div>
              <div className="stat-card">
                <h3>Total Value</h3>
                <div className="stat-value">
                  {formatCurrency(analytics.inventoryStatus.totalValue)}
                </div>
              </div>
            </div>
          </div>

          {/* Low Stock Products */}
          <div className="section">
            <h2>Low Stock Alert</h2>
            <div className="low-stock-grid">
              {analytics.lowStockProducts.map((product, index) => (
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
              ))}
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
                <div>Actions</div>
              </div>
              {analytics.recentOrders.map((order, index) => (
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
                    <button className="action-btn view">View</button>
                    <button className="action-btn edit">Edit</button>
                  </div>
                </motion.div>
              ))}
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
                  {formatNumber(analytics.customerAnalytics.totalCustomers)}
                </div>
              </div>
              <div className="customer-stat">
                <h3>New Customers</h3>
                <div className="stat-value">
                  {formatNumber(analytics.customerAnalytics.newCustomers)}
                </div>
              </div>
              <div className="customer-stat">
                <h3>Returning Customers</h3>
                <div className="stat-value">
                  {formatNumber(analytics.customerAnalytics.returningCustomers)}
                </div>
              </div>
              <div className="customer-stat">
                <h3>Avg Customer Value</h3>
                <div className="stat-value">
                  {formatCurrency(
                    analytics.customerAnalytics.averageCustomerValue
                  )}
                </div>
              </div>
              <div className="customer-stat">
                <h3>Retention Rate</h3>
                <div className="stat-value">
                  {analytics.customerAnalytics.customerRetentionRate}%
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
                        analytics.performanceMetrics.websiteTraffic.totalVisits
                      )}
                    </span>
                  </div>
                  <div className="perf-stat">
                    <span>Unique Visitors:</span>
                    <span>
                      {formatNumber(
                        analytics.performanceMetrics.websiteTraffic
                          .uniqueVisitors
                      )}
                    </span>
                  </div>
                  <div className="perf-stat">
                    <span>Bounce Rate:</span>
                    <span>
                      {analytics.performanceMetrics.websiteTraffic.bounceRate}%
                    </span>
                  </div>
                  <div className="perf-stat">
                    <span>Avg Session:</span>
                    <span>
                      {
                        analytics.performanceMetrics.websiteTraffic
                          .avgSessionDuration
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
                        analytics.performanceMetrics.conversionFunnel.visitors
                      )}
                    </span>
                  </div>
                  <div className="funnel-step">
                    <span>Product Views:</span>
                    <span>
                      {formatNumber(
                        analytics.performanceMetrics.conversionFunnel
                          .productViews
                      )}
                    </span>
                  </div>
                  <div className="funnel-step">
                    <span>Add to Cart:</span>
                    <span>
                      {formatNumber(
                        analytics.performanceMetrics.conversionFunnel.addToCart
                      )}
                    </span>
                  </div>
                  <div className="funnel-step">
                    <span>Checkout:</span>
                    <span>
                      {formatNumber(
                        analytics.performanceMetrics.conversionFunnel.checkout
                      )}
                    </span>
                  </div>
                  <div className="funnel-step">
                    <span>Completed:</span>
                    <span>
                      {formatNumber(
                        analytics.performanceMetrics.conversionFunnel.completed
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
                        analytics.performanceMetrics.shippingMetrics
                          .avgShippingTime
                      }
                    </span>
                  </div>
                  <div className="ship-stat">
                    <span>On-Time Delivery:</span>
                    <span>
                      {
                        analytics.performanceMetrics.shippingMetrics
                          .onTimeDelivery
                      }
                      %
                    </span>
                  </div>
                  <div className="ship-stat">
                    <span>Shipping Cost:</span>
                    <span>
                      {formatCurrency(
                        analytics.performanceMetrics.shippingMetrics
                          .shippingCost
                      )}
                    </span>
                  </div>
                  <div className="ship-stat">
                    <span>Free Shipping Threshold:</span>
                    <span>
                      {formatCurrency(
                        analytics.performanceMetrics.shippingMetrics
                          .freeShippingThreshold
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
              {analytics.alerts.map((alert, index) => (
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
              ))}
            </div>
          </div>
        </motion.div>
      )}
    </motion.div>
  );
};

export default AdminDashboard;
