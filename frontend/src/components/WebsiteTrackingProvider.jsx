import React, { createContext, useContext, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import useWebsiteTracking from '../hooks/useWebsiteTracking';

const WebsiteTrackingContext = createContext();

/**
 * Website Tracking Provider Component
 * Provides tracking context to the entire application
 */
export const WebsiteTrackingProvider = ({ children }) => {
    const location = useLocation();
    const tracking = useWebsiteTracking();
    
    // Track page views on route changes
    useEffect(() => {
        if (window.websiteTracker) {
            window.websiteTracker.trackPageView();
        }
    }, [location]);
    
    return (
        <WebsiteTrackingContext.Provider value={tracking}>
            {children}
        </WebsiteTrackingContext.Provider>
    );
};

/**
 * Hook to use website tracking context
 */
export const useWebsiteTrackingContext = () => {
    const context = useContext(WebsiteTrackingContext);
    if (!context) {
        throw new Error('useWebsiteTrackingContext must be used within a WebsiteTrackingProvider');
    }
    return context;
};

export default WebsiteTrackingProvider;
