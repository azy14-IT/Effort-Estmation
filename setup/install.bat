@echo off
echo ===================================================
echo   WorkTrack Pro - Easy Installer
echo ===================================================
echo.

:: 1. Check for Admin Privileges
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo [ERROR] This script requires Administrator privileges.
    echo Please right-click and select "Run as Administrator".
    pause
    exit /b
)

:: 2. Set Installation Directory
set "INSTALL_DIR=C:\WorkTrackPro"
echo [INFO] Installing to: %INSTALL_DIR%

:: 3. Create Directory
if not exist "%INSTALL_DIR%" (
    mkdir "%INSTALL_DIR%"
    echo [INFO] Created installation folder.
)

:: 4. Copy Application JAR
echo [INFO] Copying application files...
copy /Y "..\target\employee-tracking-0.0.1-SNAPSHOT.jar" "%INSTALL_DIR%\employee-tracking.jar" >nul
if %errorLevel% neq 0 (
    echo [ERROR] Could not find the built JAR file. 
    echo Please make sure you have run 'mvn clean package' first.
    pause
    exit /b
)

:: 5. Setup Windows Service (WinSW)
:: Ideally, you should bundle WinSW.exe in this setup folder as 'winsw.exe'
if exist "winsw.exe" (
    copy /Y "winsw.exe" "%INSTALL_DIR%\WorkTrackPro-Service.exe" >nul
    copy /Y "service-config.xml" "%INSTALL_DIR%\WorkTrackPro-Service.xml" >nul
    
    echo [INFO] Installing Windows Service...
    cd /d "%INSTALL_DIR%"
    WorkTrackPro-Service.exe install
    WorkTrackPro-Service.exe start
    
    echo [SUCCESS] Service installed and started!
) else (
    echo [WARNING] 'winsw.exe' not found in setup folder. 
    echo Please download WinSW.exe, rename it to winsw.exe and place it here to auto-install the service.
    echo Manual service setup required.
)

echo.
echo ===================================================
echo   Installation Complete!
echo ===================================================
echo.
echo Open your browser and go to: http://localhost:8080
echo.
pause
