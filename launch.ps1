# EsportDev Arena Launcher
# This script runs the app from the repository root.

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "   EsportDev Arena - JavaFX Esports Dashboard" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

$projectRoot = $PSScriptRoot
if (-not $projectRoot) {
    $projectRoot = (Get-Location).Path
}

Write-Host "[1/3] Checking Maven installation..." -ForegroundColor Yellow
$mavenCmd = Get-Command mvn,mvn.cmd -ErrorAction SilentlyContinue | Select-Object -First 1
if (-not $mavenCmd) {
    Write-Host "ERROR: Maven is not available in PATH." -ForegroundColor Red
    Write-Host "Install Maven or add it to PATH, then re-run this script." -ForegroundColor Red
    exit 1
}

& $mavenCmd.Source --version | Select-Object -First 1

Write-Host ""
Write-Host "[2/3] Navigating to project..." -ForegroundColor Yellow
Set-Location $projectRoot
Write-Host "OK: Project directory ready -> $projectRoot" -ForegroundColor Green

Write-Host ""
Write-Host "[3/3] Building and launching application..." -ForegroundColor Yellow
Write-Host "This may take a few moments on first run..." -ForegroundColor Gray
Write-Host ""

& $mavenCmd.Source clean javafx:run
if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERROR: Launch failed. Check output above." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "OK: Application completed successfully" -ForegroundColor Green

