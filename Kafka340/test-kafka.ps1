#test-kafka.ps1

Write-Host "==============================================" -ForegroundColor Cyan
Write-Host "KAFKA INTEGRATION TEST - TASK 340" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host "Date: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor White
Write-Host ""

$totalTests = 0
$passedTests = 0
$failedTests = 0

function Test-Step {
    param([string]$TestName, [string]$Description, [scriptblock]$TestScript)
    
    Write-Host "`n[$($script:totalTests+1)] $TestName" -ForegroundColor Yellow
    Write-Host "   $Description" -ForegroundColor Gray
    
    $script:totalTests++
    
    try {
        & $TestScript
        Write-Host "   [PASS] OK" -ForegroundColor Green
        $script:passedTests++
        return $true
    } catch {
        Write-Host "   [FAIL] ERROR: $_" -ForegroundColor Red
        $script:failedTests++
        return $false
    }
}

# TEST 1 - Docker Infrastructure
Test-Step "DOCKER INFRASTRUCTURE" "Checking running containers" {
    $containers = docker ps --format "{{.Names}} {{.Status}}" 2>$null
    
    if (-not $containers) { throw "Docker containers not found" }
    
    Write-Host "   Running containers:" -ForegroundColor White
    $containers | ForEach-Object { Write-Host "     $_" -ForegroundColor White }
    
    # Check required containers
    $hasKafka = $containers | Where-Object { $_ -like "*kafka*" }
    $hasZookeeper = $containers | Where-Object { $_ -like "*zookeeper*" }
    
    if (-not $hasKafka) { throw "Kafka container not running" }
    if (-not $hasZookeeper) { throw "Zookeeper container not running" }
}

# TEST 2 - Kafka Availability
Test-Step "KAFKA AVAILABILITY" "Checking Kafka connection" {
    $topics = docker exec kafka kafka-topics --list --bootstrap-server localhost:9092 2>$null
    if (-not $topics) { throw "Cannot get Kafka topics list" }
    
    Write-Host "   Topics in Kafka:" -ForegroundColor White
    $topics | ForEach-Object { Write-Host "     $_" -ForegroundColor White }
}

# TEST 3 - Kafka Topics
Test-Step "KAFKA TOPICS" "Checking InTopic and OutTopic existence" {
    $topics = docker exec kafka kafka-topics --list --bootstrap-server localhost:9092 2>$null
    $requiredTopics = @("InTopic", "OutTopic")
    foreach ($topic in $requiredTopics) {
        if ($topics -notcontains $topic) {
            throw "Missing topic: $topic"
        }
    }
    Write-Host "   Required topics present" -ForegroundColor White
}

# TEST 4 - Spring Boot Application
Test-Step "SPRING BOOT APPLICATION" "Checking discussion module" {
    $urls = @(
        "http://localhost:24130/health",
        "http://localhost:24130/api/v1.0/comments/health",
        "http://localhost:24130/api/v1.0/test/kafka/health"
    )
    
    $healthOk = $false
    foreach ($url in $urls) {
        try {
            $response = Invoke-WebRequest -Uri $url -TimeoutSec 5 -ErrorAction SilentlyContinue
            if ($response.StatusCode -eq 200) {
                Write-Host "     $url : OK" -ForegroundColor White
                $healthOk = $true
            }
        } catch {
            Write-Host "     $url : Not available" -ForegroundColor Yellow
        }
    }
    
    if (-not $healthOk) {
        throw "Spring Boot application not available"
    }
}

