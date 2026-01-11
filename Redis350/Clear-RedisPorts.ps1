# Clear-RedisPorts.ps1
# Очистка портов Redis (6380-6390)

Write-Host "=== Очистка портов Redis ===" -ForegroundColor Yellow

$ports = 6380..6390

foreach ($port in $ports) {
    Write-Host "`nПорт $port:" -ForegroundColor Cyan
    
    # 1. Ищем процесс, использующий порт
    Write-Host "   Поиск процесса..." -NoNewline -ForegroundColor Gray
    $process = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    
    if ($process) {
        $pid = $process.OwningProcess
        $procName = (Get-Process -Id $pid -ErrorAction SilentlyContinue).Name
        Write-Host " занят процессом $procName (PID: $pid)" -ForegroundColor Yellow
        
        # Предлагаем завершить
        $answer = Read-Host "   Завершить процесс? (y/n)"
        if ($answer -eq "y") {
            Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
            Write-Host "   [✓] Процесс завершен" -ForegroundColor Green
        }
    }
    else {
        Write-Host " свободен" -ForegroundColor Green
    }
    
    # 2. Ищем Docker контейнеры
    Write-Host "   Поиск контейнеров Docker..." -NoNewline -ForegroundColor Gray
    $containers = docker ps -a --format "{{.Names}}:{{.Ports}}" | Where-Object { $_ -match ":$port->" }
    
    if ($containers) {
        Write-Host " найдены контейнеры:" -ForegroundColor Yellow
        foreach ($container in $containers) {
            $name = $container.Split(":")[0]
            Write-Host "     - $name" -ForegroundColor Gray
            
            # Останавливаем и удаляем
            docker stop $name 2>$null
            docker rm $name 2>$null
            Write-Host "       [✓] Остановлен и удален" -ForegroundColor Green
        }
    }
    else {
        Write-Host " не найдено" -ForegroundColor Green
    }
}

Write-Host "`n=== ОЧИСТКА ЗАВЕРШЕНА ===" -ForegroundColor Green
Write-Host "Порты 6380-6390 теперь должны быть свободны." -ForegroundColor Gray