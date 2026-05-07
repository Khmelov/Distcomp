FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY notebook-api/pom.xml notebook-api/
COPY publisher/pom.xml publisher/
COPY discussion/pom.xml discussion/

RUN apk add --no-cache maven \
    && mvn dependency:go-offline -B -pl publisher -am

COPY notebook-api/src notebook-api/src
COPY publisher/src publisher/src

RUN mvn package -pl publisher -am -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -g 1000 app && adduser -u 1000 -G app -D app
USER app

COPY --from=build /app/publisher/target/publisher-*-exec.jar app.jar

EXPOSE 24110

ENTRYPOINT ["java", "-jar", "app.jar"]
