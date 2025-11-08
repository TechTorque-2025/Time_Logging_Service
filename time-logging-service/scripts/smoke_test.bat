@echo off
REM Quick smoke test for Time Logging Service (Windows)
REM Make sure the service is running on localhost:8080 before running this script

SET CURL=curl

ECHO Creating a time log...
%CURL% -s -X POST http://localhost:8080/time-logs -H "Content-Type: application/json" -H "X-User-Subject: employee-1" -H "X-User-Roles: EMPLOYEE" -d "{\"serviceId\":\"svc-1\",\"hours\":2.5,\"date\":\"2025-10-30\",\"description\":\"Work\"}" -o response.json -w "\nHTTP_STATUS:%{http_code}\n"
FOR /F "tokens=*" %%i IN (response.json) DO SET RESP=%%i
ECHO Response: %RESP%

ECHO Listing logs for employee...
%CURL% -s "http://localhost:8080/time-logs?fromDate=2025-10-01&toDate=2025-10-31" -H "X-User-Subject: employee-1" -H "X-User-Roles: EMPLOYEE"

ECHO Getting summary (weekly)...
%CURL% -s "http://localhost:8080/time-logs/summary?period=weekly&date=2025-10-30" -H "X-User-Subject: employee-1" -H "X-User-Roles: EMPLOYEE"

ECHO Smoke test finished.
PAUSE

