FROM amazoncorretto:17-alpine
WORKDIR /app
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 24130 24110
ENTRYPOINT ["java", "-jar", "app.jar"]