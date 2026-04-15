# Script PowerShell pour compiler et lancer avec Java 17
$JAVA_HOME = "C:\Java\jdk-17.0.12"
$M2_HOME = "C:\Program Files\apache-maven-3.9.9"
$MAVEN_CLASSPATH_JAR = "$M2_HOME\boot\plexus-classworlds-*"

# Vérifier Java 17
Write-Host "Verification de Java 17..."
& "$JAVA_HOME\bin\java" -version

Write-Host ""
Write-Host "=== Compilation du projet ==="
cd "C:\Users\melki\OneDrive\Bureau\pidev\project_java"

# Appel direct à Maven via Java (bypass mvn.cmd)
$classworlds = (Get-ChildItem "$M2_HOME\boot\plexus-classworlds-*.jar" | Select-Object -First 1).FullName

& "$JAVA_HOME\bin\java" `
  -classpath "$classworlds" `
  "-Dclassworlds.conf=$M2_HOME\bin\m2.conf" `
  "-Dmaven.home=$M2_HOME" `
  "-Dmaven.multiModuleProjectDirectory=$(Get-Location)" `
  org.codehaus.plexus.classworlds.launcher.Launcher `
  clean install -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERREUR: La compilation a échoué!"
    exit 1
}

Write-Host ""
Write-Host "=== Lancement de l'application JavaFX ==="
& "$JAVA_HOME\bin\java" `
  -classpath "$classworlds" `
  "-Dclassworlds.conf=$M2_HOME\bin\m2.conf" `
  "-Dmaven.home=$M2_HOME" `
  "-Dmaven.multiModuleProjectDirectory=$(Get-Location)" `
  org.codehaus.plexus.classworlds.launcher.Launcher `
  javafx:run

Write-Host ""
Read-Host "Appuyez sur Entrée pour terminer"

