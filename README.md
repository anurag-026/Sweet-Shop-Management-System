# 🍭 Sweet Shop Management System

A comprehensive full-stack e-commerce application for managing a sweet shop with advanced analytics, inventory management, and order processing capabilities.

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [Architecture](#-architecture)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Setup Instructions](#-setup-instructions)
  - [Database Setup](#database-setup)
  - [Backend Setup](#backend-setup)
  - [Frontend Setup](#frontend-setup)
- [Screenshots](#-screenshots)
- [API Documentation](#-api-documentation)
- [Deployment](#-deployment)
- [My AI Usage](#-my-ai-usage)
- [Contributing](#-contributing)
- [License](#-license)

## 🎯 Project Overview

The Sweet Shop Management System is a modern, full-stack web application designed to streamline the operations of a sweet shop business. It provides a complete solution for inventory management, customer orders, analytics, and administrative tasks.

### Key Capabilities

- **User Management**: Secure authentication with JWT tokens and role-based access control
- **Product Catalog**: Comprehensive sweet inventory with categories, pricing, and stock management
- **Shopping Cart**: Full-featured cart system with real-time updates
- **Order Processing**: Complete order lifecycle from creation to delivery tracking
- **Admin Dashboard**: Advanced analytics and business intelligence
- **Inventory Management**: Real-time stock tracking with low-stock alerts
- **Customer Analytics**: Customer segmentation and behavior analysis

## 🏗️ Architecture

The application follows a modern three-tier architecture:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│   (React)       │◄──►│   (Spring Boot) │◄──►│   (PostgreSQL)  │
│   Port: 3000    │    │   Port: 8081    │    │   (NeonDB)      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Frontend (React)

- **Framework**: React 19.1.1 with React Router
- **Styling**: Tailwind CSS with custom components
- **State Management**: Context API for authentication and cart
- **HTTP Client**: Axios for API communication
- **Animations**: Framer Motion for smooth transitions

### Backend (Spring Boot)

- **Framework**: Spring Boot 3.5.6
- **Security**: Spring Security with JWT authentication
- **Database**: Spring Data JPA with PostgreSQL
- **API**: RESTful APIs with comprehensive documentation
- **Analytics**: Real-time business intelligence and reporting

### Database (PostgreSQL)

- **Provider**: NeonDB (Serverless PostgreSQL)
- **Features**: ACID compliance, real-time analytics, auto-scaling
- **Schema**: 15+ tables with relationships and indexes
- **Analytics**: Dedicated tables for business intelligence

## ✨ Features

### 🔐 Authentication & Authorization

- JWT-based authentication with refresh tokens
- Role-based access control (USER/ADMIN)
- Secure password hashing with BCrypt
- Session management with token blacklisting

### 🛍️ E-commerce Features

- Product catalog with search and filtering
- Shopping cart with real-time updates
- Order processing with multiple payment methods
- Order tracking and status updates
- Customer profile management

### 📊 Admin Dashboard

- Real-time sales analytics and reporting
- Customer segmentation and behavior analysis
- Inventory management with low-stock alerts
- Order management and status tracking
- Website traffic and conversion analytics
- System alerts and notifications

### 📈 Analytics & Reporting

- Sales overview with growth metrics
- Monthly sales trends and forecasting
- Top products and category analysis
- Customer lifetime value analysis
- Conversion funnel tracking
- Shipping performance metrics

## 🛠️ Tech Stack

### Frontend

- **React** 19.1.1 - UI framework
- **React Router** 7.9.1 - Client-side routing
- **Tailwind CSS** - Utility-first CSS framework
- **Framer Motion** 12.23.15 - Animation library
- **Axios** 1.12.2 - HTTP client
- **Lucide React** 0.544.0 - Icon library

### Backend

- **Spring Boot** 3.5.6 - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence
- **PostgreSQL** - Primary database
- **JWT** - Token-based authentication
- **Maven** - Dependency management

### Database

- **PostgreSQL** - Primary database
- **NeonDB** - Serverless PostgreSQL hosting
- **UUID** - Primary key generation
- **JSONB** - Flexible data storage

### Development Tools

- **Node.js** - Frontend runtime
- **Java 17** - Backend runtime
- **Maven** - Backend build tool
- **Git** - Version control

## 📋 Prerequisites

Before setting up the project, ensure you have the following installed:

- **Node.js** (v16 or higher)
- **Java** (v17 or higher)
- **Maven** (v3.6 or higher)
- **Git**
- **PostgreSQL client** (for database setup)
- **IDE** (VS Code, IntelliJ IDEA, or similar)

## 🚀 Setup Instructions

### Database Setup

1. **Create NeonDB Account**

   - Visit [NeonDB](https://neon.tech)
   - Create a new project
   - Note down your connection details

2. **Set Up Database Schema**

   ```bash
   # Connect to your NeonDB database
   psql "postgresql://neondb_owner:[PASSWORD]@ep-blue-frost-a1japvkr-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require"

   # Run the setup script
   \i setup-database.sql
   ```

3. **Verify Database Setup**
   - Check that all tables are created
   - Verify sample data is inserted
   - Test database connection

### Backend Setup

1. **Clone the Repository**

   ```bash
   git clone <repository-url>
   cd "Incubyte Assignment/SweetShopBackend"
   ```

2. **Configure Database Connection**

   ```bash
   # Update application.properties or application-railway.properties
   spring.datasource.url=jdbc:postgresql://ep-blue-frost-a1japvkr-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require
   spring.datasource.username=neondb_owner
   spring.datasource.password=YOUR_PASSWORD
   ```

3. **Build and Run**

   ```bash
   # Build the project
   mvn clean install

   # Run the application
   mvn spring-boot:run
   ```

4. **Verify Backend**
   - Backend runs on `http://localhost:8081`
   - Test health endpoint: `http://localhost:8081/api/health`
   - Check API documentation: `http://localhost:8081/api/docs`

### Frontend Setup

1. **Navigate to Frontend Directory**

   ```bash
   cd frontend
   ```

2. **Install Dependencies**

   ```bash
   npm install
   ```

3. **Configure API Endpoint**

   ```bash
   # Update src/services/axiosInstance.js
   const API_BASE_URL = 'http://localhost:8081';
   ```

4. **Start Development Server**

   ```bash
   npm start
   ```

5. **Verify Frontend**
   - Frontend runs on `http://localhost:3000`
   - Test login with default credentials
   - Verify all features are working

### Default Credentials

- **Admin**: `admin@sweetshop.com` / `admin123`
- **User**: `user@sweetshop.com` / `user123`

## 📸 Screenshots

### 🏠 Homepage & Authentication

#### Login Page

![Login Page](screenshot/Screenshot%202025-09-19%20171050.png)
_Clean and modern login interface with form validation_

#### Registration Page

![Registration Page](screenshot/Screenshot%202025-09-19%20171056.png)
_User registration form with comprehensive validation_

### 🛍️ Product Catalog & Shopping

#### Product Catalog

![Product Catalog](screenshot/Screenshot%202025-09-19%20171108.png)
_Comprehensive product catalog with search and filtering capabilities_

#### Product Details

![Product Details](screenshot/Screenshot%202025-09-19%20171115.png)
_Detailed product view with add to cart functionality_

#### Shopping Cart

![Shopping Cart](screenshot/Screenshot%202025-09-19%20171119.png)
_Shopping cart with quantity management and checkout options_

### 📊 Admin Dashboard & Analytics

#### Admin Dashboard Overview

![Admin Dashboard](screenshot/Screenshot%202025-09-19%20171138.png)
_Comprehensive admin dashboard with key metrics and analytics_

#### Sales Analytics

![Sales Analytics](screenshot/Screenshot%202025-09-19%20171151.png)
_Detailed sales analytics with charts and trends_

#### Order Management

![Order Management](screenshot/Screenshot%202025-09-19%20171208.png)
_Order management interface with status updates and tracking_

#### Customer Analytics

![Customer Analytics](screenshot/Screenshot%202025-09-19%20171214.png)
_Customer segmentation and behavior analysis_

#### Inventory Management

![Inventory Management](screenshot/Screenshot%202025-09-19%20171224.png)
_Inventory status with low-stock alerts and management tools_

### 🔧 System Management

#### Admin Panel

![Admin Panel](screenshot/Screenshot%202025-09-19%20171240.png)
_Administrative panel for system configuration_

#### User Profile

![User Profile](screenshot/Screenshot%202025-09-19%20171246.png)
_User profile management with personal information_

#### System Alerts

![System Alerts](screenshot/Screenshot%202025-09-19%20171255.png)
_Real-time system alerts and notifications_

### 📈 Advanced Analytics

#### Monthly Sales Trends

![Monthly Sales](screenshot/Screenshot%202025-09-19%20171304.png)
_Monthly sales trends with growth indicators_

#### Category Performance

![Category Performance](screenshot/Screenshot%202025-09-19%20171324.png)
_Sales performance by product category_

#### Top Products

![Top Products](screenshot/Screenshot%202025-09-19%20171332.png)
_Top-selling products with revenue metrics_

#### Conversion Funnel

![Conversion Funnel](screenshot/Screenshot%202025-09-19%20171343.png)
_Website conversion funnel analysis_

#### Shipping Metrics

![Shipping Metrics](screenshot/Screenshot%202025-09-19%20171349.png)
_Shipping performance and delivery metrics_

#### Low Stock Alerts

![Low Stock Alerts](screenshot/Screenshot%202025-09-19%20171405.png)
_Inventory alerts for low-stock items_

#### Recent Orders

![Recent Orders](screenshot/Screenshot%202025-09-19%20171411.png)
_Recent orders with status and customer information_

## 📚 API Documentation

The backend provides a comprehensive REST API with detailed documentation:

- **API Base URL**: `http://localhost:8081`
- **Documentation**: Available at `/api/docs` (when running)
- **Health Check**: `/api/health`

### Key API Endpoints

#### Authentication

- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `POST /api/auth/refresh` - Token refresh
- `GET /api/auth/profile` - Get user profile

#### Products

- `GET /api/sweets` - Get all products
- `GET /api/sweets/{id}` - Get product by ID
- `POST /api/sweets` - Create product (Admin)
- `PUT /api/sweets/{id}` - Update product (Admin)
- `DELETE /api/sweets/{id}` - Delete product (Admin)

#### Cart Management

- `GET /api/cart` - Get cart items
- `POST /api/cart/add` - Add item to cart
- `PUT /api/cart/{id}` - Update cart item
- `DELETE /api/cart/{id}` - Remove cart item
- `DELETE /api/cart/clear` - Clear cart

#### Order Management

- `POST /api/orders/checkout` - Create order
- `GET /api/orders` - Get user orders
- `GET /api/orders/{id}` - Get order by ID
- `PUT /api/orders/{id}/status` - Update order status (Admin)

#### Admin Analytics

- `GET /api/admin/analytics/sales-overview` - Sales overview
- `GET /api/admin/analytics/monthly-sales` - Monthly sales
- `GET /api/admin/analytics/top-products` - Top products
- `GET /api/admin/analytics/customers/summary` - Customer summary
- `GET /api/admin/alerts` - System alerts

For complete API documentation, see [API_DOCUMENTATION.md](API_DOCUMENTATION.md).

## 🚀 Deployment

### Backend Deployment (Railway)

1. **Connect Repository**

   - Link your GitHub repository to Railway
   - Select the `SweetShopBackend` directory

2. **Configure Environment Variables**

   ```bash
   NEON_DB_PASSWORD=your_neon_password
   SPRING_PROFILES_ACTIVE=railway
   JWT_SECRET=your_jwt_secret
   CORS_ORIGINS=https://your-frontend-url.vercel.app
   ```

3. **Deploy**
   - Railway will automatically build and deploy
   - Monitor logs for any issues

### Frontend Deployment (Vercel)

1. **Connect Repository**

   - Link your GitHub repository to Vercel
   - Select the `frontend` directory

2. **Configure Build Settings**

   ```bash
   Build Command: npm run build
   Output Directory: build
   ```

3. **Set Environment Variables**

   ```bash
   REACT_APP_API_URL=https://your-backend-url.railway.app
   ```

4. **Deploy**
   - Vercel will automatically build and deploy
   - Update CORS settings in backend

### Database Deployment (NeonDB)

1. **Create NeonDB Project**

   - Sign up at [NeonDB](https://neon.tech)
   - Create a new project

2. **Run Setup Script**

   - Connect to your database
   - Execute `setup-database.sql`

3. **Update Connection Strings**
   - Update backend environment variables
   - Test connection

## 🤖 My AI Usage

### AI Tools Used

#### 1. **Cursor AI (Primary Development Assistant)**

- **Code Generation**: Used extensively for generating React components, Spring Boot controllers, and service classes
- **Code Refactoring**: Helped optimize database queries, improve component structure, and enhance error handling
- **Debugging**: Assisted in identifying and fixing authentication issues, API endpoint problems, and frontend state management bugs
- **Documentation**: Generated comprehensive API documentation and code comments

#### 2. **ChatGPT (Secondary Support)**

- **Architecture Planning**: Used for high-level system design decisions and database schema planning
- **Problem Solving**: Helped troubleshoot complex authentication flows and JWT token management
- **Code Review**: Provided feedback on code quality and suggested improvements

### How AI Impacted My Workflow

#### **Development Speed**

- **10x Faster Development**: AI tools dramatically accelerated the development process, allowing me to build complex features in hours instead of days
- **Rapid Prototyping**: Quickly generated boilerplate code and basic functionality, then refined with AI assistance
- **Instant Code Generation**: Created entire React components and Spring Boot services in minutes

#### **Code Quality**

- **Best Practices**: AI consistently suggested modern coding patterns and best practices
- **Error Prevention**: Helped identify potential issues before they became problems
- **Consistent Style**: Maintained consistent code formatting and structure across the entire codebase

#### **Learning and Growth**

- **Technology Exploration**: AI helped me quickly understand and implement new technologies like Framer Motion and advanced Spring Security features
- **Problem-Solving Skills**: Enhanced my debugging abilities by providing multiple solution approaches
- **Architecture Understanding**: Gained deeper insights into full-stack application architecture

#### **Specific Use Cases**

1. **Authentication System**

   - AI helped design the JWT-based authentication flow
   - Generated secure password hashing and token validation logic
   - Created role-based access control implementation

2. **Database Design**

   - Assisted in designing the comprehensive database schema
   - Generated complex SQL queries for analytics
   - Created proper relationships and indexes

3. **Frontend Components**

   - Generated responsive React components with Tailwind CSS
   - Created smooth animations using Framer Motion
   - Implemented complex state management with Context API

4. **API Development**

   - Generated RESTful API endpoints with proper error handling
   - Created comprehensive API documentation
   - Implemented analytics and reporting endpoints

5. **Analytics System**
   - Designed the analytics database schema
   - Generated complex aggregation queries
   - Created real-time reporting dashboards

### Reflection on AI Impact

#### **Positive Impacts**

- **Efficiency**: Reduced development time by approximately 70%
- **Quality**: Improved code quality and consistency
- **Learning**: Accelerated learning of new technologies
- **Innovation**: Enabled focus on business logic rather than boilerplate code

#### **Challenges Overcome**

- **Complex Integrations**: AI helped navigate complex integrations between frontend, backend, and database
- **Authentication Issues**: Resolved JWT token management and refresh token implementation
- **Performance Optimization**: Identified and fixed performance bottlenecks in database queries

#### **Future Considerations**

- **AI as a Partner**: View AI as a collaborative partner rather than a replacement
- **Continuous Learning**: Continue learning new AI tools and techniques
- **Quality Control**: Always review and test AI-generated code thoroughly
- **Human Creativity**: Use AI to handle routine tasks while focusing on creative problem-solving

### Conclusion

AI tools have fundamentally transformed my development workflow, enabling me to build a comprehensive, production-ready application in a fraction of the traditional time. The combination of Cursor AI and ChatGPT provided both technical assistance and strategic guidance, resulting in a robust, scalable, and maintainable codebase. This experience has reinforced my belief that AI is a powerful tool that, when used thoughtfully, can significantly enhance developer productivity and code quality.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
