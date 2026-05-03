@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.10
set PATH=%JAVA_HOME%\bin;%PATH%

echo Using Java:
java -version
echo.

set WRAPPER_JAR=".mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

if not exist %WRAPPER_JAR% (
  echo Maven wrapper jar not found at %WRAPPER_JAR%
  exit /b 1
)

"%JAVA_HOME%\bin\java.exe" -jar %WRAPPER_JAR% %*
