# Script to change PostgreSQL password to 'postgres' for test system compatibility
# WARNING: This will change the password for user 'postgres' to 'postgres'
# Run: .\change-password-to-postgres.ps1

$psqlPath = "C:\Program Files\PostgreSQL\18\bin\psql.exe"

Write-Host "=== Changing PostgreSQL Password ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "WARNING: This will change password for user 'postgres' to 'postgres'" -ForegroundColor Yellow
Write-Host ""

$confirm = Read-Host "Do you want to continue? (yes/no)"

if ($confirm -ne "yes") {
    Write-Host "Cancelled." -ForegroundColor Yellow
    exit 0
}

Write-Host ""
Write-Host "Changing password..." -ForegroundColor Yellow

$env:PGPASSWORD = "1902"
$changeCmd = "& `"$psqlPath`" -U postgres -c `"ALTER USER postgres WITH PASSWORD 'postgres';`""
Invoke-Expression $changeCmd 2>&1 | Out-Null

if ($LASTEXITCODE -eq 0) {
    Write-Host "Password changed successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "IMPORTANT: You also need to update application.properties:" -ForegroundColor Yellow
    Write-Host "Change: spring.datasource.password=1902" -ForegroundColor White
    Write-Host "To:     spring.datasource.password=postgres" -ForegroundColor White
    Write-Host ""
    Write-Host "Then restart your application." -ForegroundColor Yellow
} else {
    Write-Host "Failed to change password. Error code: $LASTEXITCODE" -ForegroundColor Red
}

$env:PGPASSWORD = ""

