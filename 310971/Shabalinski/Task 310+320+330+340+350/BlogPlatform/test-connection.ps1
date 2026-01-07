# Test database connection with different passwords
# Run: .\test-connection.ps1

$psqlPath = "C:\Program Files\PostgreSQL\18\bin\psql.exe"

Write-Host "=== Testing Database Connection ===" -ForegroundColor Cyan
Write-Host ""

# Test with password 1902
Write-Host "Test 1: Connection with password '1902'..." -ForegroundColor Yellow
$env:PGPASSWORD = "1902"
$result1 = & $psqlPath -U postgres -d distcomp -c "SELECT 'Connection OK with password 1902' AS status;" 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "   OK: Connection successful with password '1902'" -ForegroundColor Green
} else {
    Write-Host "   FAILED: Connection failed with password '1902'" -ForegroundColor Red
    Write-Host "   Error: $result1" -ForegroundColor Red
}

Write-Host ""

# Test with password postgres
Write-Host "Test 2: Connection with password 'postgres'..." -ForegroundColor Yellow
$env:PGPASSWORD = "postgres"
$result2 = & $psqlPath -U postgres -d distcomp -c "SELECT 'Connection OK with password postgres' AS status;" 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "   OK: Connection successful with password 'postgres'" -ForegroundColor Green
    Write-Host "   WARNING: Test system might be using password 'postgres'" -ForegroundColor Yellow
} else {
    Write-Host "   FAILED: Connection failed with password 'postgres'" -ForegroundColor Red
}

Write-Host ""

# Test query
Write-Host "Test 3: Sample query to distcomp.tbl_user..." -ForegroundColor Yellow
$env:PGPASSWORD = "1902"
$queryResult = & $psqlPath -U postgres -d distcomp -t -c "SELECT COUNT(*) FROM distcomp.tbl_user;" 2>&1

if ($LASTEXITCODE -eq 0) {
    $count = $queryResult.Trim()
    Write-Host "   OK: Query successful. Found $count user(s)" -ForegroundColor Green
} else {
    Write-Host "   FAILED: Query failed" -ForegroundColor Red
    Write-Host "   Error: $queryResult" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Summary ===" -ForegroundColor Cyan
Write-Host "Database: distcomp" -ForegroundColor White
Write-Host "Schema: distcomp" -ForegroundColor White
Write-Host "Tables: tbl_user, tbl_article, tbl_label, tbl_post, tbl_article_label" -ForegroundColor White
Write-Host ""
Write-Host "If test system still fails, check:" -ForegroundColor Yellow
Write-Host "1. Test system configuration file" -ForegroundColor White
Write-Host "2. Password in test system settings" -ForegroundColor White
Write-Host "3. JDBC URL format" -ForegroundColor White

$env:PGPASSWORD = ""

