# Исправление структуры таблицы tbl_tweet_label

## Проблема

Ошибка: `ERROR: column tl1_0.id does not exist` в таблице `tbl_tweet_label`

## Причина

Таблица `tbl_tweet_label` была создана без колонки `id`, или была создана в неправильной схеме.

## Решение

### Вариант 1: Пересоздать таблицу (рекомендуется)

Подключитесь к PostgreSQL и выполните:

```sql
-- Подключитесь к базе
\c distcomp

-- Удалите таблицу (удалит все связи)
DROP TABLE IF EXISTS distcomp.tbl_tweet_label CASCADE;

-- Перезапустите приложение - Liquibase создаст таблицу правильно
```

### Вариант 2: Добавить колонку вручную

```sql
-- Проверьте структуру таблицы
\d distcomp.tbl_tweet_label

-- Если колонки id нет, добавьте её
ALTER TABLE distcomp.tbl_tweet_label 
ADD COLUMN IF NOT EXISTS id BIGSERIAL PRIMARY KEY;

-- Если таблица в схеме public, переместите её
ALTER TABLE IF EXISTS public.tbl_tweet_label SET SCHEMA distcomp;
```

### Вариант 3: Проверить, где находится таблица

```sql
-- Проверьте все таблицы с именем tbl_tweet_label
SELECT table_schema, table_name, column_name, data_type
FROM information_schema.columns
WHERE table_name = 'tbl_tweet_label'
ORDER BY table_schema, ordinal_position;
```

### Вариант 4: Пересоздать всю схему (удалит все данные!)

```sql
-- ОСТОРОЖНО: это удалит все данные!
DROP SCHEMA IF EXISTS distcomp CASCADE;
CREATE SCHEMA distcomp;

-- Перезапустите приложение
```

## Проверка после исправления

```sql
-- Проверьте структуру таблицы
\d distcomp.tbl_tweet_label

-- Должны быть колонки:
-- id (BIGSERIAL, PRIMARY KEY)
-- tweet_id (BIGINT, NOT NULL)
-- label_id (BIGINT, NOT NULL)
```

## Миграция 007

Добавлена миграция `007-fix-tbl-tweet-label-id.xml`, которая автоматически добавит колонку `id`, если её нет. Перезапустите приложение, и миграция применится автоматически.

