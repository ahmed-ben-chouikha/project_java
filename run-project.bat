@echo off
REM Wrapper pour lancer Maven avec Java 8
setx JAVA_HOME "C:\Java\jdk-1.8"
set "JAVA_HOME=C:\Java\jdk-1.8"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Verification de Java 8...
"%JAVA_HOME%\bin\java" -version

echo.
echo Verification de Maven...
call mvn -version

echo.
echo === Compilation et installation du projet ===
cd /d "C:\Users\melki\OneDrive\Bureau\pidev\project_java"
call mvn clean install

echo.
echo === Lancement de l'application JavaFX ===
call mvn javafx:run

pause

