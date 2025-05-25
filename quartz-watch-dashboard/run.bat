@echo off

REM Quartz Monitor Dashboard startup script for Windows

echo Starting Quartz Monitor Dashboard...

REM Check if Maven is installed
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo Maven is not installed. Please install Maven first.
    exit /b 1
)

REM Clean and package the application
echo Building the application...
call mvn clean package -DskipTests

REM Check if build was successful
if %errorlevel% neq 0 (
    echo Build failed. Please check the errors above.
    exit /b 1
)

REM Run the application
echo Starting the application...
java -jar target\quartz-watch-dashboard-0.0.1-SNAPSHOT.jar 