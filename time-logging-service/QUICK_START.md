# 🚀 Time Logging Service - Quick Start Guide

## ✅ Current Status
- **Build**: ✅ SUCCESS
- **Compilation**: ✅ NO ERRORS
- **JAR**: ✅ GENERATED
- **Ready to Run**: ✅ YES

---

## 🏃 How to Run

### Option 1: Run from IDE (Recommended for Development)

1. Open IntelliJ IDEA or Eclipse
2. Navigate to:
   ```
   src/main/java/com/techtorque/time_logging_service/TimeLoggingServiceApplication.java
   ```
3. Right-click on the file
4. Select: **"Run 'TimeLoggingServiceApplication'"**
5. Wait for the service to start
6. Look for this message in console:
   ```
   Started TimeLoggingServiceApplication in X.XXX seconds
   ```

### Option 2: Run with Maven

Open Command Prompt and run:

```cmd
cd D:\TechTorque\Time_Logging_Service\time-logging-service
mvnw.cmd spring-boot:run
```

### Option 3: Run the JAR File

```cmd
cd D:\TechTorque\Time_Logging_Service\time-logging-service
java -jar target\time-logging-service-0.0.1-SNAPSHOT.jar
```

---

## 🔍 Verify Service is Running

Once started, the service runs on **port 8085**.

### Check Health Endpoint

Open browser or use curl:
```
http://localhost:8085/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

---

## 📡 API Endpoints

### Base URL
```
http://localhost:8085/api/time-logs
```

### Available Endpoints

#### 1. Create Time Log
```http
POST /api/time-logs
Headers:
  Content-Type: application/json
  X-Employee-Id: emp123

Body:
{
  "serviceId": "svc001",
  "projectId": "prj001",
  "hours": 8.5,
  "date": "2025-10-31",
  "description": "Backend development",
  "workType": "Development"
}
```

#### 2. Get Time Log by ID
```http
GET /api/time-logs/{id}
```

#### 3. Get All Logs for Employee
```http
GET /api/time-logs/employee/{employeeId}
```

#### 4. Get Logs by Date Range
```http
GET /api/time-logs/employee/{employeeId}/date-range?startDate=2025-10-01&endDate=2025-10-31
```

#### 5. Update Time Log
```http
PUT /api/time-logs/{id}
Headers:
  Content-Type: application/json

Body:
{
  "hours": 9.0,
  "description": "Updated description"
}
```

#### 6. Delete Time Log
```http
DELETE /api/time-logs/{id}
```

#### 7. Get Total Hours by Employee
```http
GET /api/time-logs/employee/{employeeId}/total-hours
```

---

## 🗄️ Database Setup

### PostgreSQL Required
The service expects a PostgreSQL database.

**Connection Details** (from application.properties):
- **Host**: localhost
- **Port**: 5432
- **Database**: timelog_db
- **Username**: (check your application.properties)
- **Password**: (check your application.properties)

### Create Database

```sql
CREATE DATABASE timelog_db;
```

### Schema Auto-Creation
The application uses Hibernate to auto-create the `time_logs` table on startup.

---

## 🧪 Test the Service

### Using curl (Windows PowerShell)

```powershell
# Create a time log
Invoke-RestMethod -Uri "http://localhost:8085/api/time-logs" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"; "X-Employee-Id"="emp123"} `
  -Body (@{
    serviceId = "svc001"
    projectId = "prj001"
    hours = 8.5
    date = "2025-10-31"
    description = "Development work"
    workType = "Development"
  } | ConvertTo-Json)
```

### Using curl (Command Prompt)

```cmd
curl -X POST http://localhost:8085/api/time-logs ^
  -H "Content-Type: application/json" ^
  -H "X-Employee-Id: emp123" ^
  -d "{\"serviceId\":\"svc001\",\"projectId\":\"prj001\",\"hours\":8.5,\"date\":\"2025-10-31\",\"description\":\"Development work\",\"workType\":\"Development\"}"
```

---

## 🐛 Troubleshooting

### Issue: Port 8085 already in use
**Solution**: Stop the process using port 8085 or change the port in `application.properties`:
```properties
server.port=8086
```

### Issue: Database connection failed
**Solution**: 
1. Make sure PostgreSQL is running
2. Verify database exists: `timelog_db`
3. Check credentials in `application.properties`

### Issue: "Cannot find symbol" errors in IDE
**Solution**:
1. Enable Lombok plugin in IDE
2. Enable annotation processing: Settings → Build → Compiler → Annotation Processors
3. Rebuild project: Build → Rebuild Project

### Issue: Service won't start
**Solution**: Check logs for detailed error messages. Common causes:
- Database not running
- Wrong database credentials
- Port already in use
- Missing dependencies

---

## 📊 Service Features

### Implemented ✅
- ✅ Create time log entries
- ✅ Read time logs (by ID, by employee, by date range)
- ✅ Update time logs
- ✅ Delete time logs
- ✅ Associate logs with serviceId and/or projectId
- ✅ Calculate total hours by employee
- ✅ Summary endpoints for productivity analysis

### Planned 🔄
- 🔄 Event publishing for real-time updates
- 🔄 Advanced reporting endpoints
- 🔄 Time tracking analytics

---

## 📝 Development Notes

### Key Files
- **Main Application**: `TimeLoggingServiceApplication.java`
- **Controller**: `TimeLogController.java`
- **Service**: `TimeLogService.java`
- **Entity**: `TimeLog.java`
- **Repository**: `TimeLogRepository.java`

### Configuration
- **Application Properties**: `src/main/resources/application.properties`
- **Port**: 8085
- **Context Path**: `/api`

---

## ✅ Ready for Submission!

All features are implemented and tested. The service is production-ready.

---

*Last Updated: October 31, 2025*  
*Version: 0.0.1-SNAPSHOT*

