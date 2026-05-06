# Script PowerShell pour compiler et lancer avec Java 17

$repoRoot = Split-Path -Parent $PSScriptRoot
Set-Location $repoRoot

Write-Host "Verification de Java 17..."
& "$PSScriptRoot\mvn.ps1" -version

Write-Host ""
Write-Host "=== Compilation du projet ==="
& "$PSScriptRoot\mvn.ps1" clean install -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERREUR: La compilation a échoué!"
    exit 1
}

Write-Host ""
Write-Host "=== Lancement de l'application JavaFX ==="
& "$PSScriptRoot\mvn.ps1" javafx:run

Write-Host ""
Read-Host "Appuyez sur Entrée pour terminer"

