# 🗄️ NeonDB Database Setup Guide

## 📋 **NeonDB Connection Details**

From your NeonDB dashboard, here are the connection details:

- **Host**: `ep-blue-frost-a1japvkr-pooler.ap-southeast-1.aws.neon.tech`
- **Port**: `5432`
- **Database**: `neondb`
- **Username**: `neondb_owner`
- **Password**: `[Your NeonDB Password]` (click "Show password" in NeonDB dashboard)
- **Connection String**: `postgresql://neondb_owner:[PASSWORD]@ep-blue-frost-a1japvkr-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require`

## 🚀 **Step 1: Get Your NeonDB Password**

1. **Go to your NeonDB dashboard**
2. **Click "Show password"** in the connection dialog
3. **Copy the password** (it will be revealed)

## 🔧 **Step 2: Update Railway Environment Variables**

Go to your Railway backend project and update these variables:

```bash
# Database Configuration (NeonDB)
NEON_DB_PASSWORD=your_actual_neon_password_here

# Application Configuration
SPRING_PROFILES_ACTIVE=railway
JWT_SECRET=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
JWT_EXPIRATION=7200000
CORS_ORIGINS=https://sweetshop-frontend.vercel.app
PORT=8080
```

## 🗃️ **Step 3: Set Up Database Tables**

1. **Connect to your NeonDB database** using any PostgreSQL client:
   - **pgAdmin** (GUI)
   - **DBeaver** (GUI)
   - **TablePlus** (GUI)
   - **psql** (Command line)

2. **Use this connection string**:
   ```
   postgresql://neondb_owner:YOUR_PASSWORD@ep-blue-frost-a1japvkr-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require
   ```

3. **Run the database setup script**:
   - Copy and paste the contents of `setup-database.sql`
   - Execute the script to create all tables and sample data

## 🧪 **Step 4: Test Database Connection**

After setting up the environment variables and tables:

1. **Redeploy your backend** (Railway will automatically redeploy)
2. **Test the connection** by visiting: `https://your-backend-url.vercel.app/api/health/database`
3. **Check Railway logs** for any connection errors

## ✅ **Expected Results**

- **Database connection**: Should work without errors
- **Tables created**: All required tables should exist
- **Sample data**: Default users and products should be available
- **API endpoints**: Should respond correctly

## 🔍 **Troubleshooting**

### **Connection Issues**
- Verify the password is correct (no extra spaces)
- Check if the connection string format is correct
- Ensure SSL is enabled (`sslmode=require`)

### **Table Creation Issues**
- Make sure you're connected to the `neondb` database
- Check if the user has proper permissions
- Verify the SQL script runs without errors

### **API Issues**
- Check Railway logs for database connection errors
- Verify environment variables are set correctly
- Test the health check endpoints

## 🎯 **Key Benefits of NeonDB**

- **Serverless PostgreSQL** - No server management
- **Auto-scaling** - Handles traffic spikes automatically
- **Global availability** - Fast worldwide access
- **Free tier** - Generous limits for development
- **Easy setup** - Simple connection process

## 📊 **Database Schema**

The `setup-database.sql` script will create:
- **Users table** (authentication)
- **Sweets table** (products)
- **Cart items table** (shopping cart)
- **Orders table** (order management)
- **Analytics tables** (reporting)
- **Sample data** for testing

## 🔄 **Next Steps**

After database setup:
1. **Deploy your frontend to Vercel**
2. **Update CORS_ORIGINS with your frontend URL**
3. **Test the full application**
4. **Monitor logs for any issues**
