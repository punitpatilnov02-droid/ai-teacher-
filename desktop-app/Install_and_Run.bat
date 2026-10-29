@echo off
setlocal enabledelayedexpansion
title AI TEACH - Automated Desktop Launcher

echo ========================================================
echo    AI TEACH - Your Interactive 3D Tutor
echo    1-Click Automated Windows Setup & Desktop Launcher
echo ========================================================
echo.

:: 1. Check for Node.js
echo [1/3] Checking for Node.js installation...
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Node.js is not installed on this computer!
    echo Node.js is required to run the native Electron desktop application.
    echo.
    echo Opening official Node.js download page in your browser...
    start https://nodejs.org/en/download/
    echo Please run the installer, then double-click this Install_and_Run.bat again.
    echo.
    pause
    exit /b 1
)

for /f "tokens=*" %%v in ('node -v') do set NODE_VER=%%v
echo [OK] Node.js found: %NODE_VER%
echo.

:: 2. Check dependencies (npm install if node_modules missing)
echo [2/3] Checking Electron desktop dependencies...
if not exist "node_modules\" (
    echo First-time setup detected. Installing Electron desktop engine...
    echo Please wait while npm packages are installed automatically...
    echo.
    call npm install
    if %errorlevel% neq 0 (
        echo.
        echo [ERROR] Failed to install npm dependencies.
        echo Please check your internet connection or run Command Prompt as Administrator.
        pause
        exit /b 1
    )
    echo.
    echo [OK] Electron engine installed successfully!
) else (
    echo [OK] Dependencies already installed.
)
echo.

:: 3. Launch the native desktop application
echo [3/3] Launching AI TEACH Native Desktop Window...
echo --------------------------------------------------------
echo The desktop application window will open automatically.
echo You can minimize this command prompt.
echo --------------------------------------------------------
call npm start

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Encountered an error while starting the app.
    pause
)
