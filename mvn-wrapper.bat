@echo off
REM Wrapper simple pour Maven avec Java 17
setlocal enabledelayedexpansion

REM Configuration
set "JAVA_HOME=C:\Java\jdk-17.0.12"
set "M2_HOME=C:\Program Files\apache-maven-3.9.9"

REM Appeler mvn avec les arguments
"%M2_HOME%\bin\mvn.cmd" %*

