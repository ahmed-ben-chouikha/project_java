# RankUp E-Sports Launcher
# Script pour lancer l'application avec Java 17 et Maven

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "   RankUp E-Sports Platform" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

$javaExe = $null
function Resolve-JavaHome {
    $candidates = @()

    if ($env:JAVA_HOME) {
        $candidates += $env:JAVA_HOME
    }

    $searchRoots = @(
        'C:\Program Files\Java',
        'C:\Program Files\Eclipse Adoptium',
        'C:\Program Files\Microsoft\jdk',
        'C:\Program Files\Zulu',
        'C:\Program Files\Amazon Corretto',
        'C:\Java'
    )

    foreach ($root in $searchRoots) {
        if (Test-Path $root) {
            Get-ChildItem $root -Directory | ForEach-Object { $candidates += $_.FullName }
        }
    }

    foreach ($candidate in $candidates | Select-Object -Unique) {
        $javaPath = Join-Path $candidate 'bin\java.exe'
        if (Test-Path $javaPath) {
            return $candidate
        }
    }

    return $null
}

$detectedJavaHome = Resolve-JavaHome
if ($detectedJavaHome) {
    $env:JAVA_HOME = $detectedJavaHome
    if ($env:Path -notlike "$detectedJavaHome\bin*") {
        $env:Path = "$detectedJavaHome\bin;" + $env:Path
    }
    $javaExe = Join-Path $detectedJavaHome 'bin\java.exe'
}

if (-not $javaExe) {
    Write-Host "ERREUR: Java introuvable. Installez un JDK 17+ ou definissez JAVA_HOME." -ForegroundColor Red
    exit 1
}

Write-Host "[1/2] Verification de Java..." -ForegroundColor Yellow
& $javaExe -version 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERREUR: Impossible de verifier Java." -ForegroundColor Red
    exit 1
}

$javaVersionText = (& $javaExe -version 2>&1 | Select-Object -First 1)
if ($javaVersionText -match 'version "([0-9]+)(?:\.([0-9]+))?') {
    $major = [int]$Matches[1]
    if ($major -eq 1 -or $major -lt 17) {
        Write-Host "ERREUR: Java 17+ requis. La version detectee est: $javaVersionText" -ForegroundColor Red
        Write-Host "Installez un JDK 17 ou 21, puis relancez le script." -ForegroundColor Yellow
        exit 1
    }
}

Write-Host "[OK] Java pret" -ForegroundColor Green

Write-Host ""
Write-Host "[2/2] Lancement de l'application JavaFX..." -ForegroundColor Yellow
Write-Host "Le projet $((Get-Location).Path)" -ForegroundColor Gray
Write-Host ""

& "$PSScriptRoot\mvn.ps1" javafx:run

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERREUR: Le lancement Maven a echoue." -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host ""
Write-Host "Application fermee." -ForegroundColor Yellow

