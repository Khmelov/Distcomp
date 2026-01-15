-- Инициализация схемы distcomp
CREATE SCHEMA IF NOT EXISTS distcomp;

-- Установка схемы по умолчанию
SET search_path TO distcomp, public;

-- Логирование
SELECT 'Schema distcomp created successfully' AS status;
