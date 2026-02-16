@echo off
set "MAVEN_HOME=C:\Program Files\apache-maven-3.9.12"
set "PATH=%MAVEN_HOME%\bin;%PATH%"

echo Checking environment...
call mvn -version
if %ERRORLEVEL% NEQ 0 (
    echo Maven setup failed. Please check the path "C:\Program Files\apache-maven-3.9.12".
    pause
    exit /b 1
)

echo Starting Employee Tracking Application...
call mvn spring-boot:run
pause
