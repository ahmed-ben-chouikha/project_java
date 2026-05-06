# Script de lancement final pour RankUp E-Sports
# Configuration automatique de Java et Maven, puis lancement de l'app

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  RankUp E-Sports Platform" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Configuration Java
$JAVA_HOME = "C:\Users\F\.jdks\jbr-17.0.14"
$JAVA = "$JAVA_HOME\bin\java.exe"

# Configuration Maven
$M2_HOME = "C:\apache-maven"
$MVN = "$M2_HOME\bin\mvn.cmd"

# Vérifier Java
Write-Host "[1/4] Vérification de Java 17..." -ForegroundColor Yellow
if (-not (Test-Path $JAVA)) {
    Write-Host "ERREUR: Java introuvable à $JAVA_HOME" -ForegroundColor Red
    exit 1
}

$env:JAVA_HOME = $JAVA_HOME
& $JAVA -version 2>&1 | Select-Object -First 1
Write-Host "[OK] Java trouvé" -ForegroundColor Green
Write-Host ""

# Vérifier Maven
Write-Host "[2/4] Vérification de Maven..." -ForegroundColor Yellow
if (-not (Test-Path $MVN)) {
    Write-Host "ERREUR: Maven introuvable" -ForegroundColor Red
    Write-Host "Exécutez d'abord: .\install-maven.ps1" -ForegroundColor Yellow
    exit 1
}

$env:M2_HOME = $M2_HOME
& $MVN -version 2>&1 | Select-Object -First 1
Write-Host "[OK] Maven trouvé" -ForegroundColor Green
Write-Host ""

# Base de données
Write-Host "[3/4] Vérification de la base de données..." -ForegroundColor Yellow
Write-Host "Assurez-vous que MySQL est en cours d'exécution avec:" -ForegroundColor Gray
Write-Host "  - Base: esportdevvvvvv" -ForegroundColor Gray
Write-Host "  - Host: localhost:3306" -ForegroundColor Gray
Write-Host "  - User: root" -ForegroundColor Gray
Write-Host ""

# Lancer l'application
Write-Host "[4/4] Lancement de l'application JavaFX..." -ForegroundColor Yellow
Write-Host "Cela peut prendre quelques minutes (compilation en cours)..." -ForegroundColor Gray
Write-Host ""

& $MVN clean javafx:run

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "[OK] Application fermée normalement" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "[ERREUR] L'application s'est fermée avec le code: $LASTEXITCODE" -ForegroundColor Red
    exit $LASTEXITCODE
}

