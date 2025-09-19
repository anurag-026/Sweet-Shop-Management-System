package com.anurag.SweetShopBackend.service;

import com.anurag.SweetShopBackend.model.*;
import com.anurag.SweetShopBackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Service responsible for collecting and aggregating analytics data
 * This service runs scheduled tasks to process raw data into analytics tables
 */
@Service
public class AnalyticsCollectionService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private SweetRepository sweetRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductAnalyticsRepository productAnalyticsRepository;
    
    @Autowired
    private CategorySalesRepository categorySalesRepository;
    
    @Autowired
    private CustomerAnalyticsRepository customerAnalyticsRepository;
    
    @Autowired
    private CustomerSegmentRepository customerSegmentRepository;
    
    @Autowired
    private MonthlySalesRepository monthlySalesRepository;
    
    @Autowired
    private SystemAlertRepository systemAlertRepository;
    
    /**
     * Track order creation for analytics
     * This should be called whenever a new order is created
     */
    @Transactional
    public void trackOrderCreated(Order order) {
        // Update product analytics
        updateProductAnalytics(order);
        
        // Update category sales
        updateCategorySales(order);
        
        // Update customer analytics
        updateCustomerAnalytics(order);
        
        // Update monthly sales
        updateMonthlySales(order);
        
        // Check for low stock and create alerts
        checkLowStockAndCreateAlerts(order);
    }
    
    /**
     * Track product view for analytics
     * This should be called whenever a product is viewed
     */
    @Transactional
    public void trackProductView(String sessionId, User user, Sweet sweet) {
        // Create conversion event
        ConversionEvent event = new ConversionEvent();
        event.setSessionId(sessionId);
        event.setUser(user);
        event.setEventType("product_view");
        event.setProduct(sweet);
        event.setEventDate(LocalDateTime.now());
        
        // Update product view count in product analytics
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        
        Optional<ProductAnalytics> monthlyAnalyticsOpt = productAnalyticsRepository
            .findBySweetAndTimePeriodAndPeriodStart(sweet, "monthly", firstDayOfMonth);
        
        if (monthlyAnalyticsOpt.isPresent()) {
            ProductAnalytics analytics = monthlyAnalyticsOpt.get();
            analytics.setViewCount(analytics.getViewCount() + 1);
            
            // Update conversion rate
            if (analytics.getUnitsSold() > 0 && analytics.getViewCount() > 0) {
                double conversionRate = (double) analytics.getUnitsSold() / analytics.getViewCount() * 100;
                analytics.setConversionRate(Math.round(conversionRate * 10) / 10.0);
            }
            
            productAnalyticsRepository.save(analytics);
        } else {
            // Create new analytics record if it doesn't exist
            ProductAnalytics newAnalytics = new ProductAnalytics();
            newAnalytics.setSweet(sweet);
            newAnalytics.setViewCount(1);
            newAnalytics.setTimePeriod("monthly");
            newAnalytics.setPeriodStart(firstDayOfMonth);
            newAnalytics.setPeriodEnd(firstDayOfMonth.plusMonths(1).minusDays(1));
            productAnalyticsRepository.save(newAnalytics);
        }
    }
    
    /**
     * Track add to cart for analytics
     * This should be called whenever a product is added to cart
     */
    @Transactional
    public void trackAddToCart(String sessionId, User user, Sweet sweet, int quantity) {
        // Create conversion event
        ConversionEvent event = new ConversionEvent();
        event.setSessionId(sessionId);
        event.setUser(user);
        event.setEventType("add_to_cart");
        event.setProduct(sweet);
        event.setEventDate(LocalDateTime.now());
        
        // Update add to cart count in product analytics
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        
        Optional<ProductAnalytics> monthlyAnalyticsOpt = productAnalyticsRepository
            .findBySweetAndTimePeriodAndPeriodStart(sweet, "monthly", firstDayOfMonth);
        
        if (monthlyAnalyticsOpt.isPresent()) {
            ProductAnalytics analytics = monthlyAnalyticsOpt.get();
            analytics.setAddToCartCount(analytics.getAddToCartCount() + quantity);
            productAnalyticsRepository.save(analytics);
        }
    }
    
    /**
     * Track checkout initiation for analytics
     * This should be called whenever a checkout is initiated
     */
    @Transactional
    public void trackCheckout(String sessionId, User user) {
        // Create conversion event
        ConversionEvent event = new ConversionEvent();
        event.setSessionId(sessionId);
        event.setUser(user);
        event.setEventType("checkout");
        event.setEventDate(LocalDateTime.now());
    }
    
    /**
     * Track purchase completion for analytics
     * This should be called whenever a purchase is completed
     */
    @Transactional
    public void trackPurchase(String sessionId, User user, Order order) {
        // Create conversion event
        ConversionEvent event = new ConversionEvent();
        event.setSessionId(sessionId);
        event.setUser(user);
        event.setEventType("purchase");
        event.setOrder(order);
        event.setEventDate(LocalDateTime.now());
    }
    
    /**
     * Track inventory changes for analytics
     * This should be called whenever inventory is updated
     */
    @Transactional
    public void trackInventoryChange(Sweet sweet, int previousQuantity, int newQuantity, String reason, User changedBy) {
        // Create inventory history record
        InventoryHistory history = new InventoryHistory();
        history.setSweet(sweet);
        history.setPreviousQuantity(previousQuantity);
        history.setNewQuantity(newQuantity);
        history.setChangeReason(reason);
        history.setChangedBy(changedBy);
        history.setChangedAt(LocalDateTime.now());
        
        // Check if stock is low and create alert if needed
        if (newQuantity < sweet.getMinThreshold()) {
            createLowStockAlert(sweet);
        }
    }
    
    /**
     * Track website visit for analytics
     * This should be called whenever a user visits the website
     */
    @Transactional
    public void trackWebsiteVisit(String sessionId, User user, String ipAddress, String userAgent, 
                                String referrer, String landingPage) {
        // Create website traffic record
        WebsiteTraffic traffic = new WebsiteTraffic();
        traffic.setSessionId(sessionId);
        traffic.setUser(user);
        traffic.setIpAddress(ipAddress);
        traffic.setUserAgent(userAgent);
        traffic.setReferrer(referrer);
        traffic.setLandingPage(landingPage);
        traffic.setVisitDate(LocalDate.now());
        traffic.setPageViews(1);
    }
    
    /**
     * Update session duration and exit page for analytics
     * This should be called when a session ends
     */
    @Transactional
    public void updateSessionEnd(String sessionId, String exitPage, int durationSeconds, boolean isBounce) {
        // Find and update the website traffic record
        // Implementation depends on WebsiteTrafficRepository capabilities
    }
    
    /**
     * Daily scheduled task to aggregate analytics data
     * Runs at 1:00 AM every day
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void dailyAnalyticsAggregation() {
        // Aggregate yesterday's data
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        // Aggregate product analytics
        aggregateProductAnalytics(yesterday);
        
        // Aggregate category sales
        aggregateCategorySales(yesterday);
        
        // Update customer segments
        updateCustomerSegments();
    }
    
    /**
     * Monthly scheduled task to aggregate analytics data
     * Runs at 2:00 AM on the 1st day of each month
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    @Transactional
    public void monthlyAnalyticsAggregation() {
        // Get previous month
        LocalDate firstDayOfPrevMonth = LocalDate.now().withDayOfMonth(1).minusMonths(1);
        int year = firstDayOfPrevMonth.getYear();
        int month = firstDayOfPrevMonth.getMonthValue();
        
        // Aggregate monthly sales
        aggregateMonthlySales(year, month);
    }
    
    // Helper methods
    
    private void updateProductAnalytics(Order order) {
        LocalDate orderDate = order.getOrderDate().toLocalDate();
        LocalDate firstDayOfMonth = orderDate.withDayOfMonth(1);
        
        // Get order items
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        
        // Update analytics for each product
        for (OrderItem item : orderItems) {
            Sweet sweet = item.getSweet();
            double revenue = item.getPrice() * item.getQuantity();
            double profit = 0.0;
            
            // Calculate profit if cost is available
            if (sweet.getCost() != null) {
                profit = revenue - (sweet.getCost() * item.getQuantity());
            }
            
            // Update or create monthly product analytics
            Optional<ProductAnalytics> monthlyAnalyticsOpt = productAnalyticsRepository
                .findBySweetAndTimePeriodAndPeriodStart(sweet, "monthly", firstDayOfMonth);
            
            if (monthlyAnalyticsOpt.isPresent()) {
                ProductAnalytics analytics = monthlyAnalyticsOpt.get();
                analytics.setUnitsSold(analytics.getUnitsSold() + item.getQuantity());
                analytics.setRevenue(analytics.getRevenue() + revenue);
                analytics.setProfit(analytics.getProfit() + profit);
                
                // Update conversion rate
                if (analytics.getViewCount() > 0) {
                    double conversionRate = (double) analytics.getUnitsSold() / analytics.getViewCount() * 100;
                    analytics.setConversionRate(Math.round(conversionRate * 10) / 10.0);
                }
                
                productAnalyticsRepository.save(analytics);
            } else {
                // Create new analytics record
                ProductAnalytics newAnalytics = new ProductAnalytics();
                newAnalytics.setSweet(sweet);
                newAnalytics.setUnitsSold(item.getQuantity());
                newAnalytics.setRevenue(revenue);
                newAnalytics.setProfit(profit);
                newAnalytics.setTimePeriod("monthly");
                newAnalytics.setPeriodStart(firstDayOfMonth);
                newAnalytics.setPeriodEnd(firstDayOfMonth.plusMonths(1).minusDays(1));
                productAnalyticsRepository.save(newAnalytics);
            }
        }
    }
    
    private void updateCategorySales(Order order) {
        LocalDate orderDate = order.getOrderDate().toLocalDate();
        LocalDate firstDayOfMonth = orderDate.withDayOfMonth(1);
        
        // Get order items
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        
        // Group by category and update category sales
        for (OrderItem item : orderItems) {
            Sweet sweet = item.getSweet();
            String category = sweet.getCategory();
            
            if (category == null || category.isEmpty()) {
                category = "Uncategorized";
            }
            
            double revenue = item.getPrice() * item.getQuantity();
            double profit = 0.0;
            
            // Calculate profit if cost is available
            if (sweet.getCost() != null) {
                profit = revenue - (sweet.getCost() * item.getQuantity());
            }
            
            // Update or create monthly category sales
            Optional<CategorySales> monthlySalesOpt = categorySalesRepository
                .findByCategoryAndTimePeriodAndPeriodStart(category, "monthly", firstDayOfMonth);
            
            if (monthlySalesOpt.isPresent()) {
                CategorySales sales = monthlySalesOpt.get();
                sales.setUnitsSold(sales.getUnitsSold() + item.getQuantity());
                sales.setRevenue(sales.getRevenue() + revenue);
                sales.setProfit(sales.getProfit() + profit);
                categorySalesRepository.save(sales);
            } else {
                // Create new category sales record
                CategorySales newSales = new CategorySales();
                newSales.setCategory(category);
                newSales.setUnitsSold(item.getQuantity());
                newSales.setRevenue(revenue);
                newSales.setProfit(profit);
                newSales.setTimePeriod("monthly");
                newSales.setPeriodStart(firstDayOfMonth);
                newSales.setPeriodEnd(firstDayOfMonth.plusMonths(1).minusDays(1));
                categorySalesRepository.save(newSales);
            }
        }
        
        // Update percentages for all categories in this period
        updateCategoryPercentages(firstDayOfMonth);
    }
    
    private void updateCategoryPercentages(LocalDate firstDayOfMonth) {
        List<CategorySales> allCategorySales = categorySalesRepository
            .findByTimePeriodAndPeriodStart("monthly", firstDayOfMonth);
        
        // Calculate total revenue
        double totalRevenue = allCategorySales.stream()
            .mapToDouble(CategorySales::getRevenue)
            .sum();
        
        if (totalRevenue > 0) {
            // Update percentages
            for (CategorySales sales : allCategorySales) {
                double percentage = (sales.getRevenue() / totalRevenue) * 100;
                sales.setPercentage(Math.round(percentage * 10) / 10.0);
                categorySalesRepository.save(sales);
            }
        }
    }
    
    private void updateCustomerAnalytics(Order order) {
        User user = order.getUser();
        
        // Get or create customer analytics
        Optional<CustomerAnalytics> analyticsOpt = customerAnalyticsRepository.findByUser(user);
        CustomerAnalytics analytics;
        
        if (analyticsOpt.isPresent()) {
            analytics = analyticsOpt.get();
            
            // Update existing customer analytics
            analytics.setTotalOrders(analytics.getTotalOrders() + 1);
            analytics.setTotalSpent(analytics.getTotalSpent() + order.getTotalAmount());
            analytics.setAvgOrderValue(analytics.getTotalSpent() / analytics.getTotalOrders());
            
            // Update order dates
            if (analytics.getFirstOrderDate() == null) {
                analytics.setFirstOrderDate(order.getOrderDate());
            }
            
            LocalDateTime previousLastOrderDate = analytics.getLastOrderDate();
            analytics.setLastOrderDate(order.getOrderDate());
            
            // Mark as returning customer if this is not their first order
            if (previousLastOrderDate != null) {
                analytics.setIsReturning(true);
                
                // Calculate order frequency in days
                long daysBetween = ChronoUnit.DAYS.between(previousLastOrderDate, order.getOrderDate());
                
                if (analytics.getOrderFrequencyDays() == null) {
                    analytics.setOrderFrequencyDays((double) daysBetween);
                } else {
                    // Moving average of order frequency
                    analytics.setOrderFrequencyDays((analytics.getOrderFrequencyDays() + daysBetween) / 2);
                }
            }
        } else {
            // Create new customer analytics
            analytics = new CustomerAnalytics();
            analytics.setUser(user);
            analytics.setTotalOrders(1);
            analytics.setTotalSpent(order.getTotalAmount());
            analytics.setAvgOrderValue(order.getTotalAmount());
            analytics.setFirstOrderDate(order.getOrderDate());
            analytics.setLastOrderDate(order.getOrderDate());
            analytics.setIsReturning(false);
        }
        
        // Save analytics
        customerAnalyticsRepository.save(analytics);
        
        // Assign to appropriate segment
        assignCustomerSegment(analytics);
    }
    
    private void assignCustomerSegment(CustomerAnalytics analytics) {
        // Simple segmentation logic based on average order value
        String segmentName;
        
        if (analytics.getAvgOrderValue() >= 100) {
            segmentName = "Premium Buyers";
        } else if (analytics.getAvgOrderValue() >= 50) {
            segmentName = "Regular Customers";
        } else {
            segmentName = "Occasional Buyers";
        }
        
        // Get or create segment
        CustomerSegment segment = customerSegmentRepository.findBySegmentName(segmentName)
            .orElseGet(() -> {
                CustomerSegment newSegment = new CustomerSegment();
                newSegment.setSegmentName(segmentName);
                return customerSegmentRepository.save(newSegment);
            });
        
        // Assign segment
        analytics.setSegment(segment);
        customerAnalyticsRepository.save(analytics);
    }
    
    private void updateMonthlySales(Order order) {
        LocalDateTime orderDate = order.getOrderDate();
        int year = orderDate.getYear();
        int month = orderDate.getMonthValue();
        
        // Get or create monthly sales record
        Optional<MonthlySales> salesOpt = monthlySalesRepository.findByMonthAndYear(month, year);
        
        if (salesOpt.isPresent()) {
            MonthlySales sales = salesOpt.get();
            sales.setTotalOrders(sales.getTotalOrders() + 1);
            sales.setTotalRevenue(sales.getTotalRevenue() + order.getTotalAmount());
            
            // Calculate profit if available
            double orderProfit = calculateOrderProfit(order);
            sales.setTotalProfit(sales.getTotalProfit() + orderProfit);
            
            // Update average order value
            sales.setAvgOrderValue(sales.getTotalRevenue() / sales.getTotalOrders());
            
            // Update growth percentage (compared to previous month)
            updateMonthlyGrowthPercentage(sales, month, year);
            
            monthlySalesRepository.save(sales);
        } else {
            // Create new monthly sales record
            MonthlySales newSales = new MonthlySales();
            newSales.setMonth(month);
            newSales.setYear(year);
            newSales.setTotalOrders(1);
            newSales.setTotalRevenue(order.getTotalAmount());
            
            // Calculate profit if available
            double orderProfit = calculateOrderProfit(order);
            newSales.setTotalProfit(orderProfit);
            
            newSales.setAvgOrderValue(order.getTotalAmount());
            
            // Update growth percentage (compared to previous month)
            updateMonthlyGrowthPercentage(newSales, month, year);
            
            monthlySalesRepository.save(newSales);
        }
    }
    
    private double calculateOrderProfit(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        double profit = 0.0;
        
        for (OrderItem item : orderItems) {
            Sweet sweet = item.getSweet();
            if (sweet.getCost() != null) {
                profit += (item.getPrice() - sweet.getCost()) * item.getQuantity();
            }
        }
        
        return profit;
    }
    
    private void updateMonthlyGrowthPercentage(MonthlySales sales, int month, int year) {
        // Get previous month
        int prevMonth = month == 1 ? 12 : month - 1;
        int prevYear = month == 1 ? year - 1 : year;
        
        // Find previous month's sales
        Optional<MonthlySales> prevSalesOpt = monthlySalesRepository.findByMonthAndYear(prevMonth, prevYear);
        
        if (prevSalesOpt.isPresent()) {
            MonthlySales prevSales = prevSalesOpt.get();
            
            // Calculate growth percentage
            if (prevSales.getTotalRevenue() > 0) {
                double growthPercentage = ((sales.getTotalRevenue() - prevSales.getTotalRevenue()) 
                    / prevSales.getTotalRevenue()) * 100;
                sales.setGrowthPercentage(Math.round(growthPercentage * 10) / 10.0);
            }
        }
    }
    
    private void checkLowStockAndCreateAlerts(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        
        for (OrderItem item : orderItems) {
            Sweet sweet = item.getSweet();
            
            // Check if stock is below threshold
            if (sweet.getQuantity() < sweet.getMinThreshold()) {
                createLowStockAlert(sweet);
            }
        }
    }
    
    private void createLowStockAlert(Sweet sweet) {
        // Create system alert for low stock
        SystemAlert alert = new SystemAlert();
        alert.setAlertType("warning");
        alert.setTitle("Low Stock Alert");
        alert.setMessage("Product '" + sweet.getName() + "' is running low on stock. Current quantity: " 
            + sweet.getQuantity() + ", Threshold: " + sweet.getMinThreshold());
        alert.setPriority("high");
        systemAlertRepository.save(alert);
    }
    
    private void aggregateProductAnalytics(LocalDate date) {
        // Implementation would depend on specific aggregation requirements
    }
    
    private void aggregateCategorySales(LocalDate date) {
        // Implementation would depend on specific aggregation requirements
    }
    
    private void updateCustomerSegments() {
        // Implementation would depend on specific segmentation requirements
    }
    
    private void aggregateMonthlySales(int year, int month) {
        // Implementation would depend on specific aggregation requirements
    }
}
