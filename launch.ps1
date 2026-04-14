# EsportDev Arena Launcher
# This script sets up Maven PATH and runs the app

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "   EsportDev Arena - JavaFX Esports Dashboard" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

# Use JDK (not JRE) and Maven extracted folder
$env:JAVA_HOME = "C:\Program Files\Java\jdk-22"
$env:PATH = "$env:JAVA_HOME\bin;C:\Apache\apache-maven-3.9.6\bin;" + $env:PATH

Write-Host "[1/3] Checking Maven installation..." -ForegroundColor Yellow
$mavenCheck = & mvn --version 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Maven found" -ForegroundColor Green
    Write-Host $mavenCheck[0] -ForegroundColor Gray
} else {
    Write-Host "[ERROR] Maven not found at C:\Apache\apache-maven-3.9.6\bin" -ForegroundColor Red
    Write-Host "Please ensure Maven is installed correctly." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[2/3] Navigating to project..." -ForegroundColor Yellow
cd "C:\Users\ahmed\Downloads\JAVAFX\Connexion3A36"
Write-Host "[OK] Project directory ready" -ForegroundColor Green

Write-Host ""
Write-Host "[3/3] Building and launching application..." -ForegroundColor Yellow
Write-Host "This may take a few moments on first run..." -ForegroundColor Gray
Write-Host ""

# Run Maven
& mvn clean javafx:run

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "[ERROR] Launch failed. Check the output above for errors." -ForegroundColor Red
    exit 1
} else {
    Write-Host ""
    Write-Host "[OK] Application completed successfully" -ForegroundColor Green
}

