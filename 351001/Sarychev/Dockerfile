FROM maven:3.8.4-openjdk-17 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre

RUN addgroup --system --gid 1001 spring && \
adduser --system --uid 1001 --ingroup spring spring
USER spring:spring

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 24110

ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xmx512m -Xms256m"

  # Команда запуска
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]