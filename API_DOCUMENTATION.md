# Sweet Shop Backend API Documentation

## Table of Contents

1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Base URL](#base-url)
4. [Common Headers](#common-headers)
5. [Error Responses](#error-responses)
6. [Authentication APIs](#authentication-apis)
7. [Sweet Management APIs](#sweet-management-apis)
8. [Cart Management APIs](#cart-management-apis)
9. [Order Management APIs](#order-management-apis)
10. [Admin APIs](#admin-apis)
11. [Data Models](#data-models)
12. [Security Notes](#security-notes)

## Overview

The Sweet Shop Backend API provides a comprehensive REST API for managing a sweet shop inventory system. It includes user authentication, product management, shopping cart functionality, order processing, and administrative features.

**Key Features:**

- JWT-based authentication
- Role-based access control (USER/ADMIN)
- Product catalog management
- Shopping cart functionality
- Order processing
- Admin dashboard

### Data Source Notes

- All Admin Analytics endpoints return values computed from your database in the requested range. When no data exists, numeric fields return 0 and lists return [].
- Order checkout supports optional payment metadata (Credit Card/PayPal) and shipping/customer notes.
- Order status values include: PENDING, CONFIRMED, PROCESSING, SHIPPED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED, REFUNDED.

## Authentication

The API uses JWT (JSON Web Token) for authentication. Include the JWT token in the Authorization header for protected endpoints:

```
Authorization: Bearer <your-jwt-token>
```

## Base URL

```
http://localhost:8081
```

## Common Headers

### Request Headers

```
Content-Type: application/json
Authorization: Bearer <jwt-token>  (for protected endpoints)
```

### Response Headers

```
Content-Type: application/json
```

## Error Responses

All error responses follow this format:

```json
{
  "timestamp": "2025-09-19T04:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error description",
  "path": "/api/endpoint"
}
```

### Common HTTP Status Codes

- `200` - OK
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `500` - Internal Server Error

---

## Authentication APIs

### 1. Register User

**Endpoint:** `POST /api/auth/register`

**Description:** Register a new user account

**Access:** Public

**Request Body:**

```json
{
  "fullName": "John Doe",
  "email": "user@example.com",
  "password": "password123"
}
```

**Request Headers:**

```
Content-Type: application/json
```

**Response (Success - 200):**

```json
"User registered successfully!"
```

**Response (Error - 400):**

```json
{
  "timestamp": "2025-09-19T04:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error: Email is already registered!",
  "path": "/api/auth/register"
}
```

**Validation Rules:**

- Full name is required
- Email must be valid format
- Password is required
- Email must be unique

---

### 2. Login User

**Endpoint:** `POST /api/auth/login`

**Description:** Authenticate user and return JWT token

**Access:** Public

**Request Body:**

```json
{
  "email": "user@sweetshop.com",
  "password": "user123"
}
```

**Request Headers:**

```
Content-Type: application/json
```

**Response (Success - 200):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTYzMjA0ODAwMCwiZXhwIjoxNjMyMTM0NDAwfQ.signature",
  "email": "user@example.com",
  "role": "ROLE_USER"
}
```

**Response (Error - 401):**

```json
{
  "timestamp": "2025-09-19T04:30:00",
  "status": 401,
  "error": "Authentication Failed",
  "message": "Invalid email or password",
  "path": "/api/auth/login"
}
```

---

### 3. Logout User

**Endpoint:** `POST /api/auth/logout`

**Description:** Logout user (invalidates the token and clears security context)

**Access:** Authenticated

**Request Headers:**

```
Authorization: Bearer <jwt-token>
```

**Response (Success - 200):**

```json
"User logged out successfully!"
```

**Response (Error - 400):**

```json
{
  "timestamp": "2025-09-19T04:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid token format",
  "path": "/api/auth/logout"
}
```

**Notes:**

- This endpoint invalidates the provided token by adding it to a blacklist
- The token will no longer be usable for any API calls
- Attempting to use an invalidated token will result in a 401 Unauthorized response

---

### 4. Refresh Token

**Endpoint:** `POST /api/auth/refresh`

**Description:** Refresh JWT token to get a new valid token

**Access:** Public (requires token in header, even if expired)

**Request Headers:**

```
Authorization: Bearer <jwt-token>
```

**Response (Success - 200):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTYzMjA0ODAwMCwiZXhwIjoxNjMyMTM0NDAwfQ.signature",
  "email": "user@example.com",
  "role": "ROLE_USER"
}
```

**Response (Error - 401):**

```json
{
  "error": "Unauthorized",
  "message": "Token has been invalidated. Please login again.",
  "timestamp": 1632048000000
}
```

**Notes:**

- This endpoint allows refreshing an existing token
- The token can be expired but must have a valid signature
- Returns a new token with extended expiration time (1 minute)
- The old token is automatically invalidated (blacklisted) when refreshed
- Critical for maintaining user sessions with short-lived tokens
- Frontend should call this endpoint before token expiration (every ~50 seconds)
- If refresh fails, user must login again

---

### 5. Get User Profile

**Endpoint:** `GET /api/auth/profile`

**Description:** Get current user profile information

**Access:** Authenticated

**Request Headers:**

```
Authorization: Bearer <jwt-token>
```

**Response (Success - 200):**

```json
{
  "name": "John Doe",
  "email": "user@example.com",
  "phone": "+1234567890",
  "address": "123 Main Street, City, State 12345",
  "role": "ROLE_USER"
}
```

**Response (Error - 401):**

```json
{
  "timestamp": "2025-09-19T04:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "User not authenticated",
  "path": "/api/auth/profile"
}
```

---

### 6. Update User Profile

**Endpoint:** `PATCH /api/auth/profile`

**Description:** Update user profile information (name, phone, address)

**Access:** Authenticated

**Request Headers:**

```
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

**Important:** Make sure to set the `Content-Type` header to `application/json` when making this request. Many API testing tools default to `text/plain` which will cause a 415 Unsupported Media Type error.

**Request Body:**

```json
{
  "name": "John Smith",
  "phone": "+1987654321",
  "address": "456 Oak Avenue, New City, State 54321"
}
```

**Validation Rules:**

- Name is required
- Phone and address are optional
- All fields are validated for proper format

**Response (Success - 200):**

```json
{
  "name": "John Smith",
  "email": "user@example.com",
  "phone": "+1987654321",
  "address": "456 Oak Avenue, New City, State 54321",
  "role": "ROLE_USER"
}
```

**Response (Error - 400):**

```json
{
  "timestamp": "2025-09-19T04:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Name is required",
  "path": "/api/auth/profile"
}
```

**Response (Error - 401):**

```json
{
  "timestamp": "2025-09-19T04:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "User not authenticated",
  "path": "/api/auth/profile"
}
```

**Response (Error - 415):**

```json
{
  "timestamp": "2025-09-19T05:22:43.9096184",
  "status": 415,
  "error": "Unsupported Media Type",
  "message": "Please ensure your request includes the correct Content-Type header. For JSON requests, use 'Content-Type: application/json'. The server received: Content-Type 'text/plain;charset=UTF-8' is not supported",
  "path": "/api/auth/profile"
}
```

**Troubleshooting:**

- **415 Error:** Make sure your request includes `Content-Type: application/json` header
- **400 Error:** Check that your JSON is properly formatted and all required fields are provided
- **401 Error:** Ensure you're sending a valid JWT token in the Authorization header

---

## Sweet Management APIs

### 1. Get All Sweets

**Endpoint:** `GET /api/sweets`

**Description:** Retrieve all available sweets with optional search and filter parameters

**Access:** Authenticated

**Query Parameters:**

- `name` (String, optional) - Search by name (partial match, case-insensitive)
- `category` (String, optional) - Search by category (partial match, case-insensitive)
- `min` (Double, optional) - Minimum price filter
- `max` (Double, optional) - Maximum price filter

**Request Headers:**

```
Authorization: Bearer <jwt-token>
```

**Example Requests:**

```
GET /api/sweets
GET /api/sweets?name=chocolate
GET /api/sweets?category=truffles
GET /api/sweets?min=10.0&max=50.0
GET /api/sweets?name=chocolate&category=truffles&min=10.0&max=50.0
```

**Response (Success - 200):**

```json
[
  {
    "id": 1,
    "name": "Belgian Dark Chocolate Truffles",
    "category": "chocolate",
    "price": 24.99,
    "quantity": 15,
    "description": "Luxurious handcrafted truffles made with 70% Belgian dark chocolate and premium cocoa powder.",
    "image": "elegant-dark-chocolate-truffles.jpg"
  },
  {
    "id": 2,
    "name": "French Macarons Assortment",
    "category": "macarons",
    "price": 32.99,
    "quantity": 8,
    "description": "Delicate almond-based cookies with smooth ganache filling in six exquisite flavors.",
    "image": "colorful-french-macarons-assortment.jpg"
  }
]
```

---

### 2. Get Sweet by ID

**Endpoint:** `GET /api/sweets/{id}`

**Description:** Retrieve a specific sweet by its ID

**Access:** Authenticated

**Path Parameters:**

- `id` (UUID) - Sweet ID

**Request Headers:**

```
Authorization: Bearer <jwt-token>
```

**Response (Success - 200):**

```json
{
  "id": 1,
  "name": "Belgian Dark Chocolate Truffles",
  "category": "chocolate",
  "price": 24.99,
  "quantity": 15,
  "description": "Luxurious handcrafted truffles made with 70% Belgian dark chocolate and premium cocoa powder.",
  "image": "elegant-dark-chocolate-truffles.jpg"
}
```

**Response (Error - 404):**

```json
{
  "timestamp": "2025-09-19T04:30:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Sweet not found with id: 999",
  "path": "/api/sweets/999"
}
```

---

### 3. Create Sweet

**Endpoint:** `POST /api/sweets`

**Description:** Create a new sweet (Admin only)

**Access:** Admin

**Request Headers:**

```
Authorization: Bearer <admin-jwt-token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "name": "Mysore Pak",
  "category": "South Indian",
  "price": 45.0,
  "quantity": 15,
  "description": "Traditional South Indian sweet made with gram flour, ghee, and sugar",
  "image": "mysore-pak-traditional.jpg"
}
```

**Response (Success - 200):**

```json
{
  "id": 3,
  "name": "Mysore Pak",
  "category": "South Indian",
  "price": 45.0,
  "quantity": 15,
  "description": "Traditional South Indian sweet made with gram flour, ghee, and sugar",
  "image": "mysore-pak-traditional.jpg"
}
```

**Response (Error - 403):**

```json
{
  "timestamp": "2025-09-19T04:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "You don't have permission to access this resource. Admin privileges required.",
  "path": "/api/sweets"
}
```

---

### 4. Update Sweet

**Endpoint:** `PUT /api/sweets/{id}`

**Description:** Update an existing sweet (Admin only)

**Access:** Admin

**Path Parameters:**

- `id` (UUID) - Sweet ID

**Request Headers:**

```
Authorization: Bearer <admin-jwt-token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "name": "Gulab Jamun",
  "category": "Indian",
  "price": 45.0,
  "quantity": 25,
  "description": "Soft and spongy milk-based sweet balls soaked in rose-flavored sugar syrup",
  "image": "gulab-jamun-indian-sweet.jpg"
}
```

**Response (Success - 200):**

```json
{
  "id": 1,
  "name": "Gulab Jamun",
  "category": "Indian",
  "price": 45.0,
  "quantity": 25,
  "description": "Soft and spongy milk-based sweet balls soaked in rose-flavored sugar syrup",
  "image": "gulab-jamun-indian-sweet.jpg"
}
```

---

### 5. Delete Sweet

**Endpoint:** `DELETE /api/sweets/{id}`

**Description:** Delete a sweet (Admin only)

**Access:** Admin

**Path Parameters:**

- `id` (UUID) - Sweet ID

**Request Headers:**

```
Authorization: Bearer <admin-jwt-token>
```

**Response (Success - 200):**

```
(Empty response body)
```

---

### 6. Purchase Sweet

**Endpoint:** `POST /api/sweets/{id}/purchase`

**Description:** Purchase a sweet (decrease quantity)

**Access:** Authenticated

**Path Parameters:**

- `id` (UUID) - Sweet ID

**Query Parameters:**

- `qty` (Integer, required) - Quantity to purchase (must be greater than 0)

**Request Headers:**

```
Authorization: Bearer <jwt-token>
```

**Example Request:**

```
POST /api/sweets/94e31aff-b1ad-4f48-bc91-46ef369ec05e/purchase?qty=2
```

**Response (Success - 200):**

```json
{
  "id": "94e31aff-b1ad-4f48-bc91-46ef369ec05e",
  "name": "Belgian Dark Chocolate Truffles",
  "category": "chocolate",
  "price": 24.99,
  "quantity": 14,
  "description": "Luxurious handcrafted truffles made with 70% Belgian dark chocolate and premium cocoa powder.",
  "image": "elegant-dark-chocolate-truffles.jpg"
}
```

**Response (Error - 400):**

```json
{
  "timestamp": "2025-09-19T04:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Quantity must be provided and must be greater than 0",
  "path": "/api/sweets/94e31aff-b1ad-4f48-bc91-46ef369ec05e/purchase"
}
```

**Response (Error - 400 - Insufficient Stock):**

```json
{
  "timestamp": "2025-09-19T04:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Not enough stock available for sweet: Gulab Jamun. Available: 5, Requested: 10",
  "path": "/api/sweets/94e31aff-b1ad-4f48-bc91-46ef369ec05e/purchase"
}
```

---

### 7. Restock Sweet

**Endpoint:** `POST /api/sweets/{id}/restock`

**Description:** Restock a sweet (increase quantity) (Admin only)

**Access:** Admin

**Path Parameters:**

- `id` (UUID) - Sweet ID

**Query Parameters:**

- `qty` (Integer, optional) - Quantity to add (default: 1)

**Request Headers:**

```
Authorization: Bearer <admin-jwt-token>
```

**Example Request:**

```
POST /api/sweets/94e31aff-b1ad-4f48-bc91-46ef369ec05e/restock?qty=10
```

**Response (Success - 200):**

```json
{
  "id": "94e31aff-b1ad-4f48-bc91-46ef369ec05e",
  "name": "Belgian Dark Chocolate Truffles",
  "category": "chocolate",
  "price": 24.99,
  "quantity": 24,
  "description": "Luxurious handcrafted truffles made with 70% Belgian dark chocolate and premium cocoa powder.",
  "image": "elegant-dark-chocolate-truffles.jpg"
}
```

---

## Cart Management APIs

### 1. Get Cart Items

**Endpoint:** `GET /api/cart`

**Description:** Get all items in the user's cart

**Access:** Authenticated

**Request Headers:**

```
Authorization: Bearer <jwt-token>
```

**Response (Success - 200):**

```json
[
  {
    "id": 1,
    "sweetId": "94e31aff-b1ad-4f48-bc91-46ef369ec05e",
    "sweetName": "Belgian Dark Chocolate Truffles",
    "category": "chocolate",
    "price": 24.99,
    "quantity": 2,
    "totalPrice": 49.98,
    "description": "Luxurious handcrafted truffles made with 70% Belgian dark chocolate and premium cocoa powder.",
    "image": "elegant-dark-chocolate-truffles.jpg"
  }
]
```

---

### 2. Add Item to Cart

**Endpoint:** `POST /api/cart/add`

**Description:** Add a sweet to the cart

**Access:** Authenticated

**Request Headers:**

```
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "sweetId": "94e31aff-b1ad-4f48-bc91-46ef369ec05e",
  "quantity": 2
}
```

**Response (Success - 200):**

```json
{
  "id": 1,
  "sweetId": "94e31aff-b1ad-4f48-bc91-46ef369ec05e",
  "sweetName": "Belgian Dark Chocolate Truffles",
  "category": "chocolate",
  "price": 24.99,
  "quantity": 2,
  "totalPrice": 49.98,
  "description": "Luxurious handcrafted truffles made with 70% Belgian dark chocolate and premium cocoa powder.",
  "image": "elegant-dark-chocolate-truffles.jpg"
}
```

**Response (Error - 400):**

```json
{
  "timestamp": "2025-09-19T04:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Not enough stock available for sweet: Belgian Dark Chocolate Truffles",
  "path": "/api/cart/add"
}
```

---

### 3. Update Cart Item Quantity

**Endpoint:** `PUT /api/cart/{cartItemId}`

**Description:** Update the quantity of a cart item

**Access:** Authenticated

**Path Parameters:**

- `cartItemId` (Long) - Cart item ID

**Query Parameters:**

- `quantity` (Integer) - New quantity

**Request Headers:**

```
Authorization: Bearer <jwt-token>
```

**Example Request:**

```
PUT /api/cart/1?quantity=3
```

**Response (Success - 200):**

```
(Empty response body)
```

---

### 4. Remove Item from Cart

**Endpoint:** `DELETE /api/cart/{cartItemId}`

**Description:** Remove an item from the cart

**Access:** Authenticated

**Path Parameters:**

- `cartItemId` (Long) - Cart item ID

**Request Headers:**

```
Authorization: Bearer <jwt-token>
```

**Response (Success - 200):**

```
(Empty response body)
```

---

### 5. Clear Cart

**Endpoint:** `DELETE /api/cart/clear`

**Description:** Remove all items from the cart

**Access:** Authenticated

**Request Headers:**

```
Authorization: Bearer <jwt-token>
```

**Response (Success - 200):**

```
(Empty response body)
```

---

## Order Management APIs

### 1. Checkout (Create Order)

**Endpoint:** `POST /api/orders/checkout`

**Description:** Create an order from cart items. Supports optional payment and shipping details.

**Access:** Authenticated

**Request Headers:**

```
Authorization: Bearer <jwt-token>
```

**Request Body (optional):**

```json
{
  "paymentMode": "CREDIT_CARD",
  "paymentDetails": {
    "cardNumber": "1234 5678 9012 3456",
    "expiryDate": "12/26",
    "cvv": "123",
    "cardholderName": "John Doe",
    "paypalEmail": null,
    "transactionId": null
  },
  "shippingAddress": "221B Baker Street, London",
  "customerNotes": "Please deliver after 5 PM"
}
```

Notes:

- If no body is sent, the order will still be created with defaults.
- For PayPal, set `paymentMode` to `PAYPAL` and provide `paypalEmail` in `paymentDetails`.

**Response (Success - 200):**

```json
{
  "id": 1,
  "username": "user@example.com",
  "orderItems": [
    {
      "id": 1,
      "sweetId": "94e31aff-b1ad-4f48-bc91-46ef369ec05e",
      "sweetName": "Belgian Dark Chocolate Truffles",
      "category": "chocolate",
      "price": 24.99,
      "quantity": 2,
      "totalPrice": 49.98,
      "description": "Luxurious handcrafted truffles made with 70% Belgian dark chocolate and premium cocoa powder.",
      "image": "elegant-dark-chocolate-truffles.jpg"
    }
  ],
  "totalAmount": 49.98,
  "status": "PENDING",
  "orderDate": "2025-09-19T04:30:00",
  "paymentMode": "CREDIT_CARD",
  "paymentTransactionId": "CC_1695111111111_AB12CD34",
  "shippingAddress": "221B Baker Street, London",
  "customerNotes": "Please deliver after 5 PM",
  "trackingNumber": null,
  "estimatedDeliveryDate": "2025-09-26T12:00:00",
  "actualDeliveryDate": null
}
```

**Response (Error - 400):**

```json
{
  "timestamp": "2025-09-19T04:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Cart is empty. Cannot checkout.",
  "path": "/api/orders/checkout"
}
```

---

### 2. Get User Orders

**Endpoint:** `GET /api/orders`

**Description:** Get all orders for the current user

**Access:** Authenticated

**Request Headers:**

```
Authorization: Bearer <jwt-token>
```

**Response (Success - 200):**

```json
[
  {
    "id": 1,
    "username": "user@example.com",
    "orderItems": [
      {
        "id": 1,
        "sweetId": "94e31aff-b1ad-4f48-bc91-46ef369ec05e",
        "sweetName": "Belgian Dark Chocolate Truffles",
        "category": "chocolate",
        "price": 24.99,
        "quantity": 2,
        "totalPrice": 49.98,
        "description": "Luxurious handcrafted truffles made with 70% Belgian dark chocolate and premium cocoa powder.",
        "image": "elegant-dark-chocolate-truffles.jpg"
      }
    ],
    "totalAmount": 49.98,
    "status": "PENDING",
    "orderDate": "2025-09-19T04:30:00"
  }
]
```

---

### 3. Get Order by ID

**Endpoint:** `GET /api/orders/{orderId}`

**Description:** Get a specific order by ID

**Access:** Authenticated

**Path Parameters:**

- `orderId` (Long) - Order ID

**Request Headers:**

```
Authorization: Bearer <jwt-token>
```

**Response (Success - 200):**

```json
{
  "id": 1,
  "username": "user@example.com",
  "orderItems": [
    {
      "id": 1,
      "sweetId": "94e31aff-b1ad-4f48-bc91-46ef369ec05e",
      "sweetName": "Belgian Dark Chocolate Truffles",
      "category": "chocolate",
      "price": 24.99,
      "quantity": 2,
      "totalPrice": 49.98,
      "description": "Luxurious handcrafted truffles made with 70% Belgian dark chocolate and premium cocoa powder.",
      "image": "elegant-dark-chocolate-truffles.jpg"
    }
  ],
  "totalAmount": 49.98,
  "status": "PENDING",
  "orderDate": "2025-09-19T04:30:00"
}
```

---

### 4. Update Order Status

**Endpoint:** `PUT /api/orders/{orderId}/status`

**Description:** Update the status of an order (Admin only)

**Access:** Admin

**Path Parameters:**

- `orderId` (Long) - Order ID

**Query Parameters:**

- `status` (String) - New status. Allowed values:
  - `PENDING`, `CONFIRMED`, `PROCESSING`, `SHIPPED`, `OUT_FOR_DELIVERY`, `DELIVERED`, `CANCELLED`, `REFUNDED`

**Request Headers:**

```
Authorization: Bearer <admin-jwt-token>
```

**Example Request:**

```
PUT /api/orders/1/status?status=CONFIRMED
```

**Response (Success - 200):**

```json
{
  "id": 1,
  "username": "user@example.com",
  "orderItems": [
    {
      "id": 1,
      "sweetId": "94e31aff-b1ad-4f48-bc91-46ef369ec05e",
      "sweetName": "Belgian Dark Chocolate Truffles",
      "category": "chocolate",
      "price": 24.99,
      "quantity": 2,
      "totalPrice": 49.98,
      "description": "Luxurious handcrafted truffles made with 70% Belgian dark chocolate and premium cocoa powder.",
      "image": "elegant-dark-chocolate-truffles.jpg"
    }
  ],
  "totalAmount": 49.98,
  "status": "CONFIRMED",
  "orderDate": "2025-09-19T04:30:00"
}
```

### 5. Update Order Tracking

**Endpoint:** `PUT /api/orders/{orderId}/tracking`

**Description:** Update tracking number for an order (Admin only)

**Access:** Admin

**Path Parameters:**

- `orderId` (UUID) - Order ID

**Query Parameters:**

- `trackingNumber` (String, required)

**Request Headers:**

```
Authorization: Bearer <admin-jwt-token>
```

**Response (Success - 200):**

```json
{
  "id": 1,
  "username": "user@example.com",
  "trackingNumber": "TRK16951122223333ABCD",
  "status": "SHIPPED"
}
```

---

## Admin APIs

### 1. Admin Dashboard

**Endpoint:** `GET /api/admin/dashboard`

**Description:** Get admin dashboard statistics

**Access:** Admin

**Request Headers:**

```
Authorization: Bearer <admin-jwt-token>
```

**Response (Success - 200):**

```json
{
  "totalUsers": 10,
  "adminUsers": 1,
  "regularUsers": 9,
  "adminEmail": "admin@sweetshop.com"
}
```

---

### 2. System Status

**Endpoint:** `GET /api/admin/system-status`

**Description:** Get system status information

**Access:** Admin

**Request Headers:**

```
Authorization: Bearer <admin-jwt-token>
```

**Response (Success - 200):**

```json
{
  "status": "healthy",
  "version": "1.0.0",
  "environment": "production",
  "serverTime": 1632048000000
}
```

---

### 3. Admin Analytics

All endpoints below require Admin access and return analytics data for the dashboard. Query parameters like `range` accept values such as `7d`, `30d`, `90d`, `1y` unless specified.

#### 3.1 Sales Overview

**Endpoint:** `GET /api/admin/analytics/sales-overview?range=30d`

**Response (Success - 200):**

```json
{
  "range": "30d",
  "totalRevenue": 45680.5,
  "totalOrders": 1247,
  "averageOrderValue": 36.65,
  "conversionRate": 3.2,
  "revenueGrowth": 12.5,
  "ordersGrowth": 8.3,
  "avgOrderGrowth": 4.1,
  "conversionGrowth": -0.8
}
```

#### 3.2 Monthly Sales

**Endpoint:** `GET /api/admin/analytics/monthly-sales?months=12`

**Response (Success - 200):**

```json
[
  { "month": "Feb 2023", "revenue": 32150.0, "orders": 856, "growth": 5.2 },
  { "month": "Mar 2023", "revenue": 34520.0, "orders": 923, "growth": 7.4 },
  { "month": "Apr 2023", "revenue": 28950.0, "orders": 745, "growth": -16.1 },
  { "month": "May 2023", "revenue": 36780.0, "orders": 987, "growth": 27.0 },
  { "month": "Jun 2023", "revenue": 41230.0, "orders": 1105, "growth": 12.1 },
  { "month": "Jul 2023", "revenue": 38950.0, "orders": 1023, "growth": -5.5 },
  { "month": "Aug 2023", "revenue": 42340.0, "orders": 1156, "growth": 8.7 },
  { "month": "Sep 2023", "revenue": 45680.0, "orders": 1247, "growth": 7.9 },
  { "month": "Oct 2023", "revenue": 48920.0, "orders": 1324, "growth": 7.1 },
  { "month": "Nov 2023", "revenue": 52340.0, "orders": 1456, "growth": 7.0 },
  { "month": "Dec 2023", "revenue": 67890.0, "orders": 1890, "growth": 29.7 },
  { "month": "Jan 2024", "revenue": 45680.5, "orders": 1247, "growth": -32.7 }
]
```

#### 3.3 Top Products

**Endpoint:** `GET /api/admin/analytics/top-products?limit=5`

**Response (Success - 200):**

```json
[
  {
    "id": "prod-1",
    "name": "Artisan Gummy Bears",
    "category": "Gummies",
    "unitsSold": 245,
    "revenue": 3675.0,
    "profit": 1470.0,
    "profitMargin": 40.0,
    "stock": 15,
    "image": "https://i.postimg.cc/sMTg12Kg/artisan-gummy-bears-colorful.jpg"
  },
  {
    "id": "prod-2",
    "name": "Chocolate Truffles",
    "category": "Chocolate",
    "unitsSold": 198,
    "revenue": 4950.0,
    "profit": 1980.0,
    "profitMargin": 40.0,
    "stock": 8,
    "image": "https://i.postimg.cc/dZRqFQLv/elegant-dark-chocolate-truffles.jpg"
  },
  {
    "id": "prod-3",
    "name": "French Macarons",
    "category": "Macarons",
    "unitsSold": 156,
    "revenue": 3120.0,
    "profit": 1248.0,
    "profitMargin": 40.0,
    "stock": 22,
    "image": "https://i.postimg.cc/JHC1SH7P/colorful-french-macarons-assortment.jpg"
  },
  {
    "id": "prod-4",
    "name": "Salted Caramel Bonbons",
    "category": "Caramel",
    "unitsSold": 134,
    "revenue": 2680.0,
    "profit": 1072.0,
    "profitMargin": 40.0,
    "stock": 12,
    "image": "https://i.postimg.cc/KKKxzDR0/salted-caramel-bonbons-chocolate.jpg"
  },
  {
    "id": "prod-5",
    "name": "Honey Lavender Lollipops",
    "category": "Lollipops",
    "unitsSold": 98,
    "revenue": 1470.0,
    "profit": 588.0,
    "profitMargin": 40.0,
    "stock": 6,
    "image": "https://i.postimg.cc/z3TDK7pM/honey-lavender-lollipops-purple.jpg"
  }
]
```

#### 3.4 Sales by Category

**Endpoint:** `GET /api/admin/analytics/sales-by-category?range=30d`

**Response (Success - 200):**

```json
[
  {
    "category": "Chocolate",
    "revenue": 15680.5,
    "percentage": 34.3,
    "unitsSold": 456,
    "profit": 6272.2
  },
  {
    "category": "Gummies",
    "revenue": 12340.25,
    "percentage": 27.0,
    "unitsSold": 789,
    "profit": 4936.1
  },
  {
    "category": "Macarons",
    "revenue": 8920.75,
    "percentage": 19.5,
    "unitsSold": 234,
    "profit": 3568.3
  },
  {
    "category": "Caramel",
    "revenue": 4560.0,
    "percentage": 10.0,
    "unitsSold": 156,
    "profit": 1824.0
  },
  {
    "category": "Lollipops",
    "revenue": 2340.0,
    "percentage": 5.1,
    "unitsSold": 98,
    "profit": 936.0
  },
  {
    "category": "Fudge",
    "revenue": 1859.0,
    "percentage": 4.1,
    "unitsSold": 67,
    "profit": 743.6
  }
]
```

#### 3.5 Customer Summary

**Endpoint:** `GET /api/admin/analytics/customers/summary?range=30d`

**Response (Success - 200):**

```json
{
  "totalCustomers": 2847,
  "newCustomers": 156,
  "returningCustomers": 89,
  "averageCustomerValue": 65.4,
  "customerRetentionRate": 78.5
}
```

#### 3.6 Customer Segments

**Endpoint:** `GET /api/admin/analytics/customers/segments?range=30d`

**Response (Success - 200):**

```json
[
  { "segment": "Premium Buyers", "count": 234, "avgOrderValue": 125.5 },
  { "segment": "Regular Customers", "count": 1456, "avgOrderValue": 45.2 },
  { "segment": "Occasional Buyers", "count": 1157, "avgOrderValue": 28.9 }
]
```

#### 3.7 Website Traffic

**Endpoint:** `GET /api/admin/analytics/performance/traffic?range=30d`

**Response (Success - 200):**

```json
{
  "websiteTraffic": {
    "totalVisits": 45680,
    "uniqueVisitors": 23450,
    "bounceRate": 32.5,
    "avgSessionDuration": "3:45",
    "pageViews": 123450
  }
}
```

#### 3.8 Conversion Funnel

**Endpoint:** `GET /api/admin/analytics/performance/conversion-funnel?range=30d`

**Response (Success - 200):**

```json
{
  "visitors": 45680,
  "productViews": 23450,
  "addToCart": 3456,
  "checkout": 1247,
  "completed": 1156
}
```

#### 3.9 Shipping Metrics

**Endpoint:** `GET /api/admin/analytics/shipping-metrics?range=30d`

**Response (Success - 200):**

```json
{
  "avgShippingTime": "2.3 days",
  "onTimeDelivery": 94.2,
  "shippingCost": 8.5,
  "freeShippingThreshold": 75.0
}
```

#### 3.10 Alerts

**Endpoint:** `GET /api/admin/alerts?limit=20`

**Response (Success - 200):**

```json
[
  {
    "id": 1,
    "type": "warning",
    "title": "Low Stock Alert",
    "message": "5 products are running low on stock",
    "timestamp": "2024-01-28T10:30:00Z",
    "priority": "high"
  },
  {
    "id": 2,
    "type": "info",
    "title": "New Order Received",
    "message": "Order #ORD-2024-002 worth $156.75 received",
    "timestamp": "2024-01-28T09:15:00Z",
    "priority": "medium"
  },
  {
    "id": 3,
    "type": "success",
    "title": "Monthly Target Achieved",
    "message": "January revenue target exceeded by 12%",
    "timestamp": "2024-01-28T08:00:00Z",
    "priority": "low"
  },
  {
    "id": 4,
    "type": "warning",
    "title": "Payment Issue",
    "message": "Payment failed for Order #ORD-2024-005",
    "timestamp": "2024-01-27T16:45:00Z",
    "priority": "high"
  }
]
```

#### 3.11 Inventory Status

**Endpoint:** `GET /api/admin/inventory/status`

**Response (Success - 200):**

```json
{
  "totalProducts": 45,
  "inStock": 38,
  "lowStock": 5,
  "outOfStock": 2,
  "totalValue": 125430.75,
  "lowStockThreshold": 10
}
```

#### 3.12 Low Stock Items

**Endpoint:** `GET /api/admin/inventory/low-stock?threshold=10`

**Response (Success - 200):**

```json
[
  {
    "id": "ls-1",
    "name": "Pistachio Rose Turkish Delight",
    "category": "Turkish Delight",
    "currentStock": 3,
    "minThreshold": 10,
    "lastRestocked": "2024-01-15",
    "supplier": "Premium Sweets Co.",
    "image": "https://i.postimg.cc/rKJ8YpBN/pistachio-rose-turkish-delight-pink.jpg"
  },
  {
    "id": "ls-2",
    "name": "Mint Chocolate Chip Brittle",
    "category": "Brittle",
    "currentStock": 7,
    "minThreshold": 10,
    "lastRestocked": "2024-01-20",
    "supplier": "Artisan Confections",
    "image": "https://i.postimg.cc/Yhkr3FsS/mint-chocolate-chip-brittle-green.jpg"
  },
  {
    "id": "ls-3",
    "name": "Strawberry Cream Fudge",
    "category": "Fudge",
    "currentStock": 5,
    "minThreshold": 10,
    "lastRestocked": "2024-01-18",
    "supplier": "Sweet Dreams Ltd",
    "image": "https://i.postimg.cc/7CpDTJzw/strawberry-cream-fudge-pink-white.jpg"
  },
  {
    "id": "ls-4",
    "name": "Coconut Lime Macaroons",
    "category": "Macaroons",
    "currentStock": 4,
    "minThreshold": 10,
    "lastRestocked": "2024-01-22",
    "supplier": "Tropical Treats",
    "image": "https://i.postimg.cc/TyhYbXHq/coconut-lime-macaroons-white-green.jpg"
  }
]
```

---

### 4. Admin Orders

#### 4.1 List Orders

**Endpoint:** `GET /api/admin/orders?status=&page=0&size=20&sort=createdAt,desc`

**Response (Success - 200):**

```json
[
  {
    "id": "ORD-2024-001",
    "username": "sarah.j@email.com",
    "totalAmount": 89.5,
    "status": "DELIVERED",
    "orderDate": "2024-01-28T12:30:00Z"
  },
  {
    "id": "ORD-2024-002",
    "username": "m.chen@email.com",
    "totalAmount": 156.75,
    "status": "PROCESSING",
    "orderDate": "2024-01-28T09:15:00Z"
  },
  {
    "id": "ORD-2024-003",
    "username": "emily.r@email.com",
    "totalAmount": 67.25,
    "status": "SHIPPED",
    "orderDate": "2024-01-27T16:45:00Z"
  }
]
```

#### 4.2 Recent Orders

**Endpoint:** `GET /api/admin/orders/recent?limit=20`

**Response (Success - 200):**

```json
[
  {
    "id": "ORD-2024-001",
    "customer": "Sarah Johnson",
    "email": "sarah.j@email.com",
    "total": 89.5,
    "status": "Delivered",
    "date": "2024-01-28",
    "items": 3,
    "paymentMethod": "Credit Card"
  },
  {
    "id": "ORD-2024-002",
    "customer": "Michael Chen",
    "email": "m.chen@email.com",
    "total": 156.75,
    "status": "Processing",
    "date": "2024-01-28",
    "items": 5,
    "paymentMethod": "PayPal"
  }
]
```

## Data Models

### User

```json
{
  "id": 1,
  "email": "user@example.com",
  "password": "encrypted_password",
  "role": "ROLE_USER"
}
```

### Sweet

```json
{
  "id": "94e31aff-b1ad-4f48-bc91-46ef369ec05e",
  "name": "Belgian Dark Chocolate Truffles",
  "category": "chocolate",
  "price": 24.99,
  "quantity": 15,
  "description": "Luxurious handcrafted truffles made with 70% Belgian dark chocolate and premium cocoa powder.",
  "image": "elegant-dark-chocolate-truffles.jpg",
  "cost": 12.5,
  "profitMargin": 40.0,
  "minThreshold": 10,
  "lastRestocked": "2025-09-10T12:00:00",
  "supplier": {
    "id": "b1f0e0b3-aaaa-bbbb-cccc-1234567890ab",
    "name": "Premium Sweets Co."
  }
}
```

### CartItem

```json
{
  "id": 1,
  "sweetId": "94e31aff-b1ad-4f48-bc91-46ef369ec05e",
  "sweetName": "Belgian Dark Chocolate Truffles",
  "category": "chocolate",
  "price": 24.99,
  "quantity": 2,
  "totalPrice": 49.98,
  "description": "Luxurious handcrafted truffles made with 70% Belgian dark chocolate and premium cocoa powder.",
  "image": "elegant-dark-chocolate-truffles.jpg"
}
```

### Order

```json
{
  "id": 1,
  "username": "user@example.com",
  "orderItems": [...],
  "totalAmount": 80.0,
  "status": "PENDING",
  "orderDate": "2025-09-19T04:30:00",
  "paymentMode": "CREDIT_CARD",
  "paymentTransactionId": "CC_1695111111111_AB12CD34",
  "shippingAddress": "221B Baker Street, London",
  "customerNotes": "Ring the bell",
  "trackingNumber": null,
  "estimatedDeliveryDate": "2025-09-26T12:00:00",
  "actualDeliveryDate": null
}
```

### OrderItem

```json
{
  "id": 1,
  "sweetId": "94e31aff-b1ad-4f48-bc91-46ef369ec05e",
  "sweetName": "Belgian Dark Chocolate Truffles",
  "category": "chocolate",
  "price": 24.99,
  "quantity": 2,
  "totalPrice": 49.98,
  "description": "Luxurious handcrafted truffles made with 70% Belgian dark chocolate and premium cocoa powder.",
  "image": "elegant-dark-chocolate-truffles.jpg"
}
```

---

## Security Notes

### Role-Based Access Control

- **ROLE_USER**: Can access sweets, cart, and orders
- **ROLE_ADMIN**: Can access all endpoints including admin functions

### Authentication

- JWT tokens expire after 1 minute (short-lived tokens)
- Include token in Authorization header for protected endpoints
- Tokens are validated on every request to protected endpoints
- Tokens are validated against the specific user they were issued to
- Invalid or expired tokens result in 401 Unauthorized responses
- Tokens are blacklisted upon logout or refresh
- Blacklisted tokens cannot be used for authentication or refresh
- Token blacklist is cleaned up periodically to prevent memory issues
- The frontend should use the refresh token endpoint to maintain sessions

### Error Handling

- All errors return appropriate HTTP status codes
- Error responses include descriptive messages
- Security errors don't expose sensitive information

### Default Users

- **Admin**: `admin@sweetshop.com` / `admin123`
- **User**: `user@sweetshop.com` / `user123`

### Rate Limiting

- No built-in rate limiting (can be added if needed)
- Consider implementing for production use

### CORS

- Configured for `http://localhost:3000` and `http://localhost:5173`
- Update for production domains

---

## Testing

Use the provided test credentials to test the API:

1. **Register a new user**
2. **Login to get JWT token**
3. **Use token for protected endpoints**
4. **Test admin endpoints with admin credentials**

For comprehensive testing, use tools like Postman, curl, or any HTTP client that supports JWT authentication.
