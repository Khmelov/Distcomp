# Task320: Слой хранения (JPA)

REST API приложение с PostgreSQL, JPA/Hibernate и Liquibase для управления сущностями User, Tweet, Mark и Note.

## 🎯 Отличия от Task310

Task310 использовал **InMemory хранилище** (ConcurrentHashMap).  
Task320 использует **PostgreSQL + JPA/Hibernate + Liquibase**.

## 🔧 Технологии

- **Java 21**
- **Spring Boot 3.2.1**
- **Spring Data JPA**
- **PostgreSQL**
- **Liquibase** (XML миграции)
- **MapStruct** - маппинг DTO
- **Lombok** - уменьшение boilerplate
- **Maven**

## 📋 Требования

- **Java 21**
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Docker** (опционально, для быстрого запуска PostgreSQL)

---

## 🚀 Быстрый старт

### Вариант 1: С Docker (РЕКОМЕНДУЕТСЯ)

```bash
# 1. Запустите PostgreSQL в Docker
docker run --name task320-postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_DB=distcomp \
  -p 5432:5432 \
  -d postgres:15

# 2. Создайте схему distcomp
docker exec -it task320-postgres psql -U postgres -d distcomp -c "CREATE SCHEMA IF NOT EXISTS distcomp;"

# 3. Запустите приложение
cd task320-jpa
./mvnw spring-boot:run
```

### Вариант 2: С локальным PostgreSQL

#### Шаг 1: Установите PostgreSQL

**macOS (Homebrew):**
```bash
brew install postgresql@15
brew services start postgresql@15
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

**Windows:**
Скачайте с https://www.postgresql.org/download/windows/

#### Шаг 2: Создайте базу данных и схему

```bash
# Подключитесь к PostgreSQL
psql -U postgres

# Создайте базу данных
CREATE DATABASE distcomp;

# Подключитесь к базе
\c distcomp

# Создайте схему
CREATE SCHEMA IF NOT EXISTS distcomp;

# Установите пароль для пользователя postgres (если нужно)
ALTER USER postgres WITH PASSWORD 'postgres';

# Выход
\q
```

#### Шаг 3: Запустите приложение

```bash
cd task320-jpa
./mvnw spring-boot:run
```

---

## 🗄️ Структура базы данных

### Таблицы (с префиксом `tbl_`):

- **tbl_user** - пользователи
- **tbl_tweet** - твиты
- **tbl_mark** - метки/теги
- **tbl_note** - заметки
- **tbl_tweet_mark** - связь many-to-many между Tweet и Mark

### Liquibase миграции:

```
src/main/resources/db/changelog/
├── db.changelog-master.xml              # Мастер-файл
└── changeset/
    ├── 001-create-table-user.xml        # Создание tbl_user
    ├── 002-create-table-tweet.xml       # Создание tbl_tweet
    ├── 003-create-table-mark.xml        # Создание tbl_mark
    ├── 004-create-table-note.xml        # Создание tbl_note
    ├── 005-create-table-tweet-mark.xml  # Создание tbl_tweet_mark
    └── 006-insert-initial-data.xml      # Начальные данные
```

---

## 🌐 API Endpoints

### Базовый URL: `http://localhost:24110/api/v1.0`

### Пагинация и сортировка

Все GET endpoints поддерживают пагинацию:

```bash
GET /api/v1.0/users?page=0&size=10&sort=id,desc
```

Параметры:
- `page` - номер страницы (по умолчанию 0)
- `size` - размер страницы (по умолчанию 10)
- `sort` - поле и направление (например: id,desc или firstname,asc)

### User Endpoints

| Метод | Endpoint | Описание |
|-------|----------|----------|
| POST | /api/v1.0/users | Создать |
| GET | /api/v1.0/users | Список (с пагинацией) |
| GET | /api/v1.0/users/{id} | Получить по ID |
| PUT | /api/v1.0/users/{id} | Обновить |
| PATCH | /api/v1.0/users/{id} | Частичное обновление |
| DELETE | /api/v1.0/users/{id} | Удалить |

### Tweet, Mark, Note

Аналогичные endpoints для Tweet, Mark, Note.

---

## 📊 Примеры запросов

