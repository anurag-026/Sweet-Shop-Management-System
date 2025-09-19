# Sweet Shop Backend - Unit Test Suite

This document provides comprehensive information about the unit test suite for the Sweet Shop Backend API.

## Overview

The test suite includes comprehensive unit tests for all API controllers, covering every endpoint with multiple test scenarios including success cases, error handling, edge cases, and security validations.

## Test Structure

### Test Configuration
- **TestConfig.java**: Test-specific configuration including password encoder
- **BaseTestClass.java**: Base class with common test setup and utilities
- **application-test.properties**: Test-specific application properties using H2 in-memory database

### Controller Tests

#### 1. AuthControllerTest
Tests for authentication and user management endpoints:
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `POST /api/auth/refresh` - Token refresh
- `GET /api/auth/profile` - Get user profile
- `PATCH /api/auth/profile` - Update user profile

**Test Coverage:**
- ✅ Successful registration with valid data
- ✅ Registration with existing email (error case)
- ✅ Invalid input validation
- ✅ Successful login with valid credentials
- ✅ Login with invalid credentials
- ✅ Successful logout with valid token
- ✅ Logout with invalid token format
- ✅ Token refresh with valid token
- ✅ Token refresh with expired token
- ✅ Token refresh with blacklisted token
- ✅ Profile retrieval for authenticated user
- ✅ Profile update with valid data
- ✅ Profile update with partial data
- ✅ Unauthenticated access attempts

#### 2. SweetControllerTest
Tests for sweet management endpoints:
- `GET /api/sweets` - Get all sweets (with filters)
- `GET /api/sweets/{id}` - Get sweet by ID
- `POST /api/sweets` - Create sweet (Admin only)
- `PUT /api/sweets/{id}` - Update sweet (Admin only)
- `DELETE /api/sweets/{id}` - Delete sweet (Admin only)
- `POST /api/sweets/{id}/purchase` - Purchase sweet
- `POST /api/sweets/{id}/restock` - Restock sweet (Admin only)

**Test Coverage:**
- ✅ Get all sweets with and without filters
- ✅ Get sweet by valid ID
- ✅ Get sweet by invalid ID (error case)
- ✅ Create sweet with admin role
- ✅ Create sweet access denied for user role
- ✅ Update sweet with admin role
- ✅ Delete sweet with admin role
- ✅ Purchase sweet with valid quantity
- ✅ Purchase sweet with invalid quantity
- ✅ Restock sweet with admin role
- ✅ Input validation for all endpoints

#### 3. CartControllerTest
Tests for cart management endpoints:
- `GET /api/cart` - Get cart items
- `POST /api/cart/add` - Add item to cart
- `PUT /api/cart/{cartItemId}` - Update cart item quantity
- `DELETE /api/cart/{cartItemId}` - Remove item from cart
- `DELETE /api/cart/clear` - Clear entire cart

**Test Coverage:**
- ✅ Get cart items for authenticated user
- ✅ Add item to cart with valid data
- ✅ Add item to cart with invalid data
- ✅ Update cart item quantity
- ✅ Remove item from cart
- ✅ Clear entire cart
- ✅ User not found scenarios
- ✅ Unauthenticated access attempts

#### 4. OrderControllerTest
Tests for order management endpoints:
- `POST /api/orders/checkout` - Checkout cart
- `GET /api/orders` - Get user orders
- `GET /api/orders/{orderId}` - Get order by ID
- `PUT /api/orders/{orderId}/status` - Update order status (Admin only)
- `PUT /api/orders/{orderId}/tracking` - Update order tracking (Admin only)

**Test Coverage:**
- ✅ Checkout with and without request body
- ✅ Get user orders
- ✅ Get order by ID
- ✅ Update order status with admin role
- ✅ Update order tracking with admin role
- ✅ Access denied for non-admin users
- ✅ User not found scenarios
- ✅ Unauthenticated access attempts

#### 5. AdminControllerTest
Tests for admin dashboard endpoints:
- `GET /api/admin/dashboard` - Admin dashboard (Admin only)
- `GET /api/admin/system-status` - System status (Admin only)

**Test Coverage:**
- ✅ Dashboard with various user counts
- ✅ Dashboard with empty user list
- ✅ Dashboard with multiple admins
- ✅ System status endpoint
- ✅ Access denied for non-admin users
- ✅ Unauthenticated access attempts
- ✅ Exception handling

