# Time Logging Service - Quick Start Guide

## 🚀 Quick Start (5 minutes)

### 1. Prerequisites

```bash
✅ Java 17+
✅ Maven 3.6+
✅ PostgreSQL 15+
✅ Docker (optional)
```

### 2. Database Setup

```sql
-- Create database
CREATE DATABASE techtorque_timelogs;

-- Create user (if not exists)
CREATE USER techtorque WITH PASSWORD 'techtorque123';
GRANT ALL PRIVILEGES ON DATABASE techtorque_timelogs TO techtorque;
```

### 3. Run the Service

```bash
# Navigate to service directory
cd Time_Logging_Service/time-logging-service

# Run with Maven
mvn spring-boot:run

# Or build and run JAR
mvn package
java -jar target/time-logging-service-0.0.1-SNAPSHOT.jar
```

### 4. Verify Running

```bash
# Check health
curl http://localhost:8085/actuator/health

# Expected response: {"status":"UP"}
```

### 5. Access Swagger UI

Open in browser: **http://localhost:8085/swagger-ui/index.html**

---

## 📚 API Endpoints Cheat Sheet

### Create Time Log
```bash
POST /time-logs
Header: X-User-Subject: {employee-uuid}
Body: {
  "serviceId": "SRV-001",
  "projectId": "PRJ-001",
  "hours": 4.5,
  "date": "2025-11-05",
  "description": "Fixed brake system",
  "workType": "Repair"
}
```

### Get My Time Logs
```bash
GET /time-logs
Header: X-User-Subject: {employee-uuid}

# With date filtering
GET /time-logs?from=2025-11-01&to=2025-11-05
```

### Get Daily Summary
```bash
GET /time-logs/summary?period=daily&date=2025-11-05
Header: X-User-Subject: {employee-uuid}
```

### Get Weekly Summary
```bash
GET /time-logs/summary?period=weekly&date=2025-11-05
Header: X-User-Subject: {employee-uuid}
```

### Get Service Time Logs
```bash
GET /time-logs/service/SRV-001
Header: X-User-Subject: {user-uuid}
```

### Update Time Log
```bash
PUT /time-logs/{logId}
Header: X-User-Subject: {employee-uuid}
Body: {
  "hours": 5.0,
  "description": "Updated description"
}
```

### Delete Time Log
```bash
DELETE /time-logs/{logId}
Header: X-User-Subject: {employee-uuid}
```

---

## 🔑 Test Data (from DataSeeder)

### Employee IDs (use these in X-User-Subject header)
```
00000000-0000-0000-0000-000000000003  (Employee 1)
00000000-0000-0000-0000-000000000004  (Employee 2)
00000000-0000-0000-0000-000000000005  (Employee 3)
```

### Sample Service IDs
```
SRV-001, SRV-002, SRV-003, SRV-004, SRV-005
```

### Sample Project IDs
```
PRJ-001, PRJ-002, PRJ-003, PRJ-004, PRJ-005
```

### Work Types
```
Diagnostic, Repair, Maintenance, Installation, Inspection, Testing
```

---

## 🛠️ Configuration

### Environment Variables

```bash
# Database
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=techtorque_timelogs
export DB_USER=techtorque
export DB_PASS=techtorque123

# Profile
export SPRING_PROFILE=dev  # or 'prod'

# Security (false for local development)
export SECURITY_ENABLED=false
```

### application.properties

```properties
# Default configuration in src/main/resources/application.properties
spring.application.name=time-logging-service
server.port=8085

# Database (uses env vars)
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:techtorque_timelogs}
spring.datasource.username=${DB_USER:techtorque}
spring.datasource.password=${DB_PASS:techtorque123}

# JPA
spring.jpa.hibernate.ddl-auto=${DB_MODE:update}
spring.jpa.show-sql=true

# Security
app.security.enabled=${SECURITY_ENABLED:false}
```

---

## 🐳 Docker

