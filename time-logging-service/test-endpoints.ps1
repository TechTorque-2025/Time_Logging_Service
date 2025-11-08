# Time Logging Service - API Endpoint Testing Script
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Time Logging Service - Endpoint Tests" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$baseUrl = "http://localhost:8085"
$employeeId = "EMP001"
$headers = @{
    "X-Employee-Id" = $employeeId
    "Content-Type" = "application/json"
}

# Test 1: Health Check
Write-Host "TEST 1: Health Check..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/actuator/health" -UseBasicParsing -ErrorAction Stop
    Write-Host "✓ Health Check: $($response.StatusCode) - Service is UP" -ForegroundColor Green
    Write-Host $response.Content
} catch {
    Write-Host "✗ Health Check Failed: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 2: Create Time Log
Write-Host "[TEST 2] Create Time Log..." -ForegroundColor Yellow
$createPayload = @{
    serviceId = "SRV001"
    projectId = "PRJ001"
    hours = 8.5
    date = "2025-10-31"
    description = "Working on API development"
    workType = "Development"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/time-logs" -Method POST -Headers $headers -Body $createPayload -UseBasicParsing -ErrorAction Stop
    Write-Host "✓ Create Time Log: $($response.StatusCode)" -ForegroundColor Green
    $createdLog = $response.Content | ConvertFrom-Json
    Write-Host "  Created Log ID: $($createdLog.id)" -ForegroundColor Cyan
    $logId = $createdLog.id
} catch {
    Write-Host "✗ Create Time Log Failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $errorDetails = $reader.ReadToEnd()
        Write-Host "  Error Details: $errorDetails" -ForegroundColor Red
    }
}
Write-Host ""

# Test 3: Get Time Log by ID
if ($logId) {
    Write-Host "[TEST 3] Get Time Log by ID..." -ForegroundColor Yellow
    try {
        $response = Invoke-WebRequest -Uri "$baseUrl/api/time-logs/$logId" -UseBasicParsing -ErrorAction Stop
        Write-Host "✓ Get Time Log: $($response.StatusCode)" -ForegroundColor Green
        Write-Host $response.Content
    } catch {
        Write-Host "✗ Get Time Log Failed: $($_.Exception.Message)" -ForegroundColor Red
    }
    Write-Host ""
}

# Test 4: Get All Time Logs for Employee
Write-Host "[TEST 4] Get All Time Logs for Employee..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/time-logs/employee/$employeeId" -UseBasicParsing -ErrorAction Stop
    Write-Host "✓ Get Employee Time Logs: $($response.StatusCode)" -ForegroundColor Green
    $logs = $response.Content | ConvertFrom-Json
    Write-Host "  Total Logs: $($logs.Count)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Get Employee Time Logs Failed: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 5: Get Time Logs by Date Range
Write-Host "[TEST 5] Get Time Logs by Date Range..." -ForegroundColor Yellow
try {
    $startDate = "2025-10-01"
    $endDate = "2025-10-31"
    $response = Invoke-WebRequest -Uri "$baseUrl/api/time-logs/employee/$employeeId/date-range?startDate=$startDate&endDate=$endDate" -UseBasicParsing -ErrorAction Stop
    Write-Host "✓ Get Time Logs by Date Range: $($response.StatusCode)" -ForegroundColor Green
    $logs = $response.Content | ConvertFrom-Json
    Write-Host "  Logs in Range: $($logs.Count)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Get Time Logs by Date Range Failed: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 6: Get Total Hours
Write-Host "[TEST 6] Get Total Hours for Employee..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/time-logs/employee/$employeeId/total-hours" -UseBasicParsing -ErrorAction Stop
    Write-Host "✓ Get Total Hours: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "  Total Hours: $($response.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Get Total Hours Failed: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 7: Get Employee Summary (NEW - Productivity Analysis)
Write-Host "[TEST 7] Get Employee Summary (Productivity Analysis)..." -ForegroundColor Yellow
try {
    $startDate = "2025-10-01"
    $endDate = "2025-10-31"
    $response = Invoke-WebRequest -Uri "$baseUrl/api/time-logs/employee/$employeeId/summary?startDate=$startDate&endDate=$endDate" -UseBasicParsing -ErrorAction Stop
    Write-Host "✓ Get Employee Summary: $($response.StatusCode)" -ForegroundColor Green
    Write-Host $response.Content
} catch {
    Write-Host "✗ Get Employee Summary Failed: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 8: Update Time Log
if ($logId) {
    Write-Host "[TEST 8] Update Time Log..." -ForegroundColor Yellow
    $updatePayload = @{
        hours = 9.0
        description = "Updated: Working on API development and testing"
    } | ConvertTo-Json

    try {
        $response = Invoke-WebRequest -Uri "$baseUrl/api/time-logs/$logId" -Method PUT -Headers $headers -Body $updatePayload -UseBasicParsing -ErrorAction Stop
        Write-Host "✓ Update Time Log: $($response.StatusCode)" -ForegroundColor Green
    } catch {
        Write-Host "✗ Update Time Log Failed: $($_.Exception.Message)" -ForegroundColor Red
    }
    Write-Host ""
}

# Test 9: Delete Time Log
if ($logId) {
    Write-Host "[TEST 9] Delete Time Log..." -ForegroundColor Yellow
    try {
        $response = Invoke-WebRequest -Uri "$baseUrl/api/time-logs/$logId" -Method DELETE -UseBasicParsing -ErrorAction Stop
        Write-Host "✓ Delete Time Log: $($response.StatusCode)" -ForegroundColor Green
    } catch {
        Write-Host "✗ Delete Time Log Failed: $($_.Exception.Message)" -ForegroundColor Red
    }
    Write-Host ""
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "Test Suite Completed!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

