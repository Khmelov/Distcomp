#!/usr/bin/env bash
# Запуск приложения с корректным JAVA_HOME для openjdk@21 (Homebrew)

if [ -d "/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" ]; then
  export JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
elif [ -d "/usr/local/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" ]; then
  export JAVA_HOME="/usr/local/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
fi

# Default: publisher (24110). Example: MODULE=discussion ./run.sh  → discussion (24130)
MODULE="${MODULE:-publisher}"
exec ./mvnw -pl "$MODULE" spring-boot:run "$@"
