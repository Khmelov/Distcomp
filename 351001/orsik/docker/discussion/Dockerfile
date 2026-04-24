# Build from repository root (orsik/)
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /src
COPY . .
RUN chmod +x mvnw && ./mvnw -q -pl discussion -am package -DskipTests

FROM eclipse-temurin:21-jre-alpine
# TCP probe: GET /comments без tweetId трогает Cassandra (полный findAll) и часто >5s — healthcheck не проходит
RUN apk add --no-cache netcat-openbsd
WORKDIR /app
COPY --from=build /src/discussion/target/discussion-*.jar /app/app.jar
EXPOSE 24130
ENV SPRING_PROFILES_ACTIVE=docker
HEALTHCHECK --interval=10s --timeout=3s --start-period=120s --retries=20 \
  CMD nc -z 127.0.0.1 24130 || exit 1
ENTRYPOINT ["java","-jar","/app/app.jar"]
