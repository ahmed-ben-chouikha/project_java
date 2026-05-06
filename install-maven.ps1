# Script pour télécharger et configurer Maven automatiquement
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  Maven Auto-Installation" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Configuration
$MAVEN_VERSION = "3.9.9"
$MAVEN_URL = "https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip"
$INSTALL_DIR = "C:\apache-maven"
$ZIP_PATH = "$env:TEMP\maven.zip"

# Vérifier si Maven est déjà installé
Write-Host "[1/3] Vérification de Maven..." -ForegroundColor Yellow
if (Test-Path "$INSTALL_DIR\bin\mvn.cmd") {
    Write-Host "[OK] Maven trouvé à $INSTALL_DIR" -ForegroundColor Green
    $env:M2_HOME = $INSTALL_DIR
    $env:Path = "$INSTALL_DIR\bin;" + $env:Path
    & "$INSTALL_DIR\bin\mvn.cmd" -version
    exit 0
}

Write-Host "[TÉLÉCHARGEMENT] Maven $MAVEN_VERSION depuis Apache..." -ForegroundColor Yellow
try {
    [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor [System.Net.SecurityProtocolType]::Tls12

    # Créer le répertoire d'installation
    if (-not (Test-Path $INSTALL_DIR)) {
        New-Item -ItemType Directory -Path $INSTALL_DIR -Force | Out-Null
    }

    # Télécharger Maven
    $ProgressPreference = 'SilentlyContinue'
    Invoke-WebRequest -Uri $MAVEN_URL -OutFile $ZIP_PATH -TimeoutSec 300
    Write-Host "[OK] Téléchargement réussi" -ForegroundColor Green

    Write-Host "[2/3] Extraction de Maven..." -ForegroundColor Yellow
    Expand-Archive -Path $ZIP_PATH -DestinationPath $INSTALL_DIR -Force

    # Déplacer les fichiers au bon endroit
    $extracted = Get-ChildItem $INSTALL_DIR -Directory | Where-Object { $_.Name -match "apache-maven" }
    if ($extracted) {
        Move-Item "$($extracted.FullName)\*" $INSTALL_DIR -Force
        Remove-Item $extracted.FullName -Force
    }

    Write-Host "[OK] Extraction réussie" -ForegroundColor Green

    # Nettoyer
    Remove-Item $ZIP_PATH -ErrorAction SilentlyContinue

    Write-Host "[3/3] Configuration des variables d'environnement..." -ForegroundColor Yellow
    $env:M2_HOME = $INSTALL_DIR
    $env:Path = "$INSTALL_DIR\bin;" + $env:Path

    Write-Host "[OK] Maven configuré avec succès !" -ForegroundColor Green
    Write-Host ""

    # Vérifier l'installation
    & "$INSTALL_DIR\bin\mvn.cmd" -version

    Write-Host ""
    Write-Host "=== Configuration permanente (optionnel) ===" -ForegroundColor Cyan
    Write-Host "Pour rendre cette configuration permanente, ajoutez:" -ForegroundColor Gray
    Write-Host "  Variable: M2_HOME = $INSTALL_DIR" -ForegroundColor Gray
    Write-Host "  Aux variables d'environnement Windows" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Ensuite, relancez PowerShell et tapez:" -ForegroundColor Cyan
    Write-Host "  cd '$PSScriptRoot'" -ForegroundColor Gray
    Write-Host "  .\launch.ps1" -ForegroundColor Gray

} catch {
    Write-Host "[ERREUR] Le téléchargement a échoué" -ForegroundColor Red
    Write-Host "Message: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Alternative: Installez manuellement Maven depuis:" -ForegroundColor Yellow
    Write-Host "  https://maven.apache.org/download.cgi" -ForegroundColor Cyan
    exit 1
}

