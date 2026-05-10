$JavaExe = "C:\Program Files\Java\jdk-21.0.10\bin\java.exe"
$MavenJar = ".mvn/wrapper/maven-wrapper.jar"

Set-Location D:\download\rest-api\rest-api

Write-Host "========================================" -ForegroundColor Green
Write-Host "Java version:" -ForegroundColor Green
& $JavaExe -version
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

Write-Host "Building project..." -ForegroundColor Yellow
& $JavaExe -jar $MavenJar clean compile

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "Build successful!" -ForegroundColor Green
    Write-Host "Starting application..." -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    & $JavaExe -jar $MavenJar spring-boot:run
} else {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "Build failed with error code: $LASTEXITCODE" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
}
