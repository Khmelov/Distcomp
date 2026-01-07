# PowerShell script to check database connection and setup
# Run this script: .\check-database.ps1

Write-Host "=== Database Connection Check ===" -ForegroundColor Cyan
Write-Host ""

# Set password
$env:PGPASSWORD = "1902"

Write-Host "1. Checking if 'distcomp' database exists..." -ForegroundColor Yellow
$dbCheck = psql -U postgres -d postgres -t -c "SELECT 1 FROM pg_database WHERE datname='distcomp';" 2>&1

if ($LASTEXITCODE -eq 0 -and $dbCheck.Trim() -eq "1") {
    Write-Host "   ✓ Database 'distcomp' exists" -ForegroundColor Green
} else {
    Write-Host "   ✗ Database 'distcomp' does NOT exist" -ForegroundColor Red
    Write-Host "   Creating database..." -ForegroundColor Yellow
    psql -U postgres -c "CREATE DATABASE distcomp;" 2>&1 | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✓ Database 'distcomp' created successfully" -ForegroundColor Green
    } else {
        Write-Host "   ✗ Failed to create database" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "2. Testing connection to 'distcomp' database..." -ForegroundColor Yellow
$connTest = psql -U postgres -d distcomp -c "SELECT version();" 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✓ Connection successful!" -ForegroundColor Green
} else {
    Write-Host "   ✗ Connection failed" -ForegroundColor Red
    Write-Host "   Error: $connTest" -ForegroundColor Red
}

Write-Host ""
Write-Host "3. Checking if 'distcomp' schema exists..." -ForegroundColor Yellow
$schemaCheck = psql -U postgres -d distcomp -t -c "SELECT 1 FROM information_schema.schemata WHERE schema_name='distcomp';" 2>&1

if ($LASTEXITCODE -eq 0 -and $schemaCheck.Trim() -eq "1") {
    Write-Host "   ✓ Schema 'distcomp' exists" -ForegroundColor Green
} else {
    Write-Host "   ✗ Schema 'distcomp' does NOT exist" -ForegroundColor Yellow
    Write-Host "   (Schema will be created by Liquibase on application startup)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "4. Checking tables..." -ForegroundColor Yellow
$tables = psql -U postgres -d distcomp -t -c "SELECT table_name FROM information_schema.tables WHERE table_schema='distcomp' AND table_name LIKE 'tbl_%' ORDER BY table_name;" 2>&1

if ($LASTEXITCODE -eq 0 -and $tables.Trim() -ne "") {
    Write-Host "   ✓ Tables found:" -ForegroundColor Green
    $tables.Trim().Split("`n") | ForEach-Object { Write-Host "     - $_" -ForegroundColor Green }
} else {
    Write-Host "   ✗ No tables found (or schema doesn't exist)" -ForegroundColor Yellow
    Write-Host "   (Tables will be created by Liquibase on application startup)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "5. Testing sample query..." -ForegroundColor Yellow
$sampleQuery = psql -U postgres -d distcomp -t -c "SELECT COUNT(*) FROM distcomp.tbl_user;" 2>&1

if ($LASTEXITCODE -eq 0) {
    $count = $sampleQuery.Trim()
    Write-Host "   ✓ Sample query successful. User count: $count" -ForegroundColor Green
} else {
    Write-Host "   ✗ Sample query failed (table might not exist yet)" -ForegroundColor Yellow
    Write-Host "   Error: $sampleQuery" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== Connection Summary ===" -ForegroundColor Cyan
Write-Host "JDBC URL: jdbc:postgresql://localhost:5432/distcomp" -ForegroundColor White
Write-Host "Username: postgres" -ForegroundColor White
Write-Host "Password: 1902" -ForegroundColor White
Write-Host "Schema: distcomp" -ForegroundColor White
Write-Host ""

$env:PGPASSWORD = ""

