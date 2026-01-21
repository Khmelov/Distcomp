# Dockerfile для Publisher
# Расположение: publisher/Dockerfile

# 1. Используем официальный образ Java
FROM openjdk:21-jdk-slim

# 2. Устанавливаем рабочую директорию в контейнере
WORKDIR /app

# 3. Копируем JAR файл из target папки в контейнер
# Важно: сначала нужно собрать проект с помощью Maven!
COPY target/*.jar app.jar

# 4. Создаем папку для логов
RUN mkdir -p /app/logs

# 5. Указываем порт, который будет открыт
EXPOSE 24110

# 6. Команда для запуска приложения
# Используем exec form для лучшей обработки сигналов
ENTRYPOINT ["java", "-jar", "app.jar"]

# 7. Аргументы по умолчанию (можно переопределить при запуске)
CMD ["--server.port=24110", "--spring.profiles.active=docker"]