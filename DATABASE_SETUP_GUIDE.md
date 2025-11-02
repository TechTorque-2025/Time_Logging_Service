# 🗄️ Time Logging Service - Database Setup Guide

## 📋 Overview

This guide explains how the **Time Logging Service** connects to the database, following the same pattern as the **Authentication Service**.

---

## 🔧 How Database Connection Works

### 1. **Database Configuration** (`application.properties`)

Located at: `src/main/resources/application.properties`

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:techtorque_timelogs}
spring.datasource.username=${DB_USER:techtorque}
spring.datasource.password=${DB_PASS:techtorque123}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=${DB_MODE:update}
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

**How It Works:**
- Uses **environment variables** with fallback defaults
- `${DB_HOST:localhost}` means: use `DB_HOST` env var, or default to `localhost`
- `${DB_NAME:techtorque_timelogs}` means: use `DB_NAME` env var, or default to `techtorque_timelogs`

---

### 2. **Database Preflight Check** (Connection Verification)

Located at: `src/main/java/com/techtorque/time_logging_service/config/DatabasePreflightInitializer.java`

**Purpose:** Check database connection BEFORE starting the application

**How It Works:**
```java
@Override
public void initialize(ConfigurableApplicationContext applicationContext) {
    // Read database config from application.properties
    String jdbcUrl = env.getProperty("spring.datasource.url");
    String username = env.getProperty("spring.datasource.username");
    String password = env.getProperty("spring.datasource.password");
    
    // Try to connect
    try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
        logger.info("Database preflight check successful!");
    } catch (Exception e) {
        logger.error("DATABASE CONNECTION FAILED!");
        System.exit(1); // Stop app if database is not available
    }
}
```

**Registration:** Must be registered in `src/main/resources/META-INF/spring.factories`
```properties
org.springframework.context.ApplicationContextInitializer=\
com.techtorque.time_logging_service.config.DatabasePreflightInitializer
```

---

### 3. **Database Schema Management** (Hibernate DDL-Auto)

**Configured in `application.properties`:**
```properties
spring.jpa.hibernate.ddl-auto=${DB_MODE:update}
```

**Options:**
- **`validate`** - Only validate schema, no changes
- **`update`** - Update schema automatically (ADD new columns/tables)
- **`create`** - Drop and recreate schema on startup (⚠️ DELETES DATA)
- **`create-drop`** - Create on startup, drop on shutdown (⚠️ DELETES DATA)

**Default:** `update` (safe for development and production)

**How It Works:**
1. Spring Boot reads your `@Entity` classes (e.g., `TimeLog.java`)
2. Compares with actual database schema
3. If `ddl-auto=update`, it generates SQL to add missing tables/columns
4. Executes SQL automatically

---

## 🌱 Data Seeding (Like Auth Service)

### What is a DataSeeder?

A **DataSeeder** is a Spring component that runs on startup to populate the database with initial data.

**Example from Auth Service:**
```java
@Component
public class DataSeeder implements CommandLineRunner {
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data seeding...");
        
        // Create default roles
        seedRoles();
        
        // Create default users (only in dev profile)
        seedUsersByProfile();
        
        logger.info("Data seeding completed!");
    }
}
```

**Key Points:**
- Implements `CommandLineRunner` - runs AFTER app startup
- Checks if data exists before creating (idempotent)
- Profile-aware: seeds test data only in `dev` profile
- Uses repositories to insert data

---

## 🎯 For Time Logging Service: Do You Need a DataSeeder?

### ✅ **YES, if you want:**
1. Sample time log entries for testing
2. Pre-configured work types (Development, Testing, Meetings, etc.)
3. Test employee IDs with sample logs
4. Demo data for presentations

### ❌ **NO, if:**
1. Real employees will create their own logs
2. No reference data needed
3. Empty database is acceptable

---

## 📝 Time Logging Service DataSeeder Example

### Scenario: Seed Sample Time Logs for Testing

**File:** `src/main/java/com/techtorque/time_logging_service/config/DataSeeder.java`

