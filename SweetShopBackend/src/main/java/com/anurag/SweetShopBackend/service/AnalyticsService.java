package com.anurag.SweetShopBackend.service;

import com.anurag.SweetShopBackend.model.*;
import com.anurag.SweetShopBackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        List<MonthlySales> monthlySalesData = monthlySalesRepository.findRecentMonths(months);
        
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
        // Get top customer segments
        List<Object[]> segmentsData = customerAnalyticsRepository.getTopCustomerSegments();
        
        if (segmentsData != null && !segmentsData.isEmpty()) {
            return segmentsData.stream().map(data -> {
                Map<String, Object> segment = new HashMap<>();
                segment.put("segment", data[0]);
                segment.put("count", data[1]);
                segment.put("avgOrderValue", data[2]);
                return segment;
            }).collect(Collectors.toList());
        }
        
        // Fallback to default segments
        return getDefaultCustomerSegments();
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
        // Get alerts from database
        List<SystemAlert> alerts = systemAlertRepository.findByIsDismissedFalseOrderByCreatedAtDesc();
        
        if (alerts != null && !alerts.isEmpty()) {
            return alerts.stream().limit(limit).map(alert -> {
                Map<String, Object> alertMap = new HashMap<>();
                alertMap.put("id", alert.getId().toString());
                alertMap.put("type", alert.getAlertType());
                alertMap.put("title", alert.getTitle());
                alertMap.put("message", alert.getMessage());
                alertMap.put("timestamp", alert.getCreatedAt().toString());
                alertMap.put("priority", alert.getPriority());
                return alertMap;
            }).collect(Collectors.toList());
        }
        
        // Generate default alerts if none exist
        return generateDefaultAlerts();
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
        
        // Calculate monthly data from orders
        // Implementation would depend on the Order repository capabilities
        // This is a simplified placeholder
        
        // Sample data for the last 12 months
        String[] monthNames = {"Feb 2023", "Mar 2023", "Apr 2023", "May 2023", "Jun 2023", 
                             "Jul 2023", "Aug 2023", "Sep 2023", "Oct 2023", "Nov 2023", 
                             "Dec 2023", "Jan 2024"};
        double[] revenues = {32150.0, 34520.0, 28950.0, 36780.0, 41230.0, 
                           38950.0, 42340.0, 45680.0, 48920.0, 52340.0, 
                           67890.0, 45680.5};
        int[] orders = {856, 923, 745, 987, 1105, 
                      1023, 1156, 1247, 1324, 1456, 
                      1890, 1247};
        double[] growth = {5.2, 7.4, -16.1, 27.0, 12.1, 
                         -5.5, 8.7, 7.9, 7.1, 7.0, 
                         29.7, -32.7};
        
        // Limit to requested number of months
        int dataPoints = Math.min(months, monthNames.length);
        int startIdx = monthNames.length - dataPoints;
        
        for (int i = startIdx; i < monthNames.length; i++) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", monthNames[i]);
            monthData.put("revenue", revenues[i]);
            monthData.put("orders", orders[i]);
            monthData.put("growth", growth[i]);
            monthlySales.add(monthData);
        }
        
        return monthlySales;
    }
    
    private List<Map<String, Object>> calculateTopProductsFromOrders(int limit) {
        // This would normally calculate top products from order items
        // For now, return sample data
        List<Map<String, Object>> topProducts = new ArrayList<>();
        
        // Sample data for top products
        String[] names = {"Artisan Gummy Bears", "Chocolate Truffles", "French Macarons", 
                        "Salted Caramel Bonbons", "Honey Lavender Lollipops"};
        String[] categories = {"Gummies", "Chocolate", "Macarons", "Caramel", "Lollipops"};
        int[] unitsSold = {245, 198, 156, 134, 98};
        double[] revenues = {3675.0, 4950.0, 3120.0, 2680.0, 1470.0};
        double[] profits = {1470.0, 1980.0, 1248.0, 1072.0, 588.0};
        double[] margins = {40.0, 40.0, 40.0, 40.0, 40.0};
        int[] stocks = {15, 8, 22, 12, 6};
        String[] images = {
            "https://i.postimg.cc/sMTg12Kg/artisan-gummy-bears-colorful.jpg",
            "https://i.postimg.cc/dZRqFQLv/elegant-dark-chocolate-truffles.jpg",
            "https://i.postimg.cc/JHC1SH7P/colorful-french-macarons-assortment.jpg",
            "https://i.postimg.cc/KKKxzDR0/salted-caramel-bonbons-chocolate.jpg",
            "https://i.postimg.cc/z3TDK7pM/honey-lavender-lollipops-purple.jpg"
        };
        
        // Limit to requested number of products
        int count = Math.min(limit, names.length);
        
        for (int i = 0; i < count; i++) {
            Map<String, Object> product = new HashMap<>();
            product.put("id", "prod-" + (i + 1));
            product.put("name", names[i]);
            product.put("category", categories[i]);
            product.put("unitsSold", unitsSold[i]);
            product.put("revenue", revenues[i]);
            product.put("profit", profits[i]);
            product.put("profitMargin", margins[i]);
            product.put("stock", stocks[i]);
            product.put("image", images[i]);
            topProducts.add(product);
        }
        
        return topProducts;
    }
    
    private List<Map<String, Object>> calculateSalesByCategoryFromOrders(LocalDateTime startDate, LocalDateTime endDate) {
        // This would normally calculate sales by category from order items
        // For now, return sample data
        List<Map<String, Object>> salesByCategory = new ArrayList<>();
        
        // Sample data for sales by category
        String[] categories = {"Chocolate", "Gummies", "Macarons", "Caramel", "Lollipops", "Fudge"};
        double[] revenues = {15680.5, 12340.25, 8920.75, 4560.0, 2340.0, 1859.0};
        double[] percentages = {34.3, 27.0, 19.5, 10.0, 5.1, 4.1};
        int[] unitsSold = {456, 789, 234, 156, 98, 67};
        double[] profits = {6272.2, 4936.1, 3568.3, 1824.0, 936.0, 743.6};
        
        for (int i = 0; i < categories.length; i++) {
            Map<String, Object> category = new HashMap<>();
            category.put("category", categories[i]);
            category.put("revenue", revenues[i]);
            category.put("percentage", percentages[i]);
            category.put("unitsSold", unitsSold[i]);
            category.put("profit", profits[i]);
            salesByCategory.add(category);
        }
        
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