### Создать пользователя
```bash
curl -X POST http://localhost:24110/api/v1.0/users \
  -H "Content-Type: application/json" \
  -d '{
    "login": "user@example.com",
    "password": "password123",
    "firstname": "Иван",
    "lastname": "Иванов"
  }'
```

### Получить всех пользователей (страница 0, размер 5, сортировка по id desc)
```bash
curl "http://localhost:24110/api/v1.0/users?page=0&size=5&sort=id,desc"
```

### Создать твит
```bash
curl -X POST http://localhost:24110/api/v1.0/tweets \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "title": "Мой твит",
    "content": "Содержание твита"
  }'
```

---

## 🧪 Проверка работы

### 1. Проверьте, что PostgreSQL запущен

```bash
# Docker
docker ps | grep task320-postgres

# Локальный PostgreSQL
psql -U postgres -d distcomp -c "SELECT version();"
```

### 2. Проверьте, что таблицы созданы

```bash
psql -U postgres -d distcomp -c "\dt distcomp.*"
```

Вы должны увидеть:
- distcomp.tbl_user
- distcomp.tbl_tweet
- distcomp.tbl_mark
- distcomp.tbl_note
- distcomp.tbl_tweet_mark

### 3. Проверьте API

```bash
# Получить всех пользователей
curl http://localhost:24110/api/v1.0/users

# Должен вернуться первый пользователь:
{
  "content": [
    {
      "id": 1,
      "login": "nikita.malakhov022@gmail.com",
      "firstname": "Никита",
      "lastname": "Малахов"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1
}
```

---

## ⚙️ Конфигурация

### application.properties

```properties
server.port=24110

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/distcomp
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# Liquibase
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml
spring.liquibase.default-schema=distcomp
```

---

## 🐛 Решение проблем

### Ошибка: "Connection refused"
**Причина:** PostgreSQL не запущен.  
**Решение:** 
```bash
# Docker
docker start task320-postgres

# Локальный (macOS)
brew services start postgresql@15

# Локальный (Linux)
sudo systemctl start postgresql
```

### Ошибка: "Schema distcomp does not exist"
**Решение:**
```bash
docker exec -it task320-postgres psql -U postgres -d distcomp -c "CREATE SCHEMA IF NOT EXISTS distcomp;"
```

### Ошибка: "Authentication failed"
**Причина:** Неправильный пароль.  
**Решение:** Проверьте `application.properties` или установите пароль:
```bash
docker exec -it task320-postgres psql -U postgres -c "ALTER USER postgres WITH PASSWORD 'postgres';"
```

---

## 📁 Структура проекта

```
task320-jpa/
├── pom.xml
├── src/main/
│   ├── java/com/example/task320jpa/
│   │   ├── controller/          # REST контроллеры
│   │   ├── service/             # Бизнес-логика
│   │   ├── repository/          # JPA repositories
│   │   ├── entity/              # JPA Entity
│   │   ├── dto/                 # Request/Response DTO
│   │   ├── mapper/              # MapStruct маппер
│   │   ├── exception/           # Исключения
│   │   └── Task320JpaApplication.java
│   └── resources/
│       ├── application.properties
│       └── db/changelog/        # Liquibase миграции
│           ├── db.changelog-master.xml
│           └── changeset/
└── README.md
```

---

## ✅ Выполнение требований Task320

- ✅ PostgreSQL + JPA/Hibernate
- ✅ Liquibase миграции (XML формат)
- ✅ Префикс таблиц: `tbl_`
- ✅ Схема: `distcomp`
- ✅ Порт: 24110
- ✅ Префикс API: /api/v1.0/
- ✅ Пагинация и сортировка
- ✅ CRUD операции
- ✅ Валидация данных
- ✅ Обработка ошибок
- ✅ Первый пользователь: nikita.malakhov022@gmail.com

---

## 📖 Дополнительная информация

### Остановка PostgreSQL (Docker)
```bash
docker stop task320-postgres
```

### Удаление PostgreSQL (Docker)
```bash
docker rm -f task320-postgres
```

### Просмотр логов приложения
```bash
tail -f logs/spring.log
```

---

**Автор:** Малахов Никита  
**Дата:** Январь 2026  
**Задание:** Task320 - Слой хранения (JPA)
