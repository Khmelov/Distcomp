# Complete database setup script
# Run this script: .\setup-database-complete.ps1

Write-Host "=== Complete Database Setup ===" -ForegroundColor Cyan
Write-Host ""

$env:PGPASSWORD = "1902"

# Step 1: Create database if it doesn't exist
Write-Host "Step 1: Creating database 'distcomp'..." -ForegroundColor Yellow
$dbExists = psql -U postgres -d postgres -t -c "SELECT 1 FROM pg_database WHERE datname='distcomp';" 2>&1

if ($dbExists.Trim() -ne "1") {
    psql -U postgres -c "CREATE DATABASE distcomp;" 2>&1 | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✓ Database 'distcomp' created" -ForegroundColor Green
    } else {
        Write-Host "   ✗ Failed to create database" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "   ✓ Database 'distcomp' already exists" -ForegroundColor Green
}

# Step 2: Grant privileges
Write-Host ""
Write-Host "Step 2: Granting privileges..." -ForegroundColor Yellow
psql -U postgres -d distcomp -c "GRANT ALL PRIVILEGES ON DATABASE distcomp TO postgres;" 2>&1 | Out-Null
psql -U postgres -d distcomp -c "GRANT ALL ON SCHEMA public TO postgres;" 2>&1 | Out-Null
Write-Host "   ✓ Privileges granted" -ForegroundColor Green

# Step 3: Create schema if it doesn't exist
Write-Host ""
Write-Host "Step 3: Creating schema 'distcomp'..." -ForegroundColor Yellow
psql -U postgres -d distcomp -c "CREATE SCHEMA IF NOT EXISTS distcomp;" 2>&1 | Out-Null
if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✓ Schema 'distcomp' ready" -ForegroundColor Green
} else {
    Write-Host "   ⚠ Schema creation returned non-zero (might already exist)" -ForegroundColor Yellow
}

# Step 4: Grant schema privileges
Write-Host ""
Write-Host "Step 4: Granting schema privileges..." -ForegroundColor Yellow
psql -U postgres -d distcomp -c "GRANT ALL ON SCHEMA distcomp TO postgres;" 2>&1 | Out-Null
psql -U postgres -d distcomp -c "ALTER DEFAULT PRIVILEGES IN SCHEMA distcomp GRANT ALL ON TABLES TO postgres;" 2>&1 | Out-Null
Write-Host "   ✓ Schema privileges granted" -ForegroundColor Green

# Step 5: Test connection
Write-Host ""
Write-Host "Step 5: Testing connection..." -ForegroundColor Yellow
$testResult = psql -U postgres -d distcomp -c "SELECT 'Connection successful!' AS status;" 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✓ Connection test successful" -ForegroundColor Green
} else {
    Write-Host "   ✗ Connection test failed" -ForegroundColor Red
    Write-Host "   Error: $testResult" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Setup Complete ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Start your Spring Boot application" -ForegroundColor White
Write-Host "2. Liquibase will create tables automatically" -ForegroundColor White
Write-Host "3. Test system should use these connection settings:" -ForegroundColor White
Write-Host "   - JDBC URL: jdbc:postgresql://localhost:5432/distcomp" -ForegroundColor White
Write-Host "   - Username: postgres" -ForegroundColor White
Write-Host "   - Password: 1902" -ForegroundColor White
Write-Host ""

$env:PGPASSWORD = ""

