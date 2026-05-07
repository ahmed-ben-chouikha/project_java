@echo off
REM Database Setup Script for RankUp E-Sports Platform
REM This script sets up the MySQL database automatically
REM Author: Development Team
REM Date: April 30, 2026

echo.
echo ========================================
echo RankUp E-Sports Database Setup
echo ========================================
echo.

REM Check if MySQL is available
where mysql >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: MySQL command not found!
    echo.
    echo Please ensure:
    echo 1. MySQL is installed
    echo 2. MySQL bin directory is in PATH
    echo.
    echo To add MySQL to PATH:
    echo - Open System Environment Variables
    echo - Add MySQL bin directory to PATH
    echo - Default: C:\Program Files\MySQL\MySQL Server 8.0\bin
    echo.
    pause
    exit /b 1
)

echo [OK] MySQL found
echo.

REM Test MySQL connection
echo Testing MySQL connection...
mysql -h localhost -u root -e "SELECT 1;" >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Cannot connect to MySQL!
    echo.
    echo Please ensure:
    echo 1. MySQL server is running
    echo 2. Username is correct (default: root)
    echo 3. Password is correct (press Enter if none)
    echo.
    echo To start MySQL in XAMPP:
    echo - Open XAMPP Control Panel
    echo - Click "Start" for MySQL
    echo.
    pause
    exit /b 1
)

echo [OK] MySQL connection successful
echo.

REM Get the directory where this script is located
set SCRIPT_DIR=%~dp0

REM Define the SQL file path
set SQL_FILE=%SCRIPT_DIR%DATABASE_COMPLETE_SETUP.sql

echo Creating database and tables...
echo SQL File: %SQL_FILE%
echo.

REM Check if SQL file exists
if not exist "%SQL_FILE%" (
    echo ERROR: SQL file not found!
    echo Expected: %SQL_FILE%
    echo.
    pause
    exit /b 1
)

REM Execute the SQL script
mysql -h localhost -u root < "%SQL_FILE%"

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Database setup failed!
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo [SUCCESS] Database setup completed!
echo ========================================
echo.
echo The following have been created:
echo - Database: esportdevvvvvv
echo - Tables: user, team, tournament, etc.
echo - Sample data: 6 test users, 3 sample teams
echo.
echo Test Credentials:
echo - Email: player1@rankup.gg
echo - Password: password123
echo.
echo Next Steps:
echo 1. Start the RankUp application
echo 2. Login with test credentials
echo 3. Verify everything works
echo.
pause

