package com.anurag.SweetShopBackend.service;

import com.anurag.SweetShopBackend.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * Service to integrate analytics collection into the application workflow
 * This service acts as a bridge between application events and analytics tracking
 */
@Service
public class AnalyticsIntegrationService {

    @Autowired
    private AnalyticsCollectionService analyticsCollectionService;
    
    /**
     * Track product view event
     * Should be called from product controller when a product is viewed
     */
    @Transactional
    public void trackProductView(HttpServletRequest request, User user, Sweet sweet) {
        String sessionId = getOrCreateSessionId(request);
        analyticsCollectionService.trackProductView(sessionId, user, sweet);
    }
    
    /**
     * Track add to cart event
     * Should be called from cart controller when a product is added to cart
     */
    @Transactional
    public void trackAddToCart(HttpServletRequest request, User user, Sweet sweet, int quantity) {
        String sessionId = getOrCreateSessionId(request);
        analyticsCollectionService.trackAddToCart(sessionId, user, sweet, quantity);
    }
    
    /**
     * Track checkout initiation
     * Should be called from order controller when checkout is initiated
     */
    @Transactional
    public void trackCheckout(HttpServletRequest request, User user) {
        String sessionId = getOrCreateSessionId(request);
        analyticsCollectionService.trackCheckout(sessionId, user);
    }
    
    /**
     * Track order creation
     * Should be called from order service when an order is created
     */
    @Transactional
    public void trackOrderCreated(HttpServletRequest request, User user, Order order) {
        String sessionId = getOrCreateSessionId(request);
        analyticsCollectionService.trackPurchase(sessionId, user, order);
        analyticsCollectionService.trackOrderCreated(order);
    }
    
    /**
     * Track inventory update
     * Should be called from inventory service when inventory is updated
     */
    @Transactional
    public void trackInventoryUpdate(Sweet sweet, int previousQuantity, int newQuantity, String reason, User changedBy) {
        analyticsCollectionService.trackInventoryChange(sweet, previousQuantity, newQuantity, reason, changedBy);
    }
    
    /**
     * Track website visit
     * Should be called from a request interceptor on new sessions
     */
    @Transactional
    public void trackWebsiteVisit(HttpServletRequest request, User user) {
        String sessionId = getOrCreateSessionId(request);
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String referrer = request.getHeader("Referer");
        String landingPage = request.getRequestURI();
        
        analyticsCollectionService.trackWebsiteVisit(sessionId, user, ipAddress, userAgent, referrer, landingPage);
    }
    
    /**
     * Update session end
     * Should be called from a session listener when a session ends
     */
    @Transactional
    public void updateSessionEnd(String sessionId, String exitPage, int durationSeconds, boolean isBounce) {
        analyticsCollectionService.updateSessionEnd(sessionId, exitPage, durationSeconds, isBounce);
    }
    
    // Helper methods
    
    private String getOrCreateSessionId(HttpServletRequest request) {
        // Get session ID from request or create a new one
        if (request.getSession(false) != null) {
            return request.getSession().getId();
        } else {
            return UUID.randomUUID().toString();
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
