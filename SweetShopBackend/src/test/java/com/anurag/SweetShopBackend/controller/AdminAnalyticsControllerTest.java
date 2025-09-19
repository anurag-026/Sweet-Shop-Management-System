package com.anurag.SweetShopBackend.controller;

import com.anurag.SweetShopBackend.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(AdminAnalyticsController.class)
class AdminAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    private Map<String, Object> mockSalesOverview;
    private List<Map<String, Object>> mockMonthlySales;
    private List<Map<String, Object>> mockTopProducts;
    private List<Map<String, Object>> mockSalesByCategory;
    private Map<String, Object> mockCustomerSummary;
    private List<Map<String, Object>> mockCustomerSegments;
    private Map<String, Object> mockTraffic;
    private Map<String, Object> mockConversionFunnel;
    private Map<String, Object> mockShippingMetrics;
    private List<Map<String, Object>> mockAlerts;
    private Map<String, Object> mockInventoryStatus;
    private List<Map<String, Object>> mockLowStockItems;

    @BeforeEach
    void setUp() {
        // Mock sales overview
        mockSalesOverview = new HashMap<>();
        mockSalesOverview.put("totalSales", 50000.0);
        mockSalesOverview.put("totalOrders", 150);
        mockSalesOverview.put("averageOrderValue", 333.33);

        // Mock monthly sales
        mockMonthlySales = Arrays.asList(
            createMonthlySalesData("2024-01", 10000.0),
            createMonthlySalesData("2024-02", 12000.0),
            createMonthlySalesData("2024-03", 15000.0)
        );

        // Mock top products
        mockTopProducts = Arrays.asList(
            createProductData("Sweet 1", 100, 5000.0),
            createProductData("Sweet 2", 80, 4000.0),
            createProductData("Sweet 3", 60, 3000.0)
        );

        // Mock sales by category
        mockSalesByCategory = Arrays.asList(
            createCategoryData("Chocolates", 20000.0),
            createCategoryData("Candies", 15000.0),
            createCategoryData("Cakes", 10000.0)
        );

        // Mock customer summary
        mockCustomerSummary = new HashMap<>();
        mockCustomerSummary.put("totalCustomers", 500);
        mockCustomerSummary.put("newCustomers", 50);
        mockCustomerSummary.put("returningCustomers", 450);

        // Mock customer segments
        mockCustomerSegments = Arrays.asList(
            createSegmentData("High Value", 50, 25000.0),
            createSegmentData("Medium Value", 200, 15000.0),
            createSegmentData("Low Value", 250, 10000.0)
        );

        // Mock traffic data
        mockTraffic = new HashMap<>();
        mockTraffic.put("totalVisits", 10000);
        mockTraffic.put("uniqueVisitors", 5000);
        mockTraffic.put("bounceRate", 0.25);

        // Mock conversion funnel
        mockConversionFunnel = new HashMap<>();
        mockConversionFunnel.put("visitors", 10000);
        mockConversionFunnel.put("addToCart", 2000);
        mockConversionFunnel.put("checkout", 500);
        mockConversionFunnel.put("purchases", 150);

        // Mock shipping metrics
        mockShippingMetrics = new HashMap<>();
        mockShippingMetrics.put("averageDeliveryTime", 3.5);
        mockShippingMetrics.put("onTimeDelivery", 0.95);
        mockShippingMetrics.put("totalShipments", 150);

        // Mock alerts
        mockAlerts = Arrays.asList(
            createAlertData("Low Stock", "Sweet 1 is running low", "WARNING"),
            createAlertData("High Sales", "Unusual spike in sales", "INFO")
        );

        // Mock inventory status
        mockInventoryStatus = new HashMap<>();
        mockInventoryStatus.put("totalItems", 100);
        mockInventoryStatus.put("lowStockItems", 5);
        mockInventoryStatus.put("outOfStockItems", 2);

        // Mock low stock items
        mockLowStockItems = Arrays.asList(
            createLowStockData("Sweet 1", 5),
            createLowStockData("Sweet 2", 3),
            createLowStockData("Sweet 3", 8)
        );
    }

    private Map<String, Object> createMonthlySalesData(String month, Double sales) {
        Map<String, Object> data = new HashMap<>();
        data.put("month", month);
        data.put("sales", sales);
        return data;
    }

    private Map<String, Object> createProductData(String name, Integer quantity, Double revenue) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("quantity", quantity);
        data.put("revenue", revenue);
        return data;
    }

    private Map<String, Object> createCategoryData(String category, Double sales) {
        Map<String, Object> data = new HashMap<>();
        data.put("category", category);
        data.put("sales", sales);
        return data;
    }

    private Map<String, Object> createSegmentData(String segment, Integer count, Double value) {
        Map<String, Object> data = new HashMap<>();
        data.put("segment", segment);
        data.put("count", count);
        data.put("value", value);
        return data;
    }

    private Map<String, Object> createAlertData(String title, String message, String level) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("message", message);
        data.put("level", level);
        return data;
    }

    private Map<String, Object> createLowStockData(String name, Integer quantity) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("quantity", quantity);
        return data;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetSalesOverview_Success() throws Exception {
        // Given
        when(analyticsService.getSalesOverview("30d")).thenReturn(mockSalesOverview);

        // When & Then
        mockMvc.perform(get("/api/admin/analytics/sales-overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSales").value(50000.0))
                .andExpect(jsonPath("$.totalOrders").value(150))
                .andExpect(jsonPath("$.averageOrderValue").value(333.33));

        verify(analyticsService).getSalesOverview("30d");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetSalesOverview_WithCustomRange() throws Exception {
        // Given
        when(analyticsService.getSalesOverview("7d")).thenReturn(mockSalesOverview);

        // When & Then
        mockMvc.perform(get("/api/admin/analytics/sales-overview")
                .param("range", "7d"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSales").value(50000.0));

        verify(analyticsService).getSalesOverview("7d");
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetSalesOverview_AccessDenied() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/analytics/sales-overview"))
                .andExpect(status().isForbidden());

        verify(analyticsService, never()).getSalesOverview(anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetMonthlySales_Success() throws Exception {
        // Given
        when(analyticsService.getMonthlySales(12)).thenReturn(mockMonthlySales);

        // When & Then
        mockMvc.perform(get("/api/admin/analytics/monthly-sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].month").value("2024-01"))
                .andExpect(jsonPath("$[0].sales").value(10000.0));

        verify(analyticsService).getMonthlySales(12);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetMonthlySales_WithCustomMonths() throws Exception {
        // Given
        when(analyticsService.getMonthlySales(6)).thenReturn(mockMonthlySales);

        // When & Then
        mockMvc.perform(get("/api/admin/analytics/monthly-sales")
                .param("months", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(analyticsService).getMonthlySales(6);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTopProducts_Success() throws Exception {
        // Given
        when(analyticsService.getTopProducts(5)).thenReturn(mockTopProducts);

        // When & Then
        mockMvc.perform(get("/api/admin/analytics/top-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Sweet 1"))
                .andExpect(jsonPath("$[0].quantity").value(100));

        verify(analyticsService).getTopProducts(5);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTopProducts_WithCustomLimit() throws Exception {
        // Given
        when(analyticsService.getTopProducts(10)).thenReturn(mockTopProducts);

        // When & Then
        mockMvc.perform(get("/api/admin/analytics/top-products")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(analyticsService).getTopProducts(10);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetSalesByCategory_Success() throws Exception {
        // Given
        when(analyticsService.getSalesByCategory("30d")).thenReturn(mockSalesByCategory);

        // When & Then
        mockMvc.perform(get("/api/admin/analytics/sales-by-category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].category").value("Chocolates"))
                .andExpect(jsonPath("$[0].sales").value(20000.0));

        verify(analyticsService).getSalesByCategory("30d");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetCustomerSummary_Success() throws Exception {
        // Given
        when(analyticsService.getCustomerSummary("30d")).thenReturn(mockCustomerSummary);

        // When & Then
        mockMvc.perform(get("/api/admin/analytics/customers/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCustomers").value(500))
                .andExpect(jsonPath("$.newCustomers").value(50))
                .andExpect(jsonPath("$.returningCustomers").value(450));

        verify(analyticsService).getCustomerSummary("30d");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetCustomerSegments_Success() throws Exception {
        // Given
        when(analyticsService.getCustomerSegments("30d")).thenReturn(mockCustomerSegments);

        // When & Then
        mockMvc.perform(get("/api/admin/analytics/customers/segments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].segment").value("High Value"))
                .andExpect(jsonPath("$[0].count").value(50));

        verify(analyticsService).getCustomerSegments("30d");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTraffic_Success() throws Exception {
        // Given
        when(analyticsService.getWebsiteTraffic("30d")).thenReturn(mockTraffic);

        // When & Then
        mockMvc.perform(get("/api/admin/analytics/performance/traffic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVisits").value(10000))
                .andExpect(jsonPath("$.uniqueVisitors").value(5000))
                .andExpect(jsonPath("$.bounceRate").value(0.25));

        verify(analyticsService).getWebsiteTraffic("30d");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetConversionFunnel_Success() throws Exception {
        // Given
        when(analyticsService.getConversionFunnel("30d")).thenReturn(mockConversionFunnel);

        // When & Then
        mockMvc.perform(get("/api/admin/analytics/performance/conversion-funnel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visitors").value(10000))
                .andExpect(jsonPath("$.addToCart").value(2000))
                .andExpect(jsonPath("$.checkout").value(500))
                .andExpect(jsonPath("$.purchases").value(150));

        verify(analyticsService).getConversionFunnel("30d");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetShippingMetrics_Success() throws Exception {
        // Given
        when(analyticsService.getShippingMetrics("30d")).thenReturn(mockShippingMetrics);

        // When & Then
        mockMvc.perform(get("/api/admin/analytics/shipping-metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageDeliveryTime").value(3.5))
                .andExpect(jsonPath("$.onTimeDelivery").value(0.95))
                .andExpect(jsonPath("$.totalShipments").value(150));

        verify(analyticsService).getShippingMetrics("30d");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAlerts_Success() throws Exception {
        // Given
        when(analyticsService.getAlerts(20)).thenReturn(mockAlerts);

        // When & Then
        mockMvc.perform(get("/api/admin/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Low Stock"))
                .andExpect(jsonPath("$[0].level").value("WARNING"));

        verify(analyticsService).getAlerts(20);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAlerts_WithCustomLimit() throws Exception {
        // Given
        when(analyticsService.getAlerts(10)).thenReturn(mockAlerts);

        // When & Then
        mockMvc.perform(get("/api/admin/alerts")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(analyticsService).getAlerts(10);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetInventoryStatus_Success() throws Exception {
        // Given
        when(analyticsService.getInventoryStatus()).thenReturn(mockInventoryStatus);

        // When & Then
        mockMvc.perform(get("/api/admin/inventory/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(100))
                .andExpect(jsonPath("$.lowStockItems").value(5))
                .andExpect(jsonPath("$.outOfStockItems").value(2));

        verify(analyticsService).getInventoryStatus();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetLowStock_Success() throws Exception {
        // Given
        when(analyticsService.getLowStockItems(10)).thenReturn(mockLowStockItems);

        // When & Then
        mockMvc.perform(get("/api/admin/inventory/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Sweet 1"))
                .andExpect(jsonPath("$[0].quantity").value(5));

        verify(analyticsService).getLowStockItems(10);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetLowStock_WithCustomThreshold() throws Exception {
        // Given
        when(analyticsService.getLowStockItems(5)).thenReturn(mockLowStockItems);

        // When & Then
        mockMvc.perform(get("/api/admin/inventory/low-stock")
                .param("threshold", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(analyticsService).getLowStockItems(5);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllAnalyticsEndpoints_AccessDenied() throws Exception {
        // Test all analytics endpoints with USER role
        String[] endpoints = {
            "/api/admin/analytics/sales-overview",
            "/api/admin/analytics/monthly-sales",
            "/api/admin/analytics/top-products",
            "/api/admin/analytics/sales-by-category",
            "/api/admin/analytics/customers/summary",
            "/api/admin/analytics/customers/segments",
            "/api/admin/analytics/performance/traffic",
            "/api/admin/analytics/performance/conversion-funnel",
            "/api/admin/analytics/shipping-metrics",
            "/api/admin/alerts",
            "/api/admin/inventory/status",
            "/api/admin/inventory/low-stock"
        };

        for (String endpoint : endpoints) {
            mockMvc.perform(get(endpoint))
                    .andExpect(status().isForbidden());
        }
    }

    @Test
    void testGetAllAnalyticsEndpoints_NotAuthenticated() throws Exception {
        // Test all analytics endpoints without authentication
        String[] endpoints = {
            "/api/admin/analytics/sales-overview",
            "/api/admin/analytics/monthly-sales",
            "/api/admin/analytics/top-products",
            "/api/admin/analytics/sales-by-category",
            "/api/admin/analytics/customers/summary",
            "/api/admin/analytics/customers/segments",
            "/api/admin/analytics/performance/traffic",
            "/api/admin/analytics/performance/conversion-funnel",
            "/api/admin/analytics/shipping-metrics",
            "/api/admin/alerts",
            "/api/admin/inventory/status",
            "/api/admin/inventory/low-stock"
        };

        for (String endpoint : endpoints) {
            mockMvc.perform(get(endpoint))
                    .andExpect(status().isUnauthorized());
        }
    }
}
