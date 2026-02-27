# Создаем директорию для инструментов
cd /project-root
mkdir -p tools/liquibase

# Скачиваем Liquibase
wget https://github.com/liquibase/liquibase/releases/download/v4.24.0/liquibase-4.24.0.tar.gz

# Распаковываем в tools/liquibase
tar -xzf liquibase-4.24.0.tar.gz -C tools/liquibase/

# Делаем исполняемый файл доступным
chmod +x tools/liquibase/liquibase

# Скачиваем JDBC драйвер для PostgreSQL
wget https://jdbc.postgresql.org/download/postgresql-42.7.1.jar
mv postgresql-42.7.1.jar tools/liquibase/lib/

# В директории liquibase

# Проверить статус
./liquibase status

# Применить миграции
./liquibase update

# Применить только для разработки (с тестовыми данными)
./liquibase update -Dcontext=dev

# Сгенерировать SQL без выполнения
./liquibase update-sql > migration.sql

# Откатить последние 2 изменения
./liquibase rollback-count 2

# Посмотреть историю
./liquibase history