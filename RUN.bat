@echo off
REM EsportDev Arena Launcher - Easy Way to Run the App
REM This batch file runs the app from repository root

setlocal enabledelayedexpansion

echo.
echo =====================================================================
echo   EsportDev Arena - JavaFX Esports Dashboard Launcher
echo =====================================================================
echo.

REM Navigate to project
set "PROJECT_ROOT=%~dp0"
pushd "%PROJECT_ROOT%"

where mvn >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven not found in PATH.
    echo [ERROR] Install Maven and add it to PATH.
    popd
    exit /b 1
)

echo [INFO] Launching application...
echo [INFO] First run may take a few moments while dependencies download...
echo.

mvn clean javafx:run
set "EXITCODE=%ERRORLEVEL%"

echo.
if %EXITCODE% EQU 0 (
    echo [SUCCESS] Application ran successfully!
) else (
    echo [ERROR] Application launch failed. Check the output above for details.
    pause
)

popd
exit /b %EXITCODE%