```java
package com.techtorque.time_logging_service.config;

import com.techtorque.time_logging_service.entity.TimeLog;
import com.techtorque.time_logging_service.repository.TimeLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);
    
    @Autowired
    private TimeLogRepository timeLogRepository;
    
    @Autowired
    private Environment env;
    
    @Override
    public void run(String... args) throws Exception {
        // Only seed in dev profile
        boolean isDev = isDevProfile();
        
        if (!isDev) {
            logger.info("Not in dev profile. Skipping data seeding.");
            return;
        }
        
        // Check if data already exists
        if (timeLogRepository.count() > 0) {
            logger.info("Time logs already exist. Skipping seeding.");
            return;
        }
        
        logger.info("Starting time log data seeding...");
        seedSampleTimeLogs();
        logger.info("Data seeding completed!");
    }
    
    private boolean isDevProfile() {
        String[] activeProfiles = env.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("dev".equalsIgnoreCase(profile)) {
                return true;
            }
        }
        return false;
    }
    
    private void seedSampleTimeLogs() {
        // Sample employee IDs (would come from Auth service in real scenario)
        String[] employees = {"EMP001", "EMP002", "EMP003"};
        
        // Sample work types
        String[] workTypes = {"Development", "Testing", "Meetings", "Documentation", "Code Review"};
        
        // Sample projects and services
        String[] projects = {"PRJ001", "PRJ002", "PRJ003"};
        String[] services = {"SRV001", "SRV002", "SRV003"};
        
        // Create sample logs for the past 7 days
        LocalDate today = LocalDate.now();
        
        for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
            LocalDate workDate = today.minusDays(dayOffset);
            
            for (String empId : employees) {
                // Morning session (4 hours)
                TimeLog morningLog = TimeLog.builder()
                        .employeeId(empId)
                        .projectId(projects[dayOffset % projects.length])
                        .serviceId(services[dayOffset % services.length])
                        .hours(4.0)
                        .date(workDate)
                        .description("Morning work on " + workTypes[dayOffset % workTypes.length])
                        .workType(workTypes[dayOffset % workTypes.length])
                        .build();
                
                timeLogRepository.save(morningLog);
                
                // Afternoon session (4 hours)
                TimeLog afternoonLog = TimeLog.builder()
                        .employeeId(empId)
                        .projectId(projects[(dayOffset + 1) % projects.length])
                        .serviceId(services[(dayOffset + 1) % services.length])
                        .hours(4.0)
                        .date(workDate)
                        .description("Afternoon work on " + workTypes[(dayOffset + 1) % workTypes.length])
                        .workType(workTypes[(dayOffset + 1) % workTypes.length])
                        .build();
                
                timeLogRepository.save(afternoonLog);
            }
        }
        
        long count = timeLogRepository.count();
        logger.info("Created {} sample time log entries", count);
    }
}
```

---

## 🔄 Complete Database Setup Flow

### **Step 1: Application Starts**
```
Application.main()
  ↓
Spring Boot initializes
```

### **Step 2: Preflight Check (BEFORE Spring Context)**
```
DatabasePreflightInitializer.initialize()
  ↓
Read application.properties
  ↓
Try to connect to PostgreSQL
  ↓
SUCCESS → Continue | FAILURE → Exit(1)
```

### **Step 3: Spring Context Initialization**
```
Load @Configuration classes
  ↓
Create @Repository, @Service, @Controller beans
  ↓
Initialize JPA/Hibernate
```

### **Step 4: Hibernate Schema Management**
```
Read @Entity classes (TimeLog)
  ↓
Compare with database schema
  ↓
Generate SQL (if ddl-auto=update)
  ↓
Execute: CREATE TABLE IF NOT EXISTS time_logs...
```

### **Step 5: Data Seeding (AFTER Context Ready)**
```
DataSeeder.run()
  ↓
Check if dev profile active
  ↓
Check if data exists
  ↓
Insert sample data (if needed)
```

### **Step 6: Application Ready**
```
Tomcat starts on port 8085
  ↓
API endpoints available
  ↓
Ready to accept requests
```

---

## 🗃️ Database Schema (Auto-Created)

When you start the service, Hibernate creates this table automatically:

