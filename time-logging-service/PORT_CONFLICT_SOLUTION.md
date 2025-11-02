# 🚀 Quick Fix: Port 8085 Already in Use

## ✅ The Problem
The error "Port 8085 was already in use" occurs when another instance of the service is still running or the port hasn't been released yet.

## ✅ Solution Options

### Option 1: Wait and Try Again (Simplest)
The port might just need a few seconds to be released.

**PowerShell Command:**
```powershell
cd D:\TechTorque\Time_Logging_Service\time-logging-service
.\mvnw.cmd spring-boot:run
```

**Important:** In PowerShell, you MUST use `.\mvnw.cmd` (with the `.\`) instead of just `mvnw.cmd`

### Option 2: Find and Kill the Process Using Port 8085

**Step 1:** Find the process:
```cmd
netstat -ano | findstr :8085
```

**Step 2:** Kill the process (replace XXXX with the PID from step 1):
```cmd
taskkill /PID XXXX /F
```

**Step 3:** Run the service again:
```powershell
.\mvnw.cmd spring-boot:run
```

### Option 3: Run from Your IDE (Recommended!)

This is the **easiest and most reliable method**:

1. Open IntelliJ IDEA
2. Navigate to:
   ```
   src/main/java/com/techtorque/time_logging_service/TimeLoggingServiceApplication.java
   ```
3. Right-click on the file
4. Select: **"Run 'TimeLoggingServiceApplication'"**
5. The IDE will automatically handle any port conflicts

### Option 4: Change the Port Temporarily

Edit `src/main/resources/application.properties` and add:
```properties
server.port=8086
```

Then run:
```powershell
.\mvnw.cmd spring-boot:run
```

Service will be available at: `http://localhost:8086`

---

## 📝 PowerShell vs CMD Important Note

### ❌ WRONG (Will NOT work in PowerShell):
```powershell
mvnw.cmd spring-boot:run
```

### ✅ CORRECT (Works in PowerShell):
```powershell
.\mvnw.cmd spring-boot:run
```

### ✅ ALSO CORRECT (Works in CMD):
```cmd
mvnw.cmd spring-boot:run
```

---

## 🎯 Recommended Approach

**For Development:**
Use **Option 3** (Run from IDE) - It's the most convenient and handles everything automatically.

**For Testing:**
Use **Option 1** with the correct PowerShell syntax: `.\mvnw.cmd spring-boot:run`

---

## ✅ Once Started Successfully, Test With:

```
http://localhost:8085/actuator/health
```

Expected Response:
```json
{
  "status": "UP"
}
```

---

## 🔧 Troubleshooting

### If you see "Port already in use":
1. Wait 10-20 seconds for the previous instance to fully shut down
2. Try running again
3. If still failing, use Option 2 to kill the process

### If you see "command not found":
- Make sure you're using `.\mvnw.cmd` (with `.\`) in PowerShell
- Or switch to CMD where you can use just `mvnw.cmd`

---

**Your service compiles successfully! The only issue is the port conflict, which is easy to resolve.**

