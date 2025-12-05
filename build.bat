@echo off

REM This is a simple build script for ClearLag plugin
REM It works best in Windows Command Prompt (cmd.exe)

cls
echo =========================================
echo ClearLag Plugin Build Script
echo =========================================
echo.

echo Step 1: Checking Java...
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo FAILED: Java not found!
    echo Please install Java 8+ and add to PATH.
    echo.
    pause
    exit /b 1
)
echo PASSED: Java is installed!
echo.

echo Step 2: Building project...
echo =========================================
gradle jar
if %ERRORLEVEL% NEQ 0 (
    echo =========================================
    echo FAILED: Build failed!
    echo.
    pause
    exit /b 1
)

echo =========================================
echo SUCCESS: Build completed!
echo JAR file: build\libs\ClearLag1.0.jar
echo.

:END
pause
