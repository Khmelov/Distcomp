-- Подключаемся к базе данных distcomp
\c distcomp;

-- Создание схемы distcomp внутри базы данных distcomp
CREATE SCHEMA IF NOT EXISTS distcomp;

-- Установка схемы по умолчанию для пользователя postgres
ALTER USER postgres SET search_path TO distcomp, public;

-- Даем права на схему
GRANT ALL ON SCHEMA distcomp TO postgres;
GRANT ALL PRIVILEGES ON DATABASE distcomp TO postgres;

-- Даем права на все таблицы в схеме (для будущих таблиц)
ALTER DEFAULT PRIVILEGES IN SCHEMA distcomp GRANT ALL ON TABLES TO postgres;
ALTER DEFAULT PRIVILEGES IN SCHEMA distcomp GRANT ALL ON SEQUENCES TO postgres;