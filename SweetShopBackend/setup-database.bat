@echo off
echo Setting up SweetShop Database...
echo.

REM Check if PostgreSQL is running
echo Checking PostgreSQL connection...
psql -U postgres -c "SELECT version();" >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Cannot connect to PostgreSQL. Please make sure PostgreSQL is running.
    echo Make sure PostgreSQL is installed and the service is started.
    pause
    exit /b 1
)

echo PostgreSQL is running. Creating database...

REM Create database using the init script
psql -U postgres -f src\main\resources\sql\init-database.sql

if %errorlevel% equ 0 (
    echo.
    echo ✅ Database setup completed successfully!
    echo Database: sweetshop
    echo User: sweetshop_user (optional)
    echo.
    echo You can now run your Spring Boot application.
) else (
    echo.
    echo ❌ Database setup failed. Please check the error messages above.
)

echo.
pause
