# Script PowerShell pour lancer Maven avec Java 17
param(
    [Parameter(ValueFromRemainingArguments=$true)]
    [string[]]$MavenArgs
)

$javaCmd = Get-Command java.exe -ErrorAction Stop
$JAVA_HOME = Split-Path (Split-Path $javaCmd.Source -Parent) -Parent

$mavenCmd = Get-Command mvn.cmd -ErrorAction Stop
$M2_HOME = Split-Path (Split-Path $mavenCmd.Source -Parent) -Parent
$classworlds = (Get-ChildItem "$M2_HOME\boot\plexus-classworlds-*.jar" | Select-Object -First 1).FullName

if (-not $classworlds) {
  throw "Impossible de trouver plexus-classworlds dans $M2_HOME\boot"
}

# Construire le répertoire courant
$projectDir = Get-Location

# Appeler Maven via Java directement
& "$JAVA_HOME\bin\java" `
  -classpath "$classworlds" `
  "-Dclassworlds.conf=$M2_HOME\bin\m2.conf" `
  "-Dmaven.home=$M2_HOME" `
  "-Dmaven.multiModuleProjectDirectory=$projectDir" `
  org.codehaus.plexus.classworlds.launcher.Launcher `
  @MavenArgs

