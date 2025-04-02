@echo off
setlocal EnableDelayedExpansion

rem Set environment variables
set "JAVA_HOME=%~dp0venv\jdk21\jdk-21.0.2+13"
set "MAVEN_HOME=%~dp0venv\maven\apache-maven-3.9.6"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

echo Current directory: %CD%
echo JAVA_HOME: %JAVA_HOME%
echo MAVEN_HOME: %MAVEN_HOME%

echo Checking Java installation...
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo Error: Java not found at %JAVA_HOME%\bin\java.exe
    exit /b 1
)

echo Verifying Java version...
"%JAVA_HOME%\bin\java.exe" -version
if errorlevel 1 (
    echo Error: Failed to verify Java version
    exit /b 1
)

echo.
echo Checking Maven installation...
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Error: Maven not found at %MAVEN_HOME%\bin\mvn.cmd
    exit /b 1
)

echo Verifying Maven version...
"%MAVEN_HOME%\bin\mvn.cmd" -version
if errorlevel 1 (
    echo Error: Failed to verify Maven version
    exit /b 1
)

echo.
echo Building and running the project...
echo Step 1: Clean and compile...
"%MAVEN_HOME%\bin\mvn.cmd" clean compile -e
if errorlevel 1 (
    echo Error: Build failed
    exit /b 1
)

echo.
echo Step 2: Run tests...
"%MAVEN_HOME%\bin\mvn.cmd" test -e
if errorlevel 1 (
    echo Error: Tests failed
    exit /b 1
)

echo.
echo Step 3: Package and run...
"%MAVEN_HOME%\bin\mvn.cmd" package spring-boot:run -e -DskipTests
if errorlevel 1 (
    echo Error: Failed to run the application
    exit /b 1
)

endlocal 