# PowerShell script to create distcomp database and verify connection
# Run this script: .\setup-database.ps1

$env:PGPASSWORD = "1902"

Write-Host "Creating distcomp database..." -ForegroundColor Yellow

# Create database
psql -U postgres -c "CREATE DATABASE distcomp;" 2>&1 | Out-Null

if ($LASTEXITCODE -eq 0) {
    Write-Host "Database 'distcomp' created successfully!" -ForegroundColor Green
} else {
    Write-Host "Database might already exist or error occurred. Continuing..." -ForegroundColor Yellow
}

Write-Host "`nVerifying connection to distcomp database..." -ForegroundColor Yellow

# Test connection
$result = psql -U postgres -d distcomp -c "SELECT version();" 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "Connection successful!" -ForegroundColor Green
    Write-Host "`nDatabase setup complete. You can now start the application." -ForegroundColor Green
} else {
    Write-Host "Connection failed. Please check:" -ForegroundColor Red
    Write-Host "1. PostgreSQL is running" -ForegroundColor Red
    Write-Host "2. Password for user 'postgres' is '1902'" -ForegroundColor Red
    Write-Host "3. User 'postgres' has CREATE DATABASE privilege" -ForegroundColor Red
}

$env:PGPASSWORD = ""

