package com.anurag.SweetShopBackend.service;

import com.anurag.SweetShopBackend.model.*;
import com.anurag.SweetShopBackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class WebsiteTrackingService {
    
    @Autowired
    private WebsiteTrafficRepository websiteTrafficRepository;
    
    @Autowired
    private ConversionEventRepository conversionEventRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SweetRepository sweetRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    /**
     * Track a page view event
     */
    public void trackPageView(String sessionId, String page, String referrer, 
                             String userAgent, String ipAddress, String userId) {
        try {
            // Find or create website traffic record
            Optional<WebsiteTraffic> existingTraffic = websiteTrafficRepository.findBySessionId(sessionId);
            
            if (existingTraffic.isPresent()) {
                WebsiteTraffic traffic = existingTraffic.get();
                traffic.setPageViews(traffic.getPageViews() + 1);
                traffic.setExitPage(page);
                websiteTrafficRepository.save(traffic);
            } else {
                // Create new session
                trackSessionStart(sessionId, page, referrer, userAgent, ipAddress, userId);
            }
            
            // Track conversion event for page view
            trackConversionEvent(sessionId, "page_view", null, null, userId);
            
        } catch (Exception e) {
            // Log error but don't throw to avoid breaking user experience
            System.err.println("Error tracking page view: " + e.getMessage());
        }
    }
    
    /**
     * Track session start
     */
    public void trackSessionStart(String sessionId, String landingPage, String referrer, 
                                 String userAgent, String ipAddress, String userId) {
        try {
            User user = null;
            if (userId != null && !userId.isEmpty()) {
                try {
                    user = userRepository.findById(UUID.fromString(userId)).orElse(null);
                } catch (Exception e) {
                    // Invalid UUID, continue without user
                }
            }
            
            WebsiteTraffic traffic = new WebsiteTraffic();
            traffic.setSessionId(sessionId);
            traffic.setUser(user);
            traffic.setIpAddress(ipAddress);
            traffic.setUserAgent(userAgent);
            traffic.setReferrer(referrer);
            traffic.setLandingPage(landingPage);
            traffic.setPageViews(1);
            traffic.setIsBounce(true); // Will be updated if more pages are viewed
            traffic.setVisitDate(LocalDate.now());
            traffic.setCreatedAt(LocalDateTime.now());
            
            websiteTrafficRepository.save(traffic);
            
            // Track visit event
            trackConversionEvent(sessionId, "visit", null, null, userId);
            
        } catch (Exception e) {
            System.err.println("Error tracking session start: " + e.getMessage());
        }
    }
    
    /**
     * Track session end
     */
    public void trackSessionEnd(String sessionId, String exitPage, Integer sessionDuration, Integer pageViews) {
        try {
            Optional<WebsiteTraffic> trafficOpt = websiteTrafficRepository.findBySessionId(sessionId);
            if (trafficOpt.isPresent()) {
                WebsiteTraffic traffic = trafficOpt.get();
                traffic.setExitPage(exitPage);
                traffic.setSessionDurationSeconds(sessionDuration);
                traffic.setPageViews(pageViews);
                traffic.setIsBounce(pageViews <= 1); // Bounce if only 1 page view
                websiteTrafficRepository.save(traffic);
            }
        } catch (Exception e) {
            System.err.println("Error tracking session end: " + e.getMessage());
        }
    }
    
    /**
     * Track conversion events
     */
    public void trackConversionEvent(String sessionId, String eventType, String productId, 
                                   String orderId, String userId) {
        try {
            User user = null;
            if (userId != null && !userId.isEmpty()) {
                try {
                    user = userRepository.findById(UUID.fromString(userId)).orElse(null);
                } catch (Exception e) {
                    // Invalid UUID, continue without user
                }
            }
            
            Sweet product = null;
            if (productId != null && !productId.isEmpty()) {
                try {
                    product = sweetRepository.findById(UUID.fromString(productId)).orElse(null);
                } catch (Exception e) {
                    // Invalid UUID, continue without product
                }
            }
            
            Order order = null;
            if (orderId != null && !orderId.isEmpty()) {
                try {
                    order = orderRepository.findById(UUID.fromString(orderId)).orElse(null);
                } catch (Exception e) {
                    // Invalid UUID, continue without order
                }
            }
            
            ConversionEvent event = new ConversionEvent();
            event.setSessionId(sessionId);
            event.setUser(user);
            event.setEventType(eventType);
            event.setProduct(product);
            event.setOrder(order);
            event.setEventDate(LocalDateTime.now());
            event.setCreatedAt(LocalDateTime.now());
            
            conversionEventRepository.save(event);
            
        } catch (Exception e) {
            System.err.println("Error tracking conversion event: " + e.getMessage());
        }
    }
    
    /**
     * Track product view
     */
    public void trackProductView(String sessionId, String productId, String userId) {
        trackConversionEvent(sessionId, "product_view", productId, null, userId);
    }
    
    /**
     * Track add to cart
     */
    public void trackAddToCart(String sessionId, String productId, String userId) {
        trackConversionEvent(sessionId, "add_to_cart", productId, null, userId);
    }
    
    /**
     * Track checkout start
     */
    public void trackCheckoutStart(String sessionId, String userId) {
        trackConversionEvent(sessionId, "checkout", null, null, userId);
    }
    
    /**
     * Track purchase completion
     */
    public void trackPurchaseComplete(String sessionId, String orderId, String userId) {
        trackConversionEvent(sessionId, "purchase", null, orderId, userId);
    }
    
    /**
     * Generate sample data for testing
     */
    public void generateSampleData(int days, int sessionsPerDay) {
        try {
            LocalDate startDate = LocalDate.now().minusDays(days);
            
            for (int day = 0; day < days; day++) {
                LocalDate currentDate = startDate.plusDays(day);
                
                for (int session = 0; session < sessionsPerDay; session++) {
                    String sessionId = "sample-session-" + currentDate + "-" + session;
                    String[] pages = {"/", "/products", "/products/sweets", "/cart", "/checkout"};
                    String[] referrers = {"google.com", "facebook.com", "direct", "bing.com", null};
                    
                    // Random session data
                    String landingPage = pages[(int) (Math.random() * pages.length)];
                    String referrer = referrers[(int) (Math.random() * referrers.length)];
                    String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
                    String ipAddress = "192.168.1." + (int) (Math.random() * 255);
                    
                    // Track session start
                    trackSessionStart(sessionId, landingPage, referrer, userAgent, ipAddress, null);
                    
                    // Simulate page views (1-5 pages per session)
                    int pageViewCount = (int) (Math.random() * 5) + 1;
                    for (int pv = 0; pv < pageViewCount; pv++) {
                        String page = pages[(int) (Math.random() * pages.length)];
                        trackPageView(sessionId, page, referrer, userAgent, ipAddress, null);
                    }
                    
                    // Simulate conversion events
                    if (Math.random() > 0.3) { // 70% chance of product view
                        trackProductView(sessionId, null, null);
                    }
                    if (Math.random() > 0.6) { // 40% chance of add to cart
                        trackAddToCart(sessionId, null, null);
                    }
                    if (Math.random() > 0.8) { // 20% chance of checkout
                        trackCheckoutStart(sessionId, null);
                    }
                    if (Math.random() > 0.9) { // 10% chance of purchase
                        trackPurchaseComplete(sessionId, null, null);
                    }
                    
                    // Track session end
                    int sessionDuration = (int) (Math.random() * 1800) + 30; // 30 seconds to 30 minutes
                    trackSessionEnd(sessionId, pages[(int) (Math.random() * pages.length)], 
                                  sessionDuration, pageViewCount);
                }
            }
            
            System.out.println("Generated sample data for " + days + " days with " + sessionsPerDay + " sessions per day");
            
        } catch (Exception e) {
            System.err.println("Error generating sample data: " + e.getMessage());
        }
    }
}
