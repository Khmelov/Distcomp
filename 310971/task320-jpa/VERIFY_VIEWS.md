# Проверка Views и данных

## Проблема: SQL запросы не находят данные

Если запросы вида `SELECT * FROM tbl_label WHERE name='red63'` не возвращают данные, проверьте следующее:

### 1. Проверьте, созданы ли views

Подключитесь к PostgreSQL и выполните:

```sql
\c distcomp

-- Проверьте существование views
SELECT table_schema, table_name, table_type
FROM information_schema.tables
WHERE table_name LIKE 'tbl_%'
ORDER BY table_schema, table_name;
```

Должны быть:
- `public.tbl_writer` (view)
- `public.tbl_tweet` (view)
- `public.tbl_label` (view)
- `public.tbl_message` (view)
- `public.tbl_tweet_label` (view)
- `distcomp.tbl_writer` (table)
- `distcomp.tbl_tweet` (table)
- `distcomp.tbl_label` (table)
- `distcomp.tbl_message` (table)
- `distcomp.tbl_tweet_label` (table)

### 2. Если views не созданы, создайте их вручную

```sql
-- Создайте views вручную
CREATE OR REPLACE VIEW public.tbl_writer AS SELECT * FROM distcomp.tbl_writer;
CREATE OR REPLACE VIEW public.tbl_tweet AS SELECT * FROM distcomp.tbl_tweet;
CREATE OR REPLACE VIEW public.tbl_label AS SELECT * FROM distcomp.tbl_label;
CREATE OR REPLACE VIEW public.tbl_message AS SELECT * FROM distcomp.tbl_message;
CREATE OR REPLACE VIEW public.tbl_tweet_label AS SELECT * FROM distcomp.tbl_tweet_label;
```

### 3. Проверьте, где находятся данные

```sql
-- Проверьте данные в схеме distcomp
SELECT COUNT(*) FROM distcomp.tbl_label WHERE name = 'red63';
SELECT COUNT(*) FROM distcomp.tbl_label WHERE name = 'green63';
SELECT COUNT(*) FROM distcomp.tbl_label WHERE name = 'blue63';

-- Проверьте данные через view в public
SELECT COUNT(*) FROM public.tbl_label WHERE name = 'red63';
SELECT COUNT(*) FROM public.tbl_label WHERE name = 'green63';
SELECT COUNT(*) FROM public.tbl_label WHERE name = 'blue63';
```

### 4. Если данные не существуют

Данные должны создаваться через API:
```bash
POST http://localhost:24110/api/v1.0/labels
{
    "name": "red63"
}
```

Проверьте, что API работает и данные сохраняются:
```sql
SELECT * FROM distcomp.tbl_label ORDER BY id DESC LIMIT 10;
```

### 5. Альтернативное решение: Использовать полные имена

Если views не работают, используйте полные имена таблиц в SQL запросах:

```sql
SELECT id, name FROM distcomp.tbl_label WHERE name='red63';
```

### 6. Проверьте search_path

```sql
-- Проверьте текущий search_path
SHOW search_path;

-- Установите search_path для сессии
SET search_path TO distcomp, public;

-- Теперь запросы должны работать
SELECT * FROM tbl_label WHERE name='red63';
```

