@echo off
chcp 65001 >nul
title Trip Split
cd /d "%~dp0"

echo [1/2] Compiling...
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
if errorlevel 1 (
    echo.
    echo Compile failed. Please install JDK 17 or newer.
    del sources.txt
    pause
    exit /b 1
)
del sources.txt

echo [2/2] Starting Trip Split...
start "" javaw -cp out tripsplit.Main
