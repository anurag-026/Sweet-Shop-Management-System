package com.anurag.SweetShopBackend.service;

import com.anurag.SweetShopBackend.model.*;
import com.anurag.SweetShopBackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private SweetRepository sweetRepository;
    
    @Autowired
    private ProductAnalyticsRepository productAnalyticsRepository;
    
    @Autowired
    private CustomerAnalyticsRepository customerAnalyticsRepository;
    
    @Autowired
    private WebsiteTrafficRepository websiteTrafficRepository;
    
    @Autowired
    private ConversionEventRepository conversionEventRepository;
    
    @Autowired
    private ShippingAnalyticsRepository shippingAnalyticsRepository;
    
    @Autowired
    private OrderAnalyticsRepository orderAnalyticsRepository;
    
    @Autowired
    private MonthlySalesRepository monthlySalesRepository;
    
    @Autowired
    private CategorySalesRepository categorySalesRepository;
    
    @Autowired
    private SystemAlertRepository systemAlertRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    public Map<String, Object> getSalesOverview(String range) {
        Map<String, Object> result = new HashMap<>();
        
        // Parse date range
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = getStartDateFromRange(endDate, range);
        
        // Previous period for comparison
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        LocalDateTime prevEndDate = startDate;
        LocalDateTime prevStartDate = prevEndDate.minusDays(daysBetween);
        
        // Get orders in the current period
        List<Order> currentOrders = orderRepository.findByOrderDateBetween(startDate, endDate);
        List<Order> previousOrders = orderRepository.findByOrderDateBetween(prevStartDate, prevEndDate);
        
        // Calculate metrics
        double totalRevenue = currentOrders.stream().mapToDouble(Order::getTotalAmount).sum();
        int totalOrders = currentOrders.size();
        double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;
        
        // Previous period metrics
        double prevTotalRevenue = previousOrders.stream().mapToDouble(Order::getTotalAmount).sum();
        int prevTotalOrders = previousOrders.size();
        double prevAverageOrderValue = prevTotalOrders > 0 ? prevTotalRevenue / prevTotalOrders : 0;
        
        // Calculate growth percentages
        double revenueGrowth = calculateGrowthPercentage(prevTotalRevenue, totalRevenue);
        double ordersGrowth = calculateGrowthPercentage(prevTotalOrders, totalOrders);
        double avgOrderGrowth = calculateGrowthPercentage(prevAverageOrderValue, averageOrderValue);
        
        // Get conversion rate from ConversionEvent data
        double conversionRate = calculateConversionRate(startDate, endDate);
        double prevConversionRate = calculateConversionRate(prevStartDate, prevEndDate);
        double conversionGrowth = calculateGrowthPercentage(prevConversionRate, conversionRate);
        
        // Populate result
        result.put("range", range);
        result.put("totalRevenue", totalRevenue);
        result.put("totalOrders", totalOrders);
        result.put("averageOrderValue", averageOrderValue);
        result.put("conversionRate", conversionRate);
        result.put("revenueGrowth", revenueGrowth);
        result.put("ordersGrowth", ordersGrowth);
        result.put("avgOrderGrowth", avgOrderGrowth);
        result.put("conversionGrowth", conversionGrowth);
        
        return result;
    }
    
    public List<Map<String, Object>> getMonthlySales(int months) {
        // Get data from MonthlySales table if available
        Pageable pageable = PageRequest.of(0, months);
        List<MonthlySales> monthlySalesData = monthlySalesRepository.findRecentMonths(pageable);
        
        // If data is available, convert to required format
        if (!monthlySalesData.isEmpty()) {
            return monthlySalesData.stream().map(ms -> {
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", getMonthYearString(ms.getMonth(), ms.getYear()));
                monthData.put("revenue", ms.getTotalRevenue());
                monthData.put("orders", ms.getTotalOrders());
                monthData.put("growth", ms.getGrowthPercentage());
                return monthData;
            }).collect(Collectors.toList());
        }
        
        // Fallback to calculating from orders table
        return calculateMonthlySalesFromOrders(months);
    }
    
    public List<Map<String, Object>> getTopProducts(int limit) {
        // Get current month's data
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        
        List<ProductAnalytics> topProducts = productAnalyticsRepository.findTopProductsByRevenue("monthly", firstDayOfMonth);
        
        // If data is available, convert to required format
        if (!topProducts.isEmpty()) {
            return topProducts.stream().limit(limit).map(pa -> {
                Map<String, Object> product = new HashMap<>();
                product.put("id", pa.getSweet().getId().toString());
                product.put("name", pa.getSweet().getName());
                product.put("category", pa.getSweet().getCategory());
                product.put("unitsSold", pa.getUnitsSold());
                product.put("revenue", pa.getRevenue());
                product.put("profit", pa.getProfit());
                product.put("profitMargin", pa.getSweet().getProfitMargin());
                product.put("stock", pa.getSweet().getQuantity());
                product.put("image", pa.getSweet().getImage());
                return product;
            }).collect(Collectors.toList());
        }
        
        // Fallback to calculating from orders
        return calculateTopProductsFromOrders(limit);
    }
    
    public List<Map<String, Object>> getSalesByCategory(String range) {
        // Parse date range
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = getStartDateFromRange(endDate, range);
        LocalDate startLocalDate = startDate.toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDate();
        
        // Try to get data from CategorySales table
        List<CategorySales> categorySalesData = categorySalesRepository.findByTimePeriodAndPeriodStartBetweenOrderByRevenueDesc(
            getPeriodTypeFromRange(range), startLocalDate, endLocalDate);
        
        // If data is available, convert to required format
        if (!categorySalesData.isEmpty()) {
            return categorySalesData.stream().map(cs -> {
                Map<String, Object> category = new HashMap<>();
                category.put("category", cs.getCategory());
                category.put("revenue", cs.getRevenue());
                category.put("percentage", cs.getPercentage());
                category.put("unitsSold", cs.getUnitsSold());
                category.put("profit", cs.getProfit());
                return category;
            }).collect(Collectors.toList());
        }
        
        // Fallback to calculating from orders
        return calculateSalesByCategoryFromOrders(startDate, endDate);
    }
    
    public Map<String, Object> getCustomerSummary(String range) {
        Map<String, Object> result = new HashMap<>();
        
        // Get total customers
        Long totalCustomers = customerAnalyticsRepository.countTotalCustomers();
        if (totalCustomers == null) totalCustomers = 0L;
        
        // Parse date range for new customers
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = getStartDateFromRange(endDate, range);
        
        // Get new customers in the period
        Long newCustomers = customerAnalyticsRepository.countNewCustomers(startDate);
        if (newCustomers == null) newCustomers = 0L;
        
        // Get returning customers
        Long returningCustomers = customerAnalyticsRepository.countReturningCustomers();
        if (returningCustomers == null) returningCustomers = 0L;
        
        // Get average customer value
        Double averageCustomerValue = customerAnalyticsRepository.getAverageCustomerValue();
        if (averageCustomerValue == null) averageCustomerValue = 0.0;
        
        // Get customer retention rate
        Double customerRetentionRate = customerAnalyticsRepository.getCustomerRetentionRate();
        if (customerRetentionRate == null) customerRetentionRate = 0.0;
        
        // Populate result
        result.put("totalCustomers", totalCustomers);
        result.put("newCustomers", newCustomers);
        result.put("returningCustomers", returningCustomers);
        result.put("averageCustomerValue", averageCustomerValue);
        result.put("customerRetentionRate", customerRetentionRate);
        
        return result;
    }
    
    public List<Map<String, Object>> getCustomerSegments(String range) {
        // Parse date range
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = getStartDateFromRange(endDate, range);
        
        // Get all orders in the date range
        List<Order> orders = orderRepository.findByOrderDateBetween(startDate, endDate);
        
        if (orders.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Calculate customer segments based on order frequency and value
        Map<String, Map<String, Object>> customerStats = new HashMap<>();
        
        for (Order order : orders) {
            String userEmail = order.getUser().getEmail();
            double orderValue = order.getTotalAmount();
            
            if (customerStats.containsKey(userEmail)) {
                Map<String, Object> stats = customerStats.get(userEmail);
                stats.put("orderCount", (Integer) stats.get("orderCount") + 1);
                stats.put("totalValue", (Double) stats.get("totalValue") + orderValue);
            } else {
                Map<String, Object> stats = new HashMap<>();
                stats.put("email", userEmail);
                stats.put("orderCount", 1);
                stats.put("totalValue", orderValue);
                customerStats.put(userEmail, stats);
            }
        }
        
        // Categorize customers into segments
        List<Map<String, Object>> segments = new ArrayList<>();
        Map<String, Integer> segmentCounts = new HashMap<>();
        Map<String, Double> segmentValues = new HashMap<>();
        
        for (Map<String, Object> stats : customerStats.values()) {
            int orderCount = (Integer) stats.get("orderCount");
            double totalValue = (Double) stats.get("totalValue");
            double avgOrderValue = totalValue / orderCount;
            
            String segment;
            if (orderCount >= 5 && avgOrderValue >= 100) {
                segment = "Premium Buyers";
            } else if (orderCount >= 3 || avgOrderValue >= 50) {
                segment = "Regular Customers";
            } else {
                segment = "Occasional Buyers";
            }
            
            segmentCounts.put(segment, segmentCounts.getOrDefault(segment, 0) + 1);
            segmentValues.put(segment, segmentValues.getOrDefault(segment, 0.0) + totalValue);
        }
        
        // Create segment summaries
        for (String segment : segmentCounts.keySet()) {
            Map<String, Object> segmentData = new HashMap<>();
            segmentData.put("segment", segment);
            segmentData.put("count", segmentCounts.get(segment));
            segmentData.put("avgOrderValue", segmentValues.get(segment) / segmentCounts.get(segment));
            segments.add(segmentData);
        }
        
        return segments.stream()
            .sorted((a, b) -> Integer.compare((Integer) b.get("count"), (Integer) a.get("count")))
            .collect(Collectors.toList());
    }
    
    public Map<String, Object> getWebsiteTraffic(String range) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> traffic = new HashMap<>();
        
        // Parse date range
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = getStartDateFromRange(endDate, range);
        LocalDate startLocalDate = startDate.toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDate();
        
        // Get traffic metrics
        Long totalVisits = websiteTrafficRepository.countTotalVisits(startLocalDate, endLocalDate);
        if (totalVisits == null) totalVisits = 0L;
        
        Long uniqueVisitors = websiteTrafficRepository.countUniqueVisitors(startLocalDate, endLocalDate);
        if (uniqueVisitors == null) uniqueVisitors = 0L;
        
        Double bounceRate = websiteTrafficRepository.getBounceRate(startLocalDate, endLocalDate);
        if (bounceRate == null) bounceRate = 0.0;
        
        Double avgSessionDuration = websiteTrafficRepository.getAvgSessionDuration(startLocalDate, endLocalDate);
        if (avgSessionDuration == null) avgSessionDuration = 0.0;
        
        Long pageViews = websiteTrafficRepository.getTotalPageViews(startLocalDate, endLocalDate);
        if (pageViews == null) pageViews = 0L;
        
        // Format session duration
        String formattedDuration = formatSessionDuration(avgSessionDuration);
        
        // Populate result
        traffic.put("totalVisits", totalVisits);
        traffic.put("uniqueVisitors", uniqueVisitors);
        traffic.put("bounceRate", bounceRate);
        traffic.put("avgSessionDuration", formattedDuration);
        traffic.put("pageViews", pageViews);
        
        result.put("websiteTraffic", traffic);
        return result;
    }
    
    public Map<String, Object> getConversionFunnel(String range) {
        Map<String, Object> result = new HashMap<>();
        
        // Parse date range
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = getStartDateFromRange(endDate, range);
        
        // Get conversion funnel data
        List<Object[]> funnelData = conversionEventRepository.getConversionFunnel(startDate, endDate);
        
        // Initialize with zeros
        long visitors = 0;
        long productViews = 0;
        long addToCart = 0;
        long checkout = 0;
        long completed = 0;
        
        // Process funnel data
        if (funnelData != null) {
            for (Object[] data : funnelData) {
                String eventType = (String) data[0];
                Long count = (Long) data[1];
                
                switch (eventType) {
                    case "visit":
                        visitors = count;
                        break;
                    case "product_view":
                        productViews = count;
                        break;
                    case "add_to_cart":
                        addToCart = count;
                        break;
                    case "checkout":
                        checkout = count;
                        break;
                    case "purchase":
                        completed = count;
                        break;
                }
            }
        }
        
        // If no visit data, get from website traffic
        if (visitors == 0) {
            LocalDate startLocalDate = startDate.toLocalDate();
            LocalDate endLocalDate = endDate.toLocalDate();
            Long totalVisits = websiteTrafficRepository.countTotalVisits(startLocalDate, endLocalDate);
            if (totalVisits != null) visitors = totalVisits;
        }
        
        // Populate result
        result.put("visitors", visitors);
        result.put("productViews", productViews);
        result.put("addToCart", addToCart);
        result.put("checkout", checkout);
        result.put("completed", completed);
        
        return result;
    }
    
    public Map<String, Object> getShippingMetrics(String range) {
        Map<String, Object> result = new HashMap<>();
        
        // Parse date range
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = getStartDateFromRange(endDate, range);
        
        // Get shipping metrics
        Double avgShippingTime = shippingAnalyticsRepository.getAverageShippingTime(startDate, endDate);
        if (avgShippingTime == null) avgShippingTime = 0.0;
        
        Double onTimeDelivery = shippingAnalyticsRepository.getOnTimeDeliveryRate(startDate, endDate);
        if (onTimeDelivery == null) onTimeDelivery = 0.0;
        
        Double shippingCost = shippingAnalyticsRepository.getAverageShippingCost(startDate, endDate);
        if (shippingCost == null) shippingCost = 0.0;
        
        // Populate result
        result.put("avgShippingTime", String.format("%.1f days", avgShippingTime));
        result.put("onTimeDelivery", onTimeDelivery);
        result.put("shippingCost", shippingCost);
        result.put("freeShippingThreshold", 75.0); // This could be from a configuration
        
        return result;
    }
    
    public List<Map<String, Object>> getAlerts(int limit) {
        List<Map<String, Object>> alerts = new ArrayList<>();
        
        // Check for low stock items
        List<Sweet> lowStockItems = sweetRepository.findByQuantityBetween(1, 10);
        if (!lowStockItems.isEmpty()) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("id", "low-stock-" + System.currentTimeMillis());
            alert.put("type", "warning");
            alert.put("title", "Low Stock Alert");
            alert.put("message", lowStockItems.size() + " products are running low on stock");
            alert.put("timestamp", LocalDateTime.now().toString());
            alert.put("priority", "high");
            alerts.add(alert);
        }
        
        // Check for out of stock items
        List<Sweet> outOfStockItems = sweetRepository.findByQuantity(0);
        if (!outOfStockItems.isEmpty()) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("id", "out-of-stock-" + System.currentTimeMillis());
            alert.put("type", "error");
            alert.put("title", "Out of Stock Alert");
            alert.put("message", outOfStockItems.size() + " products are out of stock");
            alert.put("timestamp", LocalDateTime.now().toString());
            alert.put("priority", "high");
            alerts.add(alert);
        }
        
        // Check for recent orders
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Order> recentOrders = orderRepository.findByOrderDateBetween(oneHourAgo, LocalDateTime.now());
        if (!recentOrders.isEmpty()) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("id", "new-orders-" + System.currentTimeMillis());
            alert.put("type", "info");
            alert.put("title", "New Orders Received");
            alert.put("message", recentOrders.size() + " new orders received in the last hour");
            alert.put("timestamp", LocalDateTime.now().toString());
            alert.put("priority", "medium");
            alerts.add(alert);
        }
        
        // Check for high-value orders
        List<Order> highValueOrders = orderRepository.findByOrderDateBetween(
            LocalDateTime.now().minusDays(1), LocalDateTime.now())
            .stream()
            .filter(order -> order.getTotalAmount() > 500)
            .collect(Collectors.toList());
        
        if (!highValueOrders.isEmpty()) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("id", "high-value-" + System.currentTimeMillis());
            alert.put("type", "success");
            alert.put("title", "High Value Orders");
            alert.put("message", highValueOrders.size() + " high-value orders (>$500) received today");
            alert.put("timestamp", LocalDateTime.now().toString());
            alert.put("priority", "low");
            alerts.add(alert);
        }
        
        return alerts.stream().limit(limit).collect(Collectors.toList());
    }
    
    public Map<String, Object> getInventoryStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // Count total products
        long totalProducts = sweetRepository.count();
        
        // Count products by stock status
        long inStock = sweetRepository.countByQuantityGreaterThan(10);
        long lowStock = sweetRepository.countByQuantityBetween(1, 10);
        long outOfStock = sweetRepository.countByQuantity(0);
        
        // Calculate total inventory value
        Double totalValue = sweetRepository.calculateTotalInventoryValue();
        if (totalValue == null) totalValue = 0.0;
        
        // Populate result
        status.put("totalProducts", totalProducts);
        status.put("inStock", inStock);
        status.put("lowStock", lowStock);
        status.put("outOfStock", outOfStock);
        status.put("totalValue", totalValue);
        status.put("lowStockThreshold", 10);
        
        return status;
    }
    
    public List<Map<String, Object>> getLowStockItems(int threshold) {
        // Get low stock products
        List<Sweet> lowStockSweets = sweetRepository.findByQuantityLessThan(threshold);
        
        if (lowStockSweets != null && !lowStockSweets.isEmpty()) {
            return lowStockSweets.stream().map(sweet -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", sweet.getId().toString());
                item.put("name", sweet.getName());
                item.put("category", sweet.getCategory());
                item.put("currentStock", sweet.getQuantity());
                item.put("minThreshold", sweet.getMinThreshold() != null ? sweet.getMinThreshold() : threshold);
                item.put("lastRestocked", sweet.getLastRestocked() != null ? 
                    sweet.getLastRestocked().toLocalDate().toString() : "N/A");
                item.put("supplier", sweet.getSupplier() != null ? sweet.getSupplier().getName() : "N/A");
                item.put("image", sweet.getImage());
                return item;
            }).collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
    
    // Helper methods
    
    private LocalDateTime getStartDateFromRange(LocalDateTime endDate, String range) {
        switch (range) {
            case "7d":
                return endDate.minusDays(7);
            case "30d":
                return endDate.minusDays(30);
            case "90d":
                return endDate.minusDays(90);
            case "1y":
                return endDate.minusYears(1);
            default:
                return endDate.minusDays(30); // Default to 30 days
        }
    }
    
    private String getPeriodTypeFromRange(String range) {
        switch (range) {
            case "7d":
                return "daily";
            case "30d":
            case "90d":
                return "weekly";
            case "1y":
                return "monthly";
            default:
                return "weekly"; // Default to weekly
        }
    }
    
    private double calculateGrowthPercentage(double previous, double current) {
        if (previous == 0) return current > 0 ? 100 : 0;
        return Math.round((current - previous) / previous * 100 * 10) / 10.0;
    }
    
    private double calculateConversionRate(LocalDateTime startDate, LocalDateTime endDate) {
        Long visits = conversionEventRepository.countEventsByType("visit", startDate, endDate);
        Long purchases = conversionEventRepository.countEventsByType("purchase", startDate, endDate);
        
        if (visits == null || visits == 0) return 0;
        if (purchases == null) purchases = 0L;
        
        return Math.round((double) purchases / visits * 100 * 10) / 10.0;
    }
    
    private String getMonthYearString(int month, int year) {
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                             "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return monthNames[month - 1] + " " + year;
    }
    
    private String formatSessionDuration(double seconds) {
        int minutes = (int) (seconds / 60);
        int remainingSeconds = (int) (seconds % 60);
        return minutes + ":" + (remainingSeconds < 10 ? "0" : "") + remainingSeconds;
    }
    
    // Fallback methods when data is not in analytics tables
    
    private List<Map<String, Object>> calculateMonthlySalesFromOrders(int months) {
        List<Map<String, Object>> monthlySales = new ArrayList<>();
        
        // Get monthly sales data from orders
        List<Object[]> monthlyData = orderRepository.getMonthlySalesData();
        
        // Process the data and limit to requested months
        int count = 0;
        for (Object[] data : monthlyData) {
            if (count >= months) break;
            
            Integer year = (Integer) data[0];
            Integer month = (Integer) data[1];
            Long orderCount = (Long) data[2];
            Double totalRevenue = (Double) data[3];
            
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", getMonthYearString(month, year));
            monthData.put("revenue", totalRevenue != null ? totalRevenue : 0.0);
            monthData.put("orders", orderCount != null ? orderCount : 0);
            
            // Calculate growth percentage (simplified - would need previous month data)
            monthData.put("growth", 0.0);
            
            monthlySales.add(monthData);
            count++;
        }
        
        return monthlySales;
    }
    
    private List<Map<String, Object>> calculateTopProductsFromOrders(int limit) {
        List<Map<String, Object>> topProducts = new ArrayList<>();
        
        // Get current month's data
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDateTime startDate = firstDayOfMonth.atStartOfDay();
        LocalDateTime endDate = LocalDateTime.now();
        
        // Get all orders in the current month
        List<Order> orders = orderRepository.findByOrderDateBetween(startDate, endDate);
        
        // Calculate product sales from order items
        Map<UUID, Map<String, Object>> productStats = new HashMap<>();
        
        for (Order order : orders) {
            List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
            for (OrderItem item : orderItems) {
                UUID sweetId = item.getSweet().getId();
                double revenue = item.getPrice() * item.getQuantity();
                double profit = 0.0;
                
                // Calculate profit if cost is available
                if (item.getSweet().getCost() != null) {
                    profit = revenue - (item.getSweet().getCost() * item.getQuantity());
                }
                
                if (productStats.containsKey(sweetId)) {
                    Map<String, Object> stats = productStats.get(sweetId);
                    stats.put("unitsSold", (Integer) stats.get("unitsSold") + item.getQuantity());
                    stats.put("revenue", (Double) stats.get("revenue") + revenue);
                    stats.put("profit", (Double) stats.get("profit") + profit);
                } else {
                    Map<String, Object> stats = new HashMap<>();
                    stats.put("sweet", item.getSweet());
                    stats.put("unitsSold", item.getQuantity());
                    stats.put("revenue", revenue);
                    stats.put("profit", profit);
                    productStats.put(sweetId, stats);
                }
            }
        }
        
        // Convert to list and sort by revenue
        topProducts = productStats.values().stream()
            .map(stats -> {
                Sweet sweet = (Sweet) stats.get("sweet");
                Map<String, Object> product = new HashMap<>();
                product.put("id", sweet.getId().toString());
                product.put("name", sweet.getName());
                product.put("category", sweet.getCategory());
                product.put("unitsSold", stats.get("unitsSold"));
                product.put("revenue", stats.get("revenue"));
                product.put("profit", stats.get("profit"));
                product.put("profitMargin", sweet.getProfitMargin());
                product.put("stock", sweet.getQuantity());
                product.put("image", sweet.getImage());
                return product;
            })
            .sorted((a, b) -> Double.compare((Double) b.get("revenue"), (Double) a.get("revenue")))
            .limit(limit)
            .collect(Collectors.toList());
        
        return topProducts;
    }
    
    private List<Map<String, Object>> calculateSalesByCategoryFromOrders(LocalDateTime startDate, LocalDateTime endDate) {
        List<Map<String, Object>> salesByCategory = new ArrayList<>();
        
        // Get all orders in the date range
        List<Order> orders = orderRepository.findByOrderDateBetween(startDate, endDate);
        
        // Calculate category sales from order items
        Map<String, Map<String, Object>> categoryStats = new HashMap<>();
        
        for (Order order : orders) {
            List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
            for (OrderItem item : orderItems) {
                String category = item.getSweet().getCategory();
                if (category == null || category.isEmpty()) {
                    category = "Uncategorized";
                }
                
                double revenue = item.getPrice() * item.getQuantity();
                double profit = 0.0;
                
                // Calculate profit if cost is available
                if (item.getSweet().getCost() != null) {
                    profit = revenue - (item.getSweet().getCost() * item.getQuantity());
                }
                
                if (categoryStats.containsKey(category)) {
                    Map<String, Object> stats = categoryStats.get(category);
                    stats.put("unitsSold", (Integer) stats.get("unitsSold") + item.getQuantity());
                    stats.put("revenue", (Double) stats.get("revenue") + revenue);
                    stats.put("profit", (Double) stats.get("profit") + profit);
                } else {
                    Map<String, Object> stats = new HashMap<>();
                    stats.put("category", category);
                    stats.put("unitsSold", item.getQuantity());
                    stats.put("revenue", revenue);
                    stats.put("profit", profit);
                    categoryStats.put(category, stats);
                }
            }
        }
        
        // Calculate total revenue for percentage calculation
        double totalRevenue = categoryStats.values().stream()
            .mapToDouble(stats -> (Double) stats.get("revenue"))
            .sum();
        
        // Convert to list and calculate percentages
        salesByCategory = categoryStats.values().stream()
            .map(stats -> {
                Map<String, Object> category = new HashMap<>();
                category.put("category", stats.get("category"));
                category.put("revenue", stats.get("revenue"));
                category.put("unitsSold", stats.get("unitsSold"));
                category.put("profit", stats.get("profit"));
                
                // Calculate percentage
                double percentage = totalRevenue > 0 ? 
                    ((Double) stats.get("revenue") / totalRevenue) * 100 : 0.0;
                category.put("percentage", Math.round(percentage * 10) / 10.0);
                
                return category;
            })
            .sorted((a, b) -> Double.compare((Double) b.get("revenue"), (Double) a.get("revenue")))
            .collect(Collectors.toList());
        
        return salesByCategory;
    }
    
    private List<Map<String, Object>> getDefaultCustomerSegments() {
        List<Map<String, Object>> segments = new ArrayList<>();
        
        // Sample customer segments
        String[] segmentNames = {"Premium Buyers", "Regular Customers", "Occasional Buyers"};
        int[] counts = {234, 1456, 1157};
        double[] avgValues = {125.5, 45.2, 28.9};
        
        for (int i = 0; i < segmentNames.length; i++) {
            Map<String, Object> segment = new HashMap<>();
            segment.put("segment", segmentNames[i]);
            segment.put("count", counts[i]);
            segment.put("avgOrderValue", avgValues[i]);
            segments.add(segment);
        }
        
        return segments;
    }
    
    private List<Map<String, Object>> generateDefaultAlerts() {
        List<Map<String, Object>> alerts = new ArrayList<>();
        
        // Sample alerts
        String[] types = {"warning", "info", "success", "warning"};
        String[] titles = {"Low Stock Alert", "New Order Received", "Monthly Target Achieved", "Payment Issue"};
        String[] messages = {
            "5 products are running low on stock",
            "Order #ORD-2024-002 worth $156.75 received",
            "January revenue target exceeded by 12%",
            "Payment failed for Order #ORD-2024-005"
        };
        String[] timestamps = {
            "2024-01-28T10:30:00Z",
            "2024-01-28T09:15:00Z",
            "2024-01-28T08:00:00Z",
            "2024-01-27T16:45:00Z"
        };
        String[] priorities = {"high", "medium", "low", "high"};
        
        for (int i = 0; i < types.length; i++) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("id", i + 1);
            alert.put("type", types[i]);
            alert.put("title", titles[i]);
            alert.put("message", messages[i]);
            alert.put("timestamp", timestamps[i]);
            alert.put("priority", priorities[i]);
            alerts.add(alert);
        }
        
        return alerts;
    }
}
