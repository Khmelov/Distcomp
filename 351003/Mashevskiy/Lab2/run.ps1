$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

Write-Host "========================================" -ForegroundColor Green
Write-Host "Building with Java:" -ForegroundColor Green
java -version
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

Set-Location D:\download\rest-api\rest-api

Write-Host "Running Maven clean compile..." -ForegroundColor Yellow
cmd /c "mvnw.cmd clean compile"

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "Build successful!" -ForegroundColor Green
    Write-Host "Starting application..." -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    cmd /c "mvnw.cmd spring-boot:run"
} else {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "Build failed with error code: $LASTEXITCODE" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
}
