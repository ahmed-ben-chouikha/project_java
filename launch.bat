@echo off
REM Setup Maven and run the JavaFX esports app

setlocal enabledelayedexpansion

echo ==================================================
echo   EsportDev Arena - Setup and Launch
echo ==================================================
echo.

REM Add Maven to PATH
set "MAVEN_HOME=C:\Apache\maven"
set "PATH=%MAVEN_HOME%\bin;%PATH%"

echo [1/3] Checking Maven...
mvn --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven not found at %MAVEN_HOME%
    echo Please ensure Maven is installed at C:\Apache\maven
    exit /b 1
) else (
    echo OK: Maven found
)

echo.
echo [2/3] Building project...
cd C:\Users\ahmed\Downloads\JAVAFX\Connexion3A36
mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed
    exit /b 1
) else (
    echo OK: Build successful
)

echo.
echo [3/3] Launching application...
mvn javafx:run

exit /b 0

