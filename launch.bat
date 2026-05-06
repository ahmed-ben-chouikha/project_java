@echo off
REM Setup and run the JavaFX esports app from repository root

setlocal enabledelayedexpansion

echo ==================================================
echo   EsportDev Arena - Setup and Launch
echo ==================================================
echo.

echo [1/3] Checking Maven...
where mvn >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven not found in PATH.
    echo Install Maven and add it to PATH.
    exit /b 1
) else (
    echo OK: Maven found
)

echo.
echo [2/3] Building project...
set "PROJECT_ROOT=%~dp0"
pushd "%PROJECT_ROOT%"
mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed
    popd
    exit /b 1
) else (
    echo OK: Build successful
)

echo.
echo [3/3] Launching application...
mvn javafx:run

set "EXITCODE=%ERRORLEVEL%"
popd

exit /b %EXITCODE%

