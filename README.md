# ⏱️ Time Logging Service

This microservice is responsible for tracking all work hours logged by employees against specific services and projects.

**Assigned Team:** Dhanuja, Mahesh

### 🎯 Key Responsibilities

-   Allow employees to create, read, update, and delete their time log entries.
-   Associate each log with a specific `serviceId` or `projectId`.
-   Provide summary endpoints for employee productivity analysis.
-   (Planned) Publish events when time is logged to trigger real-time progress updates in other services.

### ⚙️ Tech Stack

-   **Framework:** Java / Spring Boot
-   **Database:** PostgreSQL
-   **Security:** Spring Security (consumes JWTs)

### ℹ️ API Information

-   **Local Port:** `8085`
-   **Swagger UI:** [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)

### 🚀 Running Locally

This service is designed to be run as part of the main `docker-compose` setup from the project's root directory.

```bash
# From the root of the TechTorque-2025 project
docker-compose up --build time-logging-service