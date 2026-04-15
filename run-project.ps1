# Script pour lancer le projet avec Java 17
$javaCmd = Get-Command java.exe -ErrorAction Stop
$detectedJavaHome = Split-Path (Split-Path $javaCmd.Source -Parent) -Parent
$env:JAVA_HOME = $detectedJavaHome

if (-not ($env:Path -split ';' | Where-Object { $_ -eq "$env:JAVA_HOME\bin" })) {
	$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
}

Write-Host "Vérification de Java 17..."
& "$env:JAVA_HOME\bin\java.exe" -version 2>&1
Write-Host ""

Write-Host "Vérification de Maven..."
& ".\mvn.ps1" -version
Write-Host ""

Write-Host "=== Compilation et installation du projet ==="
& ".\mvn.ps1" clean install
Write-Host ""

Write-Host "=== Lancement de l'application JavaFX ==="
& ".\mvn.ps1" javafx:run

