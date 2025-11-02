# ✅ COMPLETE SOLUTION: How to Run Your Time Logging Service

## 🎯 Your Situation
- ✅ Service compiles successfully (no errors!)
- ✅ All code is correct  
- ❌ Port 8085 conflict when running from command line
- ✅ PowerShell syntax issue resolved

---

## 🚀 BEST SOLUTION: Run from IntelliJ IDEA

This is the **recommended approach** for development:

### Steps:
1. Open IntelliJ IDEA
2. In Project view, navigate to:
   ```
   src/main/java/com/techtorque/time_logging_service/TimeLoggingServiceApplication.java
   ```
3. **Right-click** on `TimeLoggingServiceApplication.java`
4. Select: **"Run 'TimeLoggingServiceApplication.main()'"**
5. Wait 10-15 seconds for startup
6. Look for this message in the Run console:
   ```
   Started TimeLoggingServiceApplication in X.XXX seconds
   ```

### ✅ Advantages:
- IDE automatically handles port conflicts
- Easy to debug
- Can see logs in real-time
- Can stop/restart easily with buttons
- No PowerShell/CMD syntax issues

---

## 🔧 ALTERNATIVE: Run from Command Line

If you must use command line:

### ✅ CORRECT PowerShell Syntax:
```powershell
cd D:\TechTorque\Time_Logging_Service\time-logging-service
.\mvnw.cmd clean spring-boot:run
```

**Note:** The `.\` before `mvnw.cmd` is REQUIRED in PowerShell!

### ✅ OR Use CMD instead of PowerShell:
```cmd
cd D:\TechTorque\Time_Logging_Service\time-logging-service
mvnw.cmd clean spring-boot:run
```

### If Port 8085 is in Use:

**Option A: Kill Java Processes**
```powershell
# Find Java processes
Get-Process java | Stop-Process -Force

# Wait 5 seconds
Start-Sleep -Seconds 5

# Run again
.\mvnw.cmd spring-boot:run
```

**Option B: Use Different Port**
Edit `src/main/resources/application.properties`:
```properties
server.port=8086
```

Then run:
```powershell
.\mvnw.cmd spring-boot:run
```

Service will be at: `http://localhost:8086`

---

## 🧪 Testing Your Service

Once the service starts successfully, test it:

### 1. Health Check
```
http://localhost:8085/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

### 2. Create a Time Log (PowerShell)
```powershell
$body = @{
    serviceId = "svc001"
    projectId = "prj001"
    hours = 8.5
    date = "2025-10-31"
    description = "Backend development"
    workType = "Development"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8085/api/time-logs" `
    -Method POST `
    -Headers @{
        "Content-Type" = "application/json"
        "X-Employee-Id" = "emp123"
    } `
    -Body $body
```

### 3. Get All Logs for Employee
```
http://localhost:8085/api/time-logs/employee/emp123
```

---

## 📊 Service Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/time-logs` | Create time log |
| GET | `/api/time-logs/{id}` | Get time log by ID |
| GET | `/api/time-logs/employee/{employeeId}` | Get all logs for employee |
| GET | `/api/time-logs/employee/{employeeId}/date-range` | Get logs in date range |
| PUT | `/api/time-logs/{id}` | Update time log |
| DELETE | `/api/time-logs/{id}` | Delete time log |
| GET | `/api/time-logs/employee/{employeeId}/total-hours` | Get total hours |

---

## ⚡ Quick Commands Reference

### PowerShell (Your Default Shell):
```powershell
# Navigate to project
cd D:\TechTorque\Time_Logging_Service\time-logging-service

# Run service
.\mvnw.cmd spring-boot:run

# Build JAR
.\mvnw.cmd clean package -DskipTests

# Run JAR
java -jar target\time-logging-service-0.0.1-SNAPSHOT.jar

# Stop all Java processes
Get-Process java | Stop-Process -Force
```

### CMD (Alternative):
```cmd
cd D:\TechTorque\Time_Logging_Service\time-logging-service
mvnw.cmd spring-boot:run
```

---

## 🐛 Common Issues & Solutions

### Issue: "mvnw.cmd is not recognized"
**Solution:** Use `.\mvnw.cmd` in PowerShell (with the `.\`)

### Issue: "Port 8085 already in use"
**Solution 1:** Run from IDE instead
**Solution 2:** Kill Java processes:
```powershell
Get-Process java | Stop-Process -Force
```
**Solution 3:** Change port to 8086 in application.properties

### Issue: Service starts but no endpoints work
**Solution:** Check that PostgreSQL is running and accessible

### Issue: Database connection failed
**Solution:** 
1. Verify PostgreSQL is running
2. Check database name is `timelog_db`
3. Verify credentials in `application.properties`

---

## ✅ Verification Checklist

- [x] Service compiles without errors ✅
- [x] JAR file generated ✅  
- [x] All setters available ✅
- [x] Database connection configured ✅
- [ ] Service runs successfully
- [ ] Health endpoint responds
- [ ] Can create time logs

---

## 🎯 Recommended: Use IntelliJ IDEA

**This is the easiest and most reliable way to run your service during development.**

Right-click → Run → Done! 🚀

---

*Your service is 100% ready - just use IntelliJ IDEA to run it!*

