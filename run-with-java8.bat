@echo off
REM Script pour configurer JAVA_HOME de manière persistante et lancer Maven
setlocal enabledelayedexpansion

REM Configuration de JAVA_HOME
set "JAVA_HOME=C:\Java\jdk-1.8"
set "PATH=!JAVA_HOME!\bin;%PATH%"

REM Définir aussi la variable pour Maven
set "M2_HOME=C:\Program Files\apache-maven-3.9.9"

echo ========================================
echo Configuration:
echo JAVA_HOME: !JAVA_HOME!
echo M2_HOME: !M2_HOME!
echo ========================================

echo.
echo Verification de Java 8...
"!JAVA_HOME!\bin\java" -version

echo.
echo === Compilation et installation du projet ===
cd /d "C:\Users\melki\OneDrive\Bureau\pidev\project_java"

REM Appel direct à Maven avec JAVA_HOME défini
"!M2_HOME!\bin\mvn" clean install -DskipTests

if errorlevel 1 (
    echo.
    echo ERREUR: La compilation a échoué!
    pause
    exit /b 1
)

echo.
echo === Lancement de l'application JavaFX ===
"!M2_HOME!\bin\mvn" javafx:run

pause

