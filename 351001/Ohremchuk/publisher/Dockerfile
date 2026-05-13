FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd
COPY pom.xml pom.xml
COPY publisher/pom.xml publisher/pom.xml
COPY discussion/pom.xml discussion/pom.xml
COPY src/ src/
COPY discussion/src/ discussion/src/

RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/publisher/target/*.jar app.jar
EXPOSE 24110
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["java","-jar","/app/app.jar"]

