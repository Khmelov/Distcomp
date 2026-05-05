# Build from repository root (orsik/)
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /src
COPY . .
RUN chmod +x mvnw && ./mvnw -q -pl publisher -am package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache netcat-openbsd
WORKDIR /app
COPY --from=build /src/publisher/target/publisher-*.jar /app/app.jar
EXPOSE 24110
ENV SPRING_PROFILES_ACTIVE=docker
HEALTHCHECK --interval=10s --timeout=3s --start-period=90s --retries=15 \
  CMD nc -z 127.0.0.1 24110 || exit 1
ENTRYPOINT ["java","-jar","/app/app.jar"]
