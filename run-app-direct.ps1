# Script pour compiler et lancer RankUp sans Maven
# Compile directement avec javac et lance l'app avec JavaFX

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  RankUp E-Sports - Direct Launcher" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Configuration Java
$JAVA_HOME = "C:\Users\F\.jdks\jbr-17.0.14"
$env:JAVA_HOME = $JAVA_HOME
$JAVAC = "$JAVA_HOME\bin\javac.exe"
$JAVA = "$JAVA_HOME\bin\java.exe"

# Vérifier Java
Write-Host "[1/4] Vérification de Java..." -ForegroundColor Yellow
if (-not (Test-Path $JAVA)) {
    Write-Host "ERREUR: Java non trouvé à $JAVA_HOME" -ForegroundColor Red
    exit 1
}

& $JAVA -version 2>&1
Write-Host "[OK] Java trouvé" -ForegroundColor Green
Write-Host ""

# Définir les répertoires
$PROJECT_DIR = Get-Location
$SRC_DIR = "$PROJECT_DIR\src\main\java"
$BUILD_DIR = "$PROJECT_DIR\build"
$LIB_DIR = "$PROJECT_DIR\target\dependency"

# Créer répertoire de build
Write-Host "[2/4] Préparation des répertoires..." -ForegroundColor Yellow
if (-not (Test-Path $BUILD_DIR)) {
    New-Item -ItemType Directory -Path $BUILD_DIR | Out-Null
}

Write-Host "[OK] Répertoires prêts" -ForegroundColor Green
Write-Host ""

# Message informatif
Write-Host "[3/4] Méthode alternative recommandée..." -ForegroundColor Yellow
Write-Host "Pour lancer l'application, installez Maven :" -ForegroundColor White
Write-Host "  1. Téléchargez Maven 3.9+ depuis https://maven.apache.org/download.cgi" -ForegroundColor Gray
Write-Host "  2. Décompressez-le (ex: C:\apache-maven-3.9.9)" -ForegroundColor Gray
Write-Host "  3. Ajoutez M2_HOME dans les variables d'environnement Windows" -ForegroundColor Gray
Write-Host "  4. Relancez ce script ou utilisez: mvn javafx:run" -ForegroundColor Gray
Write-Host ""

# Ou utiliser le jar pré-compilé s'il existe
Write-Host "[4/4] Vérification du JAR compilé..." -ForegroundColor Yellow
$JAR_PATH = "$PROJECT_DIR\target\Connexion3A36-1.0-SNAPSHOT.jar"

if (Test-Path $JAR_PATH) {
    Write-Host "[OK] JAR trouvé, lancement..." -ForegroundColor Green
    Write-Host ""
    & $JAVA -jar $JAR_PATH
} else {
    Write-Host "[ATTENTE] JAR non trouvé" -ForegroundColor Yellow
    Write-Host "Vous pouvez créer le JAR avec:" -ForegroundColor White
    Write-Host "  mvn clean package" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Pour l'instant, utilisez la méthode Maven directe :" -ForegroundColor Cyan
    Write-Host "  1. Installez Maven" -ForegroundColor Gray
    Write-Host "  2. Puis lancez : mvn javafx:run" -ForegroundColor Gray
}

