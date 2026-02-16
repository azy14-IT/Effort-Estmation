@echo off
set "APP_DIR=C:\WorkTrackPro"
echo [INFO] Stopping Service...
sc stop WorkTrackPro >nul 2>&1
timeout /t 2 /nobreak >nul

echo [INFO] Uninstalling Service...
sc delete WorkTrackPro >nul 2>&1
if %errorlevel% neq 0 (
    echo Service "WorkTrackPro" not found or access denied.
) else (
    echo Service removed.
)

echo [INFO] Cleaning up application files...
if exist "%APP_DIR%" (
   del /F /Q "%APP_DIR%\*.jar"
   del /F /Q "%APP_DIR%\*.exe"
   del /F /Q "%APP_DIR%\*.xml"
   echo Files removed.
) else (
   echo Directory "%APP_DIR%" not found.
)

echo.
echo [SUCCESS] Uninstalled successfully.
pause