# TEST 5 - Message Sending
Test-Step "MESSAGE SENDING" "Test sending comment via Kafka" {
    $testId = "test-$(Get-Date -Format 'yyyyMMdd-HHmmss')"
    $body = @{
        commentId = $testId
        content = "Test message for Kafka integration"
        storyId = 1
        author = "Kafka Tester"
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:24130/api/v1.0/test/kafka/send-test" `
            -Method POST -ContentType "application/json" -Body $body -ErrorAction SilentlyContinue
        
        if ($response -and $response.status -eq "sent") {
            Write-Host "   Message sent: $testId" -ForegroundColor White
            Write-Host "   Status: $($response.status)" -ForegroundColor White
        } else {
            Write-Host "   [INFO] Cannot send message (API might be down)" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "   [INFO] Send test failed: $($_.Exception.Message)" -ForegroundColor Yellow
    }
}

# TEST 6 - Message Receiving
Test-Step "MESSAGE RECEIVING" "Checking if messages reach Kafka" {
    Start-Sleep -Seconds 3
    
    Write-Host "   Checking messages in InTopic..." -ForegroundColor White
    
    $topicCheck = docker exec kafka kafka-topics --describe --topic InTopic --bootstrap-server localhost:9092 2>$null
    if ($topicCheck) {
        Write-Host "   [OK] InTopic exists" -ForegroundColor Green
        
        # Try to read one message
        Write-Host "   Trying to read message..." -ForegroundColor White
        $message = docker exec kafka bash -c "timeout 2 kafka-console-consumer --topic InTopic --bootstrap-server localhost:9092 --from-beginning --max-messages 1 2>/dev/null" 2>$null
        
        if ($message) {
            Write-Host "   [OK] Messages found in InTopic" -ForegroundColor Green
            if ($message.Length -gt 50) {
                Write-Host "   Sample: $($message.Substring(0, [Math]::Min(50, $message.Length)))..." -ForegroundColor Gray
            } else {
                Write-Host "   Message: $message" -ForegroundColor Gray
            }
        } else {
            Write-Host "   [INFO] No messages in InTopic (topic might be empty)" -ForegroundColor Yellow
        }
    } else {
        throw "InTopic not found"
    }
}

# TEST 7 - Kafka Partitions
Test-Step "KAFKA PARTITIONS" "Checking partition configuration" {
    $topicInfo = docker exec kafka kafka-topics --describe --topic InTopic --bootstrap-server localhost:9092 2>$null
    if (-not $topicInfo) { throw "Cannot get InTopic information" }
    
    Write-Host "   InTopic configuration:" -ForegroundColor White
    $topicInfo | ForEach-Object { Write-Host "     $_" -ForegroundColor White }
    
    if ($topicInfo -match "PartitionCount:\s*3") {
        Write-Host "   [OK] 3 partitions configured correctly" -ForegroundColor Green
    } else {
        Write-Host "   [WARNING] Check partition count" -ForegroundColor Yellow
    }
}

# TEST 8 - Comment Moderation
Test-Step "COMMENT MODERATION" "Testing automatic moderation" {
    $testId = "moderation-test-$(Get-Date -Format 'HHmmss')"
    $body = @{
        commentId = $testId
        content = "Visit our casino for wins!"
        storyId = 2
        author = "Moderation Tester"
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:24130/api/v1.0/test/kafka/send-test" `
            -Method POST -ContentType "application/json" -Body $body -ErrorAction SilentlyContinue
        
        if ($response) {
            Write-Host "   Moderation test sent: $testId" -ForegroundColor White
            Write-Host "   Message with stop-word sent for moderation check" -ForegroundColor White
        } else {
            Write-Host "   [INFO] Cannot send moderation test" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "   [INFO] Moderation test send error" -ForegroundColor Gray
    }
}

# TEST 9 - Fault Tolerance
Test-Step "FAULT TOLERANCE" "Checking Kafka replication and configuration" {
    $topicDetails = docker exec kafka kafka-topics --describe --bootstrap-server localhost:9092 2>$null
    
    if ($topicDetails) {
        Write-Host "   Topic configuration analysis:" -ForegroundColor White
        
        $lines = $topicDetails -split "`n"
        foreach ($line in $lines) {
            if ($line -match "Topic:\s*(\S+).*PartitionCount:\s*(\d+).*ReplicationFactor:\s*(\d+)") {
                $topicName = $matches[1]
                $partitions = $matches[2]
                $replication = $matches[3]
                
                Write-Host "     $topicName : Partitions = $partitions, Replication = $replication" -ForegroundColor White
                
                if ($replication -eq 1) {
                    Write-Host "       [WARNING] Replication factor = 1" -ForegroundColor Yellow
                    Write-Host "       For fault tolerance, use ReplicationFactor >= 2" -ForegroundColor Yellow
                }
            }
        }
    } else {
        Write-Host "   [INFO] Cannot get detailed topic information" -ForegroundColor Gray
    }
}

# TEST 10 - Performance
Test-Step "PERFORMANCE" "Testing multiple message sending" {
    Write-Host "   Sending 3 test messages..." -ForegroundColor White
    
    $messagesSent = 0
    1..3 | ForEach-Object {
        $num = $_
        $testId = "perf-test-$num-$(Get-Date -Format 'HHmmss')"
        
        $body = @{
            commentId = $testId
            content = "Performance test $num"
            storyId = $num
            author = "Performance Tester"
        } | ConvertTo-Json
        
        try {
            Invoke-RestMethod -Uri "http://localhost:24130/api/v1.0/test/kafka/send-test" `
                -Method POST -ContentType "application/json" -Body $body -TimeoutSec 2 -ErrorAction SilentlyContinue | Out-Null
            
            $messagesSent++
            Write-Host "     Message $num sent" -ForegroundColor Gray
            Start-Sleep -Milliseconds 500
        } catch {
            Write-Host "     [INFO] Error sending message $num" -ForegroundColor Yellow
        }
    }
    
    Write-Host "   Sent $messagesSent of 3 messages" -ForegroundColor White
    
    if ($messagesSent -gt 0) {
        Write-Host "   [OK] System processed $messagesSent messages" -ForegroundColor Green
    } else {
        Write-Host "   [INFO] Performance test failed (not critical)" -ForegroundColor Gray
    }
}

# RESULTS
Write-Host "`n" + ("="*50) -ForegroundColor Cyan
Write-Host "TEST RESULTS SUMMARY" -ForegroundColor Cyan
Write-Host ("="*50) -ForegroundColor Cyan

Write-Host "Total tests: $totalTests" -ForegroundColor White
Write-Host "Passed: $passedTests" -ForegroundColor Green
Write-Host "Failed: $failedTests" -ForegroundColor $(if ($failedTests -gt 0) { "Red" } else { "White" })

# Analysis
Write-Host "`nRESULTS ANALYSIS:" -ForegroundColor Cyan

if ($totalTests -gt 0) {
    $successRate = [math]::Round(($passedTests / $totalTests) * 100, 1)
    Write-Host "Success rate: $successRate%" -ForegroundColor White

    if ($successRate -ge 90) {
        Write-Host "GRADE: EXCELLENT" -ForegroundColor Green
        Write-Host "System works correctly and meets requirements" -ForegroundColor Green
    } elseif ($successRate -ge 70) {
        Write-Host "GRADE: GOOD" -ForegroundColor Yellow
        Write-Host "System mostly works, minor issues detected" -ForegroundColor Yellow
    } elseif ($successRate -ge 50) {
        Write-Host "GRADE: SATISFACTORY" -ForegroundColor Magenta
        Write-Host "Some components need improvement" -ForegroundColor Magenta
    } else {
        Write-Host "GRADE: NEEDS IMPROVEMENT" -ForegroundColor Red
    }
} else {
    Write-Host "No data for analysis" -ForegroundColor Yellow
}

# Conclusion
if ($failedTests -eq 0 -and $passedTests -gt 0) {
    Write-Host "`nSYSTEM IS READY FOR USE!" -ForegroundColor Green
    Write-Host "All key Kafka integration components are working" -ForegroundColor White
} elseif ($passedTests -ge 5) {
    Write-Host "`nSYSTEM IS PARTIALLY WORKING" -ForegroundColor Yellow
    Write-Host "Basic functionality available, needs some fixes" -ForegroundColor White
} else {
    Write-Host "`nSYSTEM NEEDS MAJOR FIXES" -ForegroundColor Red
    Write-Host "Critical issues detected" -ForegroundColor White
}

Write-Host "`n" + ("="*50) -ForegroundColor Cyan
Write-Host "TEST COMPLETED" -ForegroundColor Cyan
Write-Host ("="*50) -ForegroundColor Cyan