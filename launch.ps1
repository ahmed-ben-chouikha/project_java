# RankUp E-Sports Launcher
# Script pour lancer l'application avec Java 17 et Maven

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "   RankUp E-Sports Platform" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

$JAVA_HOME = "C:\Java\jdk-17.0.12"
$M2_HOME = "C:\Program Files\apache-maven-3.9.9"
$classworlds = (Get-ChildItem "$M2_HOME\boot\plexus-classworlds-*.jar" | Select-Object -First 1).FullName

if (-not $classworlds) {
    Write-Host "ERREUR: Impossible de trouver plexus-classworlds-*.jar" -ForegroundColor Red
    exit 1
}

Write-Host "[1/2] Verification de Java 17..." -ForegroundColor Yellow
& "$JAVA_HOME\bin\java" -version
Write-Host "[OK] Java 17 pret" -ForegroundColor Green

Write-Host ""
Write-Host "[2/2] Lancement de l'application JavaFX..." -ForegroundColor Yellow
Write-Host "Le projet $((Get-Location).Path)" -ForegroundColor Gray
Write-Host ""

& "$JAVA_HOME\bin\java" `
  -classpath "$classworlds" `
  "-Dclassworlds.conf=$M2_HOME\bin\m2.conf" `
  "-Dmaven.home=$M2_HOME" `
  "-Dmaven.multiModuleProjectDirectory=$(Get-Location)" `
  org.codehaus.plexus.classworlds.launcher.Launcher `
  javafx:run

Write-Host ""
Write-Host "Application fermee." -ForegroundColor Yellow

