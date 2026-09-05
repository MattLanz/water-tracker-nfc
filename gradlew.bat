@echo off
setlocal enabledelayedexpansion
set DIR=%~dp0
set CMD=%DIR%gradle\wrapper\gradle-wrapper.jar
"%JAVA_HOME%\bin\java" -jar "%CMD%" %*
