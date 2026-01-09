# Исправление SQL запросов для внешней системы тестирования

## Проблема

Внешняя система тестирования выполняет SQL запросы без указания схемы:
```sql
SELECT * FROM tbl_writer WHERE id=24
```

Но таблицы находятся в схеме `distcomp`, поэтому PostgreSQL ищет их в схеме `public` (по умолчанию) и не находит.

## Решения

### Решение 1: Использовать полное имя таблицы (РЕКОМЕНДУЕТСЯ)

Измените все SQL запросы, чтобы они использовали полное имя таблицы с указанием схемы:

```sql
-- Вместо:
SELECT * FROM tbl_writer WHERE id=24

-- Используйте:
SELECT * FROM distcomp.tbl_writer WHERE id=24
```

### Решение 2: Установить search_path в connection string

Добавьте параметр `currentSchema=distcomp` в JDBC URL:

```
jdbc:postgresql://localhost:5432/distcomp?currentSchema=distcomp
```

### Решение 3: Установить search_path для базы данных

Выполните SQL команду (требуются права суперпользователя):

```sql
ALTER DATABASE distcomp SET search_path TO distcomp, public;
```

После этого переподключитесь к базе данных - все запросы будут искать таблицы сначала в схеме `distcomp`.

### Решение 4: Создать синонимы в схеме public

Если вы не можете изменить SQL запросы, создайте views в схеме `public`:

```sql
CREATE OR REPLACE VIEW public.tbl_writer AS SELECT * FROM distcomp.tbl_writer;
CREATE OR REPLACE VIEW public.tbl_tweet AS SELECT * FROM distcomp.tbl_tweet;
CREATE OR REPLACE VIEW public.tbl_label AS SELECT * FROM distcomp.tbl_label;
CREATE OR REPLACE VIEW public.tbl_message AS SELECT * FROM distcomp.tbl_message;
CREATE OR REPLACE VIEW public.tbl_tweet_label AS SELECT * FROM distcomp.tbl_tweet_label;
```

**Внимание:** Views доступны только для чтения. Для записи нужно использовать полные имена таблиц.

## Проверка текущего состояния

Подключитесь к PostgreSQL и выполните:

```sql
-- Проверьте, где находятся таблицы
SELECT table_schema, table_name 
FROM information_schema.tables 
WHERE table_name LIKE 'tbl_%'
ORDER BY table_schema, table_name;

-- Проверьте текущий search_path
SHOW search_path;

-- Проверьте search_path для базы данных
SELECT datname, datconfig 
FROM pg_database 
WHERE datname = 'distcomp';
```

## Рекомендация

**Лучшее решение:** Используйте полные имена таблиц в SQL запросах:
- `distcomp.tbl_writer`
- `distcomp.tbl_tweet`
- `distcomp.tbl_label`
- `distcomp.tbl_message`
- `distcomp.tbl_tweet_label`

Это гарантирует, что запросы всегда найдут правильные таблицы независимо от настроек search_path.

