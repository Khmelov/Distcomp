@ECHO OFF
SETLOCAL

SET MAVEN_PROJECTBASEDIR=%~dp0
SET MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

IF NOT DEFINED JAVA_HOME GOTO findJavaFromPath
SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
IF EXIST "%JAVA_EXE%" GOTO init

:findJavaFromPath
FOR %%i IN (java.exe) DO SET JAVA_EXE=%%~$PATH:i
IF NOT EXIST "%JAVA_EXE%" (
  ECHO Java not found. Please install JDK 17+ and ensure java is in PATH.
  EXIT /B 1
)

:init
SET WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar
SET WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar

IF EXIST "%WRAPPER_JAR%" GOTO run

ECHO Downloading Maven Wrapper...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$p='%WRAPPER_JAR%'; $u='%WRAPPER_URL%'; New-Item -ItemType Directory -Force -Path (Split-Path $p) | Out-Null; Invoke-WebRequest -UseBasicParsing -Uri $u -OutFile $p" 
IF NOT EXIST "%WRAPPER_JAR%" (
  ECHO Failed to download Maven Wrapper jar.
  EXIT /B 1
)

:run
"%JAVA_EXE%" %MAVEN_OPTS% -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %*
