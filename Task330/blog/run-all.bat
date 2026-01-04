@echo off
echo === Запуск проекта Blog ===
echo.

echo 1. Запуск PostgreSQL (если не запущена)...
echo Проверьте что PostgreSQL запущена на localhost:5432
echo.

echo 2. Запуск Cassandra...
docker stop cassandra-distcomp 2>nul
docker rm cassandra-distcomp 2>nul

echo Запускаю Cassandra...
docker run --name cassandra-distcomp -p 9042:9042 -d cassandra:4.1

echo Ожидание запуска Cassandra (60 секунд)...
timeout /t 60 /nobreak >nul

echo Создание keyspace distcomp...
docker exec cassandra-distcomp cqlsh -e "
CREATE KEYSPACE IF NOT EXISTS distcomp
WITH replication = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};"

echo Создание таблицы tbl_message...
docker exec cassandra-distcomp cqlsh -e "
USE distcomp;
CREATE TABLE IF NOT EXISTS tbl_message (
    country TEXT,
    topic_id BIGINT,
    id BIGINT,
    content TEXT,
    created TIMESTAMP,
    modified TIMESTAMP,
    PRIMARY KEY ((country), topic_id, id)
);"

echo.
echo 3. Запуск Discussion модуля (порт 24130)...
start cmd /k "cd discussion && mvn spring-boot:run"

echo Ожидание запуска Discussion (10 секунд)...
timeout /t 10 /nobreak >nul

echo.
echo 4. Запуск Publisher модуля (порт 24110)...
start cmd /k "cd publisher && mvn spring-boot:run"

echo.
echo =============================================
echo Приложения запущены!
echo - Publisher: http://localhost:24110
echo - Discussion: http://localhost:24130
echo - Cassandra: localhost:9042
echo - PostgreSQL: localhost:5432
echo =============================================
echo.
echo Для тестирования выполните:
echo curl http://localhost:24110/
echo curl http://localhost:24130/health
echo.
pause