import { useEffect, useCallback } from 'react';
import { useLocation } from 'react-router-dom';

/**
 * React hook for website tracking
 * Automatically tracks page views and provides tracking methods
 */
export const useWebsiteTracking = () => {
    const location = useLocation();
    
    // Track page view when location changes
    useEffect(() => {
        if (window.websiteTracker) {
            window.websiteTracker.trackPageView();
        }
    }, [location]);
    
    // Track product view
    const trackProductView = useCallback((productId) => {
        if (window.websiteTracker) {
            window.websiteTracker.trackProductView(productId);
        }
    }, []);
    
    // Track add to cart
    const trackAddToCart = useCallback((productId) => {
        if (window.websiteTracker) {
            window.websiteTracker.trackAddToCart(productId);
        }
    }, []);
    
    // Track checkout start
    const trackCheckoutStart = useCallback(() => {
        if (window.websiteTracker) {
            window.websiteTracker.trackCheckoutStart();
        }
    }, []);
    
    // Track purchase completion
    const trackPurchaseComplete = useCallback((orderId) => {
        if (window.websiteTracker) {
            window.websiteTracker.trackPurchaseComplete(orderId);
        }
    }, []);
    
    // Get session info
    const getSessionInfo = useCallback(() => {
        if (window.websiteTracker) {
            return window.websiteTracker.getSessionInfo();
        }
        return null;
    }, []);
    
    // Enable/disable tracking
    const setTracking = useCallback((enabled) => {
        if (window.websiteTracker) {
            window.websiteTracker.setTracking(enabled);
        }
    }, []);
    
    return {
        trackProductView,
        trackAddToCart,
        trackCheckoutStart,
        trackPurchaseComplete,
        getSessionInfo,
        setTracking
    };
};

export default useWebsiteTracking;
