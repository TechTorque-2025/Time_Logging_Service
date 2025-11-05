@echo off
echo ================================
echo Testing Time Logging Service Build
echo ================================
cd /d "%~dp0"

echo.
echo [1/3] Cleaning project...
call mvnw.cmd clean -q

echo.
echo [2/3] Compiling...
call mvnw.cmd compile

echo.
echo [3/3] Checking for errors...
if exist "target\classes\com\techtorque\time_logging_service\service\TimeLogService.class" (
    echo ✅ SUCCESS: TimeLogService compiled successfully
) else (
    echo ❌ FAILED: TimeLogService did not compile
    exit /b 1
)

if exist "target\classes\com\techtorque\time_logging_service\entity\TimeLog.class" (
    echo ✅ SUCCESS: TimeLog entity compiled successfully
) else (
    echo ❌ FAILED: TimeLog entity did not compile
    exit /b 1
)

echo.
echo ================================
echo ✅ BUILD SUCCESSFUL - All files compiled!
echo ================================
echo.
echo You can now run the service from your IDE!
echo.
pause