#### 6. AdminAnalyticsControllerTest
Tests for analytics endpoints (Admin only):
- `GET /api/admin/analytics/sales-overview` - Sales overview
- `GET /api/admin/analytics/monthly-sales` - Monthly sales data
- `GET /api/admin/analytics/top-products` - Top selling products
- `GET /api/admin/analytics/sales-by-category` - Sales by category
- `GET /api/admin/analytics/customers/summary` - Customer summary
- `GET /api/admin/analytics/customers/segments` - Customer segments
- `GET /api/admin/analytics/performance/traffic` - Website traffic
- `GET /api/admin/analytics/performance/conversion-funnel` - Conversion funnel
- `GET /api/admin/analytics/shipping-metrics` - Shipping metrics
- `GET /api/admin/alerts` - System alerts
- `GET /api/admin/inventory/status` - Inventory status
- `GET /api/admin/inventory/low-stock` - Low stock items

**Test Coverage:**
- ✅ All analytics endpoints with default parameters
- ✅ All analytics endpoints with custom parameters
- ✅ Access denied for non-admin users
- ✅ Unauthenticated access attempts
- ✅ Mock data validation

#### 7. AdminOrdersControllerTest
Tests for admin order management endpoints (Admin only):
- `GET /api/admin/orders` - List all orders
- `GET /api/admin/orders/recent` - Recent orders
- `PUT /api/admin/orders/{orderId}/status` - Update order status

**Test Coverage:**
- ✅ List orders with and without filters
- ✅ List orders with pagination
- ✅ Recent orders with custom limit
- ✅ Update order status with valid data
- ✅ Update order status with invalid order ID
- ✅ Update order status with invalid status
- ✅ Update order status with missing status
- ✅ Access denied for non-admin users
- ✅ Exception handling

#### 8. HealthControllerTest
Tests for health check endpoints:
- `GET /api/health/database` - Database health check
- `GET /api/health/ping` - Application ping

**Test Coverage:**
- ✅ Database health check with successful connection
- ✅ Database health check with connection failure
- ✅ Database health check with various exceptions
- ✅ Ping endpoint functionality
- ✅ Multiple calls consistency
- ✅ No authentication required
- ✅ Various header and parameter scenarios

## Test Features

### Security Testing
- ✅ Role-based access control (RBAC) validation
- ✅ Authentication requirement testing
- ✅ Authorization testing for admin endpoints
- ✅ JWT token validation
- ✅ Token blacklisting testing

### Input Validation
- ✅ Valid input testing
- ✅ Invalid input testing
- ✅ Missing required fields
- ✅ Boundary value testing
- ✅ Data type validation

### Error Handling
- ✅ Exception handling
- ✅ Error response validation
- ✅ HTTP status code validation
- ✅ Error message validation

### Edge Cases
- ✅ Empty data scenarios
- ✅ Null value handling
- ✅ Boundary conditions
- ✅ Concurrent access scenarios

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=AuthControllerTest
```

### Run Test Suite
```bash
mvn test -Dtest=AllControllerTests
```

### Run with Coverage
```bash
mvn test jacoco:report
```

## Test Configuration

### Database
- Uses H2 in-memory database for testing
- Automatic schema creation and cleanup
- No external database dependencies

### Security
- Mock authentication and authorization
- Test-specific JWT configuration
- Role-based testing scenarios

### Mocking
- Service layer mocking with Mockito
- Repository layer mocking
- External dependency mocking

## Test Data

### Test Users
- **Test User**: Regular user with ROLE_USER
- **Test Admin**: Admin user with ROLE_ADMIN
- **Mock Authentication**: Various authentication scenarios

### Test Entities
- **Sweet**: Test sweet products with various attributes
- **Order**: Test orders with different statuses
- **Cart Items**: Test cart items with quantities and prices
- **User Profiles**: Test user profiles with different data

## Coverage Metrics

The test suite provides comprehensive coverage for:
- ✅ All API endpoints (100%)
- ✅ All success scenarios
- ✅ All error scenarios
- ✅ All security validations
- ✅ All input validations
- ✅ All edge cases

## Best Practices

### Test Organization
- Each controller has its own test class
- Tests are grouped by functionality
- Clear test method naming
- Comprehensive test documentation

### Test Data Management
- Consistent test data setup
- Reusable test utilities
- Clean test data isolation
- Proper test cleanup

### Assertion Strategy
- Specific assertion validation
- JSON path assertions
- HTTP status validation
- Error message validation

### Mock Strategy
- Service layer mocking
- Repository layer mocking
- Authentication mocking
- External dependency mocking

## Maintenance

### Adding New Tests
1. Follow existing test patterns
2. Include success and error scenarios
3. Add security validation
4. Update this documentation

### Updating Tests
1. Maintain test coverage
2. Update test data as needed
3. Validate all scenarios
4. Update documentation

## Dependencies

### Test Dependencies
- Spring Boot Test
- Spring Security Test
- Mockito
- JUnit 5
- H2 Database (test scope)
- Jackson (JSON processing)

### Test Configuration
- Test-specific properties
- Mock configurations
- Security test configurations
- Database test configurations

This comprehensive test suite ensures the reliability, security, and functionality of all API endpoints in the Sweet Shop Backend application.
