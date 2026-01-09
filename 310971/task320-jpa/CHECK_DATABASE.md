# Проверка базы данных

## Проблема: "relation tbl_writer does not exist"

Если вы получаете эту ошибку, проверьте следующее:

### 1. Проверьте, что схема `distcomp` существует

Подключитесь к PostgreSQL и выполните:

```sql
-- Подключитесь к базе данных
\c distcomp

-- Проверьте существование схемы
SELECT schema_name FROM information_schema.schemata WHERE schema_name = 'distcomp';

-- Если схемы нет, создайте её
CREATE SCHEMA IF NOT EXISTS distcomp;
```

### 2. Проверьте, в какой схеме находятся таблицы

```sql
-- Проверьте все таблицы в схеме distcomp
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'distcomp';

-- Проверьте таблицы в схеме public
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public';
```

### 3. Если таблицы в схеме public, а не distcomp

**Вариант А:** Переместите таблицы в схему distcomp:

```sql
-- Переместите все таблицы из public в distcomp
ALTER TABLE public.tbl_writer SET SCHEMA distcomp;
ALTER TABLE public.tbl_label SET SCHEMA distcomp;
ALTER TABLE public.tbl_tweet SET SCHEMA distcomp;
ALTER TABLE public.tbl_message SET SCHEMA distcomp;
ALTER TABLE public.tbl_tweet_label SET SCHEMA distcomp;
```

**Вариант Б:** Пересоздайте схему (удалит все данные!):

```sql
-- Удалите схему distcomp со всеми таблицами
DROP SCHEMA IF EXISTS distcomp CASCADE;

-- Создайте схему заново
CREATE SCHEMA distcomp;

-- Перезапустите приложение - Liquibase создаст таблицы
```

### 4. Используйте полное имя таблицы в запросах

Если вы делаете прямые SQL запросы, всегда указывайте схему:

```sql
-- Правильно (с указанием схемы)
SELECT * FROM distcomp.tbl_writer WHERE id = 13;

-- Неправильно (без схемы - ищет в public)
SELECT * FROM tbl_writer WHERE id = 13;
```

### 5. Настройте search_path для текущей сессии

```sql
-- Установите схему distcomp как приоритетную
SET search_path TO distcomp, public;

-- Теперь можно использовать короткие имена
SELECT * FROM tbl_writer WHERE id = 13;
```

### 6. Проверьте настройки приложения

Убедитесь, что в `application.properties`:

```properties
spring.jpa.properties.hibernate.default_schema=distcomp
spring.liquibase.default-schema=distcomp
```

### 7. Быстрое решение

Если нужно быстро проверить данные:

```sql
-- Подключитесь к базе
\c distcomp

-- Установите схему по умолчанию
SET search_path TO distcomp;

-- Теперь запросы будут работать
SELECT * FROM tbl_writer WHERE id = 13;
```