```sql
CREATE TABLE time_logs (
    id VARCHAR(255) PRIMARY KEY,           -- UUID
    employee_id VARCHAR(255) NOT NULL,     -- Employee identifier
    service_id VARCHAR(255),               -- Optional service reference
    project_id VARCHAR(255),               -- Optional project reference
    hours DOUBLE PRECISION NOT NULL,       -- Hours worked
    date DATE NOT NULL,                    -- Work date
    description TEXT,                      -- Description of work
    work_type VARCHAR(255),                -- Type of work
    created_at TIMESTAMP NOT NULL,         -- Auto-generated
    updated_at TIMESTAMP NOT NULL          -- Auto-updated
);

-- Indexes for performance
CREATE INDEX idx_employee_id ON time_logs(employee_id);
CREATE INDEX idx_service_id ON time_logs(service_id);
CREATE INDEX idx_project_id ON time_logs(project_id);
CREATE INDEX idx_date ON time_logs(date);
```

---

## 🛠️ Manual Database Setup (If Needed)

### Option 1: Let Spring Boot Handle It (Recommended)
```bash
# Just start the service - it creates everything automatically
.\mvnw.cmd spring-boot:run
```

### Option 2: Create Database Manually
```sql
-- Connect to PostgreSQL
psql -U postgres

-- Create database
CREATE DATABASE techtorque_timelogs;

-- Create user (if not exists)
CREATE USER techtorque WITH PASSWORD 'techtorque123';

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE techtorque_timelogs TO techtorque;

-- Switch to new database
\c techtorque_timelogs

-- Grant schema permissions
GRANT ALL ON SCHEMA public TO techtorque;
```

---

## 🔐 Environment Variables (Production)

For production deployment, set these environment variables:

```bash
# Database connection
export DB_HOST=production-db-server.com
export DB_PORT=5432
export DB_NAME=timelogs_prod
export DB_USER=secure_user
export DB_PASS=secure_password_here

# Database mode (use validate in production)
export DB_MODE=validate

# Spring profile
export SPRING_PROFILE=prod
```

---

## 🧪 Testing Database Connection

### Test 1: Health Check
```bash
curl http://localhost:8085/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

### Test 2: Create a Time Log
```bash
curl -X POST http://localhost:8085/api/time-logs \
  -H "Content-Type: application/json" \
  -H "X-Employee-Id: EMP001" \
  -d '{
    "serviceId": "SRV001",
    "projectId": "PRJ001",
    "hours": 8.0,
    "date": "2025-10-31",
    "description": "Working on database setup",
    "workType": "Development"
  }'
```

### Test 3: Verify in Database
```sql
-- Connect to database
psql -U techtorque -d techtorque_timelogs

-- Check tables
\dt

-- View time logs
SELECT * FROM time_logs;
```

---

## 🎯 Summary

### What You Have Now:
✅ **Database configuration** in `application.properties`  
✅ **Preflight check** ensures database is available before startup  
✅ **Automatic schema creation** via Hibernate DDL-Auto  
✅ **Entity mappings** (`@Entity TimeLog`) define table structure  
✅ **Repository layer** for database operations  

### What You Can Add (Optional):
⏳ **DataSeeder** for sample test data (like Auth service)  
⏳ **Database migrations** using Flyway or Liquibase (for version control)  
⏳ **Connection pooling** optimization (HikariCP already configured)  

### Comparison with Auth Service:

| Feature | Auth Service | Time Logging Service | Status |
|---------|--------------|----------------------|--------|
| Database Config | ✅ application.properties | ✅ application.properties | ✅ Same |
| Preflight Check | ✅ DatabasePreflightInitializer | ✅ DatabasePreflightInitializer | ✅ Same |
| Schema Management | ✅ Hibernate DDL-Auto | ✅ Hibernate DDL-Auto | ✅ Same |
| Data Seeder | ✅ Seeds users/roles | ⏳ Optional (time logs) | ⚠️ Optional |
| Connection Pool | ✅ HikariCP | ✅ HikariCP | ✅ Same |

---

## 📚 References

- **Auth Service DataSeeder:** `Authentication/auth-service/src/main/java/com/techtorque/auth_service/config/DataSeeder.java`
- **Your Preflight Check:** `time-logging-service/src/main/java/com/techtorque/time_logging_service/config/DatabasePreflightInitializer.java`
- **Your Entity:** `time-logging-service/src/main/java/com/techtorque/time_logging_service/entity/TimeLog.java`

---

**Your database setup is already complete and working!** ✅

The service connects automatically on startup, creates tables, and is ready to store time logs.

