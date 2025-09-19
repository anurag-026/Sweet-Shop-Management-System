package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.service.WebsiteTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
@CrossOrigin(origins = "*")
public class WebsiteTrackingController {
    
    @Autowired
    private WebsiteTrackingService websiteTrackingService;
    
    /**
     * Track page view event
     */
    @PostMapping("/page-view")
    public ResponseEntity<?> trackPageView(@RequestBody Map<String, Object> trackingData) {
        try {
            String sessionId = (String) trackingData.get("sessionId");
            String page = (String) trackingData.get("page");
            String referrer = (String) trackingData.get("referrer");
            String userAgent = (String) trackingData.get("userAgent");
            String ipAddress = (String) trackingData.get("ipAddress");
            String userId = (String) trackingData.get("userId");
            
            websiteTrackingService.trackPageView(sessionId, page, referrer, userAgent, ipAddress, userId);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Page view tracked"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Track session start
     */
    @PostMapping("/session-start")
    public ResponseEntity<?> trackSessionStart(@RequestBody Map<String, Object> trackingData) {
        try {
            String sessionId = (String) trackingData.get("sessionId");
            String landingPage = (String) trackingData.get("landingPage");
            String referrer = (String) trackingData.get("referrer");
            String userAgent = (String) trackingData.get("userAgent");
            String ipAddress = (String) trackingData.get("ipAddress");
            String userId = (String) trackingData.get("userId");
            
            websiteTrackingService.trackSessionStart(sessionId, landingPage, referrer, userAgent, ipAddress, userId);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Session started"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Track session end
     */
    @PostMapping("/session-end")
    public ResponseEntity<?> trackSessionEnd(@RequestBody Map<String, Object> trackingData) {
        try {
            String sessionId = (String) trackingData.get("sessionId");
            String exitPage = (String) trackingData.get("exitPage");
            Integer sessionDuration = (Integer) trackingData.get("sessionDuration");
            Integer pageViews = (Integer) trackingData.get("pageViews");
            
            websiteTrackingService.trackSessionEnd(sessionId, exitPage, sessionDuration, pageViews);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Session ended"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Track conversion events
     */
    @PostMapping("/conversion-event")
    public ResponseEntity<?> trackConversionEvent(@RequestBody Map<String, Object> trackingData) {
        try {
            String sessionId = (String) trackingData.get("sessionId");
            String eventType = (String) trackingData.get("eventType");
            String productId = (String) trackingData.get("productId");
            String orderId = (String) trackingData.get("orderId");
            String userId = (String) trackingData.get("userId");
            
            websiteTrackingService.trackConversionEvent(sessionId, eventType, productId, orderId, userId);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Conversion event tracked"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Track product view
     */
    @PostMapping("/product-view")
    public ResponseEntity<?> trackProductView(@RequestBody Map<String, Object> trackingData) {
        try {
            String sessionId = (String) trackingData.get("sessionId");
            String productId = (String) trackingData.get("productId");
            String userId = (String) trackingData.get("userId");
            
            websiteTrackingService.trackProductView(sessionId, productId, userId);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Product view tracked"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Track add to cart
     */
    @PostMapping("/add-to-cart")
    public ResponseEntity<?> trackAddToCart(@RequestBody Map<String, Object> trackingData) {
        try {
            String sessionId = (String) trackingData.get("sessionId");
            String productId = (String) trackingData.get("productId");
            String userId = (String) trackingData.get("userId");
            
            websiteTrackingService.trackAddToCart(sessionId, productId, userId);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Add to cart tracked"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Track checkout start
     */
    @PostMapping("/checkout-start")
    public ResponseEntity<?> trackCheckoutStart(@RequestBody Map<String, Object> trackingData) {
        try {
            String sessionId = (String) trackingData.get("sessionId");
            String userId = (String) trackingData.get("userId");
            
            websiteTrackingService.trackCheckoutStart(sessionId, userId);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Checkout start tracked"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Track purchase completion
     */
    @PostMapping("/purchase-complete")
    public ResponseEntity<?> trackPurchaseComplete(@RequestBody Map<String, Object> trackingData) {
        try {
            String sessionId = (String) trackingData.get("sessionId");
            String orderId = (String) trackingData.get("orderId");
            String userId = (String) trackingData.get("userId");
            
            websiteTrackingService.trackPurchaseComplete(sessionId, orderId, userId);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Purchase completion tracked"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
