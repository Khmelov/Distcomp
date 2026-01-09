# Troubleshooting Task 320 JPA

## Ошибка запуска приложения

### Проблема: Error starting ApplicationContext

#### Решение 1: Проверьте, что PostgreSQL запущен

```bash
# Проверьте, что PostgreSQL запущен
pg_isready -h localhost -p 5432

# Или через Docker
docker ps | grep postgres
```

#### Решение 2: Создайте базу данных и схему

Подключитесь к PostgreSQL и выполните:

```sql
-- Создайте базу данных (если еще не создана)
CREATE DATABASE distcomp;

-- Подключитесь к базе данных
\c distcomp

-- Создайте схему
CREATE SCHEMA IF NOT EXISTS distcomp;

-- Проверьте права пользователя postgres
GRANT ALL PRIVILEGES ON SCHEMA distcomp TO postgres;
GRANT ALL PRIVILEGES ON DATABASE distcomp TO postgres;
```

#### Решение 3: Очистите базу данных и пересоздайте

Если база данных уже существует, но миграции не применяются:

```sql
-- Удалите схему (ОСТОРОЖНО: удалит все данные!)
DROP SCHEMA IF EXISTS distcomp CASCADE;

-- Создайте схему заново
CREATE SCHEMA distcomp;
```

Затем запустите приложение - Liquibase создаст таблицы автоматически.

#### Решение 4: Проверьте настройки в application.properties

Убедитесь, что в `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/distcomp
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml
spring.liquibase.default-schema=distcomp
```

#### Решение 5: Запуск через IntelliJ IDEA

1. Откройте `Task320JpaApplication.java`
2. Убедитесь, что PostgreSQL запущен
3. Нажмите Run (зеленая стрелка)
4. Если ошибка сохраняется, проверьте логи в консоли

#### Решение 6: Проблемы с компиляцией Lombok

Если Maven не компилирует из-за Lombok:

1. Запускайте через IntelliJ IDEA (не через Maven)
2. В IntelliJ: File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Включите "Enable annotation processing"
3. Пересоберите проект: Build → Rebuild Project

### Типичные ошибки

#### Ошибка: "Schema 'distcomp' does not exist"
**Решение**: Создайте схему вручную (см. Решение 2)

#### Ошибка: "Table 'tbl_writer' already exists"
**Решение**: Удалите схему и пересоздайте (см. Решение 3)

#### Ошибка: "Connection refused"
**Решение**: Убедитесь, что PostgreSQL запущен на localhost:5432

#### Ошибка: "Password authentication failed"
**Решение**: Проверьте username и password в application.properties

