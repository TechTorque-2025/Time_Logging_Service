# Time Logging Service

This service allows employees to create, read, update, and delete time log entries, associate logs with a serviceId or projectId, and retrieve summary reports.

Important files
- `src/main/java/.../controller/TimeLoggingController.java` - REST endpoints
- `src/main/java/.../service/TimeLoggingServiceImpl.java` - business logic
- `src/main/java/.../events/NoopTimeLogEventPublisher.java` - placeholder for event publishing
- `src/main/resources/application.properties` - runtime config

Running locally

Build:
```
cd /d D:\TechTorque\Time_Logging_Service\time-logging-service
.\mvnw.cmd clean package
```

Run (dev profile - will use configured DB in application.properties):
```
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

If you want a quick smoke test after the app is running on localhost:8080, use the provided Windows batch script in `scripts\smoke_test.bat`.

Headers expected (usually added by API Gateway):
- `X-User-Subject`: the employee id (principal)
- `X-User-Roles`: comma separated roles (e.g. `EMPLOYEE,ADMIN`)

Main endpoints

1) Create a time log
- POST /time-logs
- Roles: EMPLOYEE
- Request body (JSON):
  {
    "serviceId": "svc-1",
    "projectId": "proj-1", // optional
    "hours": 2.5,
    "date": "2025-10-30",
    "description": "Worked on feature X",
    "workType": "development"
  }
- Response: 201 Created, Location header `/time-logs/{id}` and response body with the saved record.

2) Get employee logs for a period
- GET /time-logs?fromDate=YYYY-MM-DD&toDate=YYYY-MM-DD
- Roles: EMPLOYEE
- Returns: list of logs for the authenticated employee.

3) Get a specific log
- GET /time-logs/{logId}
- Roles: EMPLOYEE, ADMIN
- Ownership enforced: employees can access their own; admins can access any.

4) Update a log
- PUT /time-logs/{logId}
- Roles: EMPLOYEE
- Employees can update their own logs. Provide a `TimeLogUpdateRequest` body with fields to update.

5) Delete a log
- DELETE /time-logs/{logId}
- Roles: EMPLOYEE

6) Summary
- GET /time-logs/summary?period=daily|weekly&date=YYYY-MM-DD
- Roles: EMPLOYEE
- Returns: `TimeLogSummaryResponse` with totalHours, count, byService, byProject.

Notes and caveats
- Basic server-side validations are in place: hours must be > 0 and <= 24, date cannot be in the future, and either serviceId or projectId must be provided.
- Events: the service calls a `TimeLogEventPublisher.publishTimeLogged(...)` after saving — currently a no-op implementation (`NoopTimeLogEventPublisher`) logs the event for visibility.
- Tests: per team direction, tests are postponed for the submission; please avoid running tests against a production/shared DB. We recommend setting up an H2/test profile for CI.

Contact
- If you need the event publisher wired to Kafka/RabbitMQ, I can add that integration next.

