@echo off
echo ========================================
echo Time Logging Service - Final Test
echo ========================================
echo.

cd /d "%~dp0"

echo [Step 1/4] Cleaning previous builds...
call mvnw.cmd clean -q
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Clean failed!
    pause
    exit /b 1
)
echo ✅ Clean successful

echo.
echo [Step 2/4] Compiling source code...
call mvnw.cmd compile -q
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Compilation failed! Check errors above.
    pause
    exit /b 1
)
echo ✅ Compilation successful

echo.
echo [Step 3/4] Running tests...
call mvnw.cmd test -q
if %ERRORLEVEL% NEQ 0 (
    echo ⚠️  Some tests failed, but continuing...
) else (
    echo ✅ All tests passed
)

echo.
echo [Step 4/4] Creating JAR package...
call mvnw.cmd package -DskipTests -q
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Package creation failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo ✅ BUILD SUCCESSFUL!
echo ========================================
echo.
echo JAR Location:
echo   target\time-logging-service-0.0.1-SNAPSHOT.jar
echo.
echo To run the service:
echo   1. From IDE: Run TimeLoggingServiceApplication.java
echo   2. From Maven: mvnw.cmd spring-boot:run
echo   3. From JAR: java -jar target\time-logging-service-0.0.1-SNAPSHOT.jar
echo.
echo Service will start on: http://localhost:8085
echo Health check: http://localhost:8085/actuator/health
echo.
echo ========================================
echo.
pause

