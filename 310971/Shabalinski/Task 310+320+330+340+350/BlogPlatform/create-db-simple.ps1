# Simple script to create distcomp database
# Run: .\create-db-simple.ps1

$ErrorActionPreference = "Stop"

Write-Host "Creating distcomp database..." -ForegroundColor Yellow

# Try to find psql
$psqlPath = $null
$possiblePaths = @(
    "C:\Program Files\PostgreSQL\*\bin\psql.exe",
    "C:\Program Files (x86)\PostgreSQL\*\bin\psql.exe",
    "$env:ProgramFiles\PostgreSQL\*\bin\psql.exe",
    "$env:ProgramFiles(x86)\PostgreSQL\*\bin\psql.exe"
)

foreach ($path in $possiblePaths) {
    $found = Get-ChildItem -Path $path -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($found) {
        $psqlPath = $found.FullName
        break
    }
}

if (-not $psqlPath) {
    Write-Host "ERROR: psql not found. Please:" -ForegroundColor Red
    Write-Host "1. Install PostgreSQL" -ForegroundColor Yellow
    Write-Host "2. Add PostgreSQL bin directory to PATH" -ForegroundColor Yellow
    Write-Host "3. Or use pgAdmin to create database manually" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Manual steps:" -ForegroundColor Cyan
    Write-Host "1. Open pgAdmin" -ForegroundColor White
    Write-Host "2. Connect to PostgreSQL server" -ForegroundColor White
    Write-Host "3. Right-click 'Databases' -> Create -> Database" -ForegroundColor White
    Write-Host "4. Name: distcomp" -ForegroundColor White
    Write-Host "5. Click Save" -ForegroundColor White
    exit 1
}

Write-Host "Found psql at: $psqlPath" -ForegroundColor Green

# Set password
$env:PGPASSWORD = "1902"

# Check if database exists
Write-Host "Checking if database exists..." -ForegroundColor Yellow
$checkCmd = "& `"$psqlPath`" -U postgres -d postgres -t -c `"SELECT 1 FROM pg_database WHERE datname='distcomp';`""
$exists = Invoke-Expression $checkCmd 2>&1

if ($exists -match "1") {
    Write-Host "Database 'distcomp' already exists" -ForegroundColor Green
} else {
    Write-Host "Creating database 'distcomp'..." -ForegroundColor Yellow
    $createCmd = "& `"$psqlPath`" -U postgres -c `"CREATE DATABASE distcomp;`""
    Invoke-Expression $createCmd 2>&1 | Out-Null
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Database 'distcomp' created successfully!" -ForegroundColor Green
    } else {
        Write-Host "Failed to create database. Error code: $LASTEXITCODE" -ForegroundColor Red
        Write-Host "Please check:" -ForegroundColor Yellow
        Write-Host "1. PostgreSQL is running" -ForegroundColor White
        Write-Host "2. Password for user 'postgres' is '1902'" -ForegroundColor White
        Write-Host "3. User 'postgres' has CREATE DATABASE privilege" -ForegroundColor White
    }
}

# Test connection
Write-Host ""
Write-Host "Testing connection..." -ForegroundColor Yellow
$testCmd = "& `"$psqlPath`" -U postgres -d distcomp -c `"SELECT 'Connection OK' AS status;`""
$testResult = Invoke-Expression $testCmd 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "Connection test successful!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Cyan
    Write-Host "1. Start your Spring Boot application" -ForegroundColor White
    Write-Host "2. Liquibase will create schema and tables automatically" -ForegroundColor White
    Write-Host "3. For test system, use these settings:" -ForegroundColor White
    Write-Host "   - URL: jdbc:postgresql://localhost:5432/distcomp" -ForegroundColor White
    Write-Host "   - Username: postgres" -ForegroundColor White
    Write-Host "   - Password: 1902" -ForegroundColor White
} else {
    Write-Host "Connection test failed!" -ForegroundColor Red
    Write-Host "Error: $testResult" -ForegroundColor Red
}

$env:PGPASSWORD = ""

