@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.10
set PATH=%JAVA_HOME%\bin;%PATH%

echo ========================================
echo Building with Java:
%JAVA_HOME%\bin\java -version
echo ========================================
echo.

echo Running Maven clean compile...
call mvnw.cmd clean compile

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo Build successful!
    echo Starting application...
    echo ========================================
    call mvnw.cmd spring-boot:run
) else (
    echo.
    echo ========================================
    echo Build failed with error code: %errorlevel%
    echo ========================================
    pause
)
