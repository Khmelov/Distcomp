# Исправления для тестов

## Проблема 1: SQL запросы не находят таблицы

**Ошибка:** `ERROR: relation "tbl_writer" does not exist`

**Причина:** SQL запросы выполняются без указания схемы `distcomp`.

**Решение:** Все SQL запросы должны использовать полное имя таблицы с указанием схемы:

```sql
-- Правильно
SELECT * FROM distcomp.tbl_writer WHERE id = 13;

-- Неправильно
SELECT * FROM tbl_writer WHERE id = 13;
```

**Или установите search_path для сессии:**

```sql
SET search_path TO distcomp, public;
SELECT * FROM tbl_writer WHERE id = 13;
```

## Проблема 2: Ожидается 403, но возвращается 409

**Ошибка:** Тест ожидает статус код `403`, но получает `409`.

**Причина:** Для дубликата login правильно возвращать `409 CONFLICT`, а не `403 FORBIDDEN`.

**Объяснение:**
- `403 FORBIDDEN` - используется, когда доступ запрещен (авторизация)
- `409 CONFLICT` - используется, когда ресурс уже существует (дубликат)

**Текущая реализация (правильная):**
```java
@ExceptionHandler(ConflictException.class)
public ResponseEntity<ApiError> handleConflict(ConflictException ex) {
    return build(HttpStatus.CONFLICT, ex.getMessage(), "40901");
}
```

**Рекомендация:** Обновите тесты, чтобы они ожидали `409 CONFLICT` вместо `403 FORBIDDEN` для дубликатов.

## Настройки для тестов

### application-test.properties
```properties
spring.datasource.url=jdbc:tc:postgresql:15:///distcomp?currentSchema=distcomp
spring.jpa.properties.hibernate.default_schema=distcomp
spring.liquibase.default-schema=distcomp
```

### schema-test.sql
```sql
CREATE SCHEMA IF NOT EXISTS distcomp;
SET search_path TO distcomp, public;
```

## Проверка базы данных

Если нужно проверить данные напрямую через SQL:

```sql
-- Подключитесь к базе
\c distcomp

-- Установите схему
SET search_path TO distcomp;

-- Или используйте полное имя
SELECT * FROM distcomp.tbl_writer WHERE id = 13;
```

