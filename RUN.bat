@echo off
REM EsportDev Arena Launcher - Easy Way to Run the App
REM This batch file sets up the environment and launches the app

setlocal enabledelayedexpansion

echo.
echo =====================================================================
echo   EsportDev Arena - JavaFX Esports Dashboard Launcher
echo =====================================================================
echo.

REM Set Java Home to JDK 22
set "JAVA_HOME=C:\Program Files\Java\jdk-22"

REM Add Maven to PATH
set "PATH=C:\Apache\apache-maven-3.9.6\bin;%PATH%"

REM Navigate to project
cd /d C:\Users\ahmed\Downloads\JAVAFX\Connexion3A36

echo [INFO] Launching application...
echo [INFO] Setting JAVA_HOME to %JAVA_HOME%
echo [INFO] First run may take a few moments while dependencies download...
echo.

REM Run Maven with full path using cmd
cmd /c "C:\Apache\apache-maven-3.9.6\bin\mvn.cmd clean javafx:run"

echo.
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Application ran successfully!
) else (
    echo [ERROR] Application launch failed. Check the output above for details.
    pause
)

