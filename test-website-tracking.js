/**
 * Test script for website performance tracking
 * Run this with: node test-website-tracking.js
 */

const BASE_URL = 'http://localhost:8080';

async function testTrackingEndpoints() {
    console.log('🧪 Testing Website Performance Tracking Implementation...\n');
    
    try {
        // Test 1: Generate sample data
        console.log('1️⃣ Generating sample data...');
        const sampleDataResponse = await fetch(`${BASE_URL}/api/admin/sample-data/website-traffic?days=7&sessionsPerDay=20`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // Add your admin token here if needed
                // 'Authorization': 'Bearer YOUR_ADMIN_TOKEN'
            }
        });
        
        if (sampleDataResponse.ok) {
            const sampleData = await sampleDataResponse.json();
            console.log('✅ Sample data generated:', sampleData);
        } else {
            console.log('⚠️ Sample data generation failed:', sampleDataResponse.status);
        }
        
        // Test 2: Check traffic analytics
        console.log('\n2️⃣ Checking traffic analytics...');
        const trafficResponse = await fetch(`${BASE_URL}/api/admin/analytics/performance/traffic?range=7d`, {
            headers: {
                // Add your admin token here if needed
                // 'Authorization': 'Bearer YOUR_ADMIN_TOKEN'
            }
        });
        
        if (trafficResponse.ok) {
            const trafficData = await trafficResponse.json();
            console.log('✅ Traffic analytics:', JSON.stringify(trafficData, null, 2));
        } else {
            console.log('⚠️ Traffic analytics failed:', trafficResponse.status);
        }
        
        // Test 3: Check conversion funnel
        console.log('\n3️⃣ Checking conversion funnel...');
        const funnelResponse = await fetch(`${BASE_URL}/api/admin/analytics/performance/conversion-funnel?range=7d`, {
            headers: {
                // Add your admin token here if needed
                // 'Authorization': 'Bearer YOUR_ADMIN_TOKEN'
            }
        });
        
        if (funnelResponse.ok) {
            const funnelData = await funnelResponse.json();
            console.log('✅ Conversion funnel:', JSON.stringify(funnelData, null, 2));
        } else {
            console.log('⚠️ Conversion funnel failed:', funnelResponse.status);
        }
        
        // Test 4: Test tracking endpoints
        console.log('\n4️⃣ Testing tracking endpoints...');
        
        const trackingData = {
            sessionId: 'test-session-' + Date.now(),
            page: '/test-page',
            referrer: 'https://google.com',
            userAgent: 'Mozilla/5.0 (Test Browser)',
            ipAddress: '127.0.0.1',
            userId: null
        };
        
        // Test session start
        const sessionStartResponse = await fetch(`${BASE_URL}/api/tracking/session-start`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(trackingData)
        });
        
        if (sessionStartResponse.ok) {
            console.log('✅ Session start tracking works');
        } else {
            console.log('⚠️ Session start tracking failed:', sessionStartResponse.status);
        }
        
        // Test page view
        const pageViewResponse = await fetch(`${BASE_URL}/api/tracking/page-view`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(trackingData)
        });
        
        if (pageViewResponse.ok) {
            console.log('✅ Page view tracking works');
        } else {
            console.log('⚠️ Page view tracking failed:', pageViewResponse.status);
        }
        
        // Test product view
        const productViewData = {
            sessionId: trackingData.sessionId,
            productId: 'test-product-123',
            userId: null
        };
        
        const productViewResponse = await fetch(`${BASE_URL}/api/tracking/product-view`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(productViewData)
        });
        
        if (productViewResponse.ok) {
            console.log('✅ Product view tracking works');
        } else {
            console.log('⚠️ Product view tracking failed:', productViewResponse.status);
        }
        
        console.log('\n🎉 Testing completed!');
        console.log('\n📊 Next steps:');
        console.log('1. Check your admin dashboard for analytics data');
        console.log('2. Integrate frontend tracking code');
        console.log('3. Test with real user interactions');
        
    } catch (error) {
        console.error('❌ Test failed:', error.message);
        console.log('\n🔧 Troubleshooting:');
        console.log('1. Make sure your backend is running on port 8080');
        console.log('2. Check if CORS is configured properly');
        console.log('3. Verify database connection');
    }
}

// Run the test
testTrackingEndpoints();
