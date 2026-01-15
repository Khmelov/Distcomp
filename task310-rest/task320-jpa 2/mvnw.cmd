@echo off
rem Maven Wrapper script for Windows

set MAVEN_VERSION=3.9.6
set MAVEN_HOME=%USERPROFILE%\.m2\wrapper\apache-maven-%MAVEN_VERSION%

if not exist "%MAVEN_HOME%" (
    echo Downloading Maven %MAVEN_VERSION%...
    mkdir "%USERPROFILE%\.m2\wrapper"
    curl -L "https://dlcdn.apache.org/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip" -o "%TEMP%\maven.zip"
    tar -xf "%TEMP%\maven.zip" -C "%USERPROFILE%\.m2\wrapper\"
    del "%TEMP%\maven.zip"
    echo Maven downloaded successfully
)

set PATH=%MAVEN_HOME%\bin;%PATH%
mvn %*