### Build Image
```bash
cd Time_Logging_Service/time-logging-service
docker build -t time-logging-service:latest .
```

### Run Container
```bash
docker run -p 8085:8085 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  -e DB_NAME=techtorque_timelogs \
  -e DB_USER=techtorque \
  -e DB_PASS=techtorque123 \
  time-logging-service:latest
```

### Using docker-compose (from project root)
```bash
docker-compose up time-logging-service
```

---

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Test with cURL

```bash
# Create a time log
curl -X POST http://localhost:8085/time-logs \
  -H "Content-Type: application/json" \
  -H "X-User-Subject: 00000000-0000-0000-0000-000000000003" \
  -d '{
    "serviceId": "SRV-001",
    "hours": 3.5,
    "date": "2025-11-05",
    "description": "Oil change service",
    "workType": "Maintenance"
  }'

# Get all logs for employee
curl -X GET http://localhost:8085/time-logs \
  -H "X-User-Subject: 00000000-0000-0000-0000-000000000003"

# Get daily summary
curl -X GET "http://localhost:8085/time-logs/summary?period=daily&date=2025-11-05" \
  -H "X-User-Subject: 00000000-0000-0000-0000-000000000003"
```

---

## 🚨 Troubleshooting

### Issue: Database connection failed
**Solution:** 
- Check PostgreSQL is running: `pg_isready`
- Verify credentials in application.properties
- Ensure database exists: `psql -l`

### Issue: Port 8085 already in use
**Solution:**
- Find process: `lsof -i :8085`
- Kill process: `kill -9 <PID>`
- Or change port: `export SERVER_PORT=8086`

### Issue: No data seeded
**Solution:**
- Ensure `dev` profile is active
- Check logs for seeding messages
- Manually delete existing data: `DELETE FROM time_logs;`
- Restart service

### Issue: Authorization errors
**Solution:**
- Set security to false for local dev: `export SECURITY_ENABLED=false`
- Ensure correct employee ID in header
- Check role permissions in controller

---

## 📊 Monitoring

### Actuator Endpoints

```bash
# Health check
GET http://localhost:8085/actuator/health

# Application info
GET http://localhost:8085/actuator/info

# Metrics
GET http://localhost:8085/actuator/metrics
```

### Logs

```bash
# View logs in real-time
tail -f logs/time-logging-service.log

# Search for errors
grep ERROR logs/time-logging-service.log
```

---

## 🔗 Related Services

| Service | Port | Description |
|---------|------|-------------|
| **Auth Service** | 8081 | User authentication & management |
| **API Gateway** | 8080 | API routing & security |
| **Service Management** | 8084 | Work orders & projects |
| **Time Logging** | 8085 | **This service** |

---

## 📖 Additional Resources

- **Full Documentation:** `IMPLEMENTATION_SUMMARY.md`
- **API Design:** `/complete-api-design.md` (project root)
- **Audit Report:** `/PROJECT_AUDIT_REPORT_2025.md` (project root)
- **Swagger UI:** http://localhost:8085/swagger-ui/index.html
- **Spring Boot Docs:** https://docs.spring.io/spring-boot/

---

## 💡 Tips

1. **Use Swagger UI** for interactive API testing - it's easier than cURL
2. **Check logs** if something doesn't work - detailed error messages are logged
3. **Use proper UUIDs** from SharedConstants for consistent data
4. **Test with dev profile** first before production deployment
5. **Keep security disabled** locally to simplify testing

---

## ✅ Verification Checklist

- [ ] Service starts without errors
- [ ] Database connection successful
- [ ] Data seeded (30 time logs for 3 employees)
- [ ] Swagger UI accessible
- [ ] Can create time log via POST
- [ ] Can retrieve logs via GET
- [ ] Can update log via PUT
- [ ] Can delete log via DELETE
- [ ] Summary endpoint works
- [ ] Statistics endpoint works

---

**Need Help?** Check the full `IMPLEMENTATION_SUMMARY.md` for detailed documentation.
