# SweetShop Database Setup Script
Write-Host "Setting up SweetShop Database..." -ForegroundColor Green
Write-Host ""

# Check if PostgreSQL is running
Write-Host "Checking PostgreSQL connection..." -ForegroundColor Yellow
try {
    $result = psql -U postgres -c "SELECT version();" 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ PostgreSQL is running" -ForegroundColor Green
    } else {
        throw "PostgreSQL connection failed"
    }
} catch {
    Write-Host "❌ ERROR: Cannot connect to PostgreSQL" -ForegroundColor Red
    Write-Host "Please make sure PostgreSQL is installed and running" -ForegroundColor Red
    Write-Host "You can start PostgreSQL service with: net start postgresql-x64-XX" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Creating database..." -ForegroundColor Yellow

# Create database using the init script
try {
    psql -U postgres -f "src\main\resources\sql\init-database.sql"
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "✅ Database setup completed successfully!" -ForegroundColor Green
        Write-Host "Database: sweetshop" -ForegroundColor Cyan
        Write-Host "User: sweetshop_user (optional)" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "You can now run your Spring Boot application with:" -ForegroundColor Yellow
        Write-Host ".\mvnw.cmd spring-boot:run" -ForegroundColor White
    } else {
        throw "Database creation failed"
    }
} catch {
    Write-Host ""
    Write-Host "❌ Database setup failed. Please check the error messages above." -ForegroundColor Red
}

Write-Host ""
Read-Host "Press Enter to exit"
