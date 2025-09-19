# Website Performance Tracking Integration Guide

## Overview
This guide explains how to integrate the website performance tracking system into your Sweet Shop application.

## Backend Implementation ✅

### 1. Controllers Created
- `WebsiteTrackingController` - Handles tracking API endpoints
- `SampleDataController` - Generates sample data for testing

### 2. Services Created
- `WebsiteTrackingService` - Processes and stores tracking data

### 3. Enhanced Repositories
- Added methods to `WebsiteTrafficRepository` and `ConversionEventRepository`

### 4. Enhanced Analytics Service
- Improved `getWebsiteTraffic()` and `getConversionFunnel()` methods

## Frontend Integration

### 1. Import Tracking Library
Add to your main App.js or index.js:

```javascript
import './utils/websiteTracking.js';
import { WebsiteTrackingProvider } from './components/WebsiteTrackingProvider';
```

### 2. Wrap App with Tracking Provider
```jsx
function App() {
  return (
    <WebsiteTrackingProvider>
      {/* Your existing app components */}
    </WebsiteTrackingProvider>
  );
}
```

### 3. Use Tracking in Components
```jsx
import { useWebsiteTrackingContext } from './components/WebsiteTrackingProvider';

function ProductCard({ product }) {
  const { trackProductView, trackAddToCart } = useWebsiteTrackingContext();
  
  const handleProductClick = () => {
    trackProductView(product.id);
    // Navigate to product page
  };
  
  const handleAddToCart = () => {
    trackAddToCart(product.id);
    // Add to cart logic
  };
  
  return (
    <div>
      <button onClick={handleProductClick}>View Product</button>
      <button onClick={handleAddToCart}>Add to Cart</button>
    </div>
  );
}
```

### 4. Track Checkout and Purchase
```jsx
function CheckoutPage() {
  const { trackCheckoutStart, trackPurchaseComplete } = useWebsiteTrackingContext();
  
  useEffect(() => {
    trackCheckoutStart();
  }, []);
  
  const handleOrderComplete = (orderId) => {
    trackPurchaseComplete(orderId);
  };
  
  return (
    // Your checkout form
  );
}
```

## API Endpoints

### Tracking Endpoints
- `POST /api/tracking/session-start` - Track session start
- `POST /api/tracking/page-view` - Track page view
- `POST /api/tracking/session-end` - Track session end
- `POST /api/tracking/product-view` - Track product view
- `POST /api/tracking/add-to-cart` - Track add to cart
- `POST /api/tracking/checkout-start` - Track checkout start
- `POST /api/tracking/purchase-complete` - Track purchase completion

### Analytics Endpoints (Existing)
- `GET /api/admin/analytics/performance/traffic` - Get traffic metrics
- `GET /api/admin/analytics/performance/conversion-funnel` - Get conversion funnel

### Sample Data Endpoints
- `POST /api/admin/sample-data/website-traffic` - Generate sample data

## Testing the Implementation

### 1. Generate Sample Data
```bash
curl -X POST "http://localhost:8080/api/admin/sample-data/website-traffic?days=30&sessionsPerDay=50" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

### 2. Check Analytics Data
```bash
curl -X GET "http://localhost:8080/api/admin/analytics/performance/traffic?range=30d" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

### 3. Test Frontend Tracking
1. Open browser developer tools
2. Navigate through your website
3. Check Network tab for tracking requests
4. Verify data appears in analytics

## Expected Results

After integration, your analytics dashboard should show:

### Traffic Metrics
- **Total Visits**: Number of sessions
- **Unique Visitors**: Distinct users
- **Bounce Rate**: Percentage of single-page sessions
- **Avg Session**: Average session duration
- **Page Views**: Total page views

### Conversion Funnel
- **Visitors**: Total website visitors
- **Product Views**: Product page visits
- **Add to Cart**: Cart addition events
- **Checkout**: Checkout process starts
- **Completed**: Successful purchases

## Configuration

### Environment Variables
No additional environment variables required. The tracking system uses existing database configuration.

### CORS Configuration
Ensure your backend allows CORS for tracking requests from your frontend domain.

## Troubleshooting

### Common Issues

1. **No tracking data appearing**
   - Check if sample data was generated
   - Verify frontend is sending tracking requests
   - Check browser console for errors

2. **CORS errors**
   - Ensure backend CORS configuration includes frontend domain
   - Check if tracking endpoints are accessible

3. **Session not persisting**
   - Verify sessionStorage is available
   - Check if tracking is enabled

### Debug Mode
Enable debug logging by opening browser console. The tracking library logs all activities.

## Performance Considerations

- Tracking requests are sent asynchronously
- Failed requests don't break user experience
- Session data is stored in sessionStorage
- Minimal impact on page load times

## Privacy Compliance

- No personally identifiable information is collected by default
- User ID is only tracked if user is logged in
- IP addresses are not stored in frontend
- All tracking can be disabled by user preference
