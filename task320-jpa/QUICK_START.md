# 🚀 Быстрый старт Task320

## ⚡ САМЫЙ ПРОСТОЙ СПОСОБ (3 команды!)

### Шаг 1: Запустите PostgreSQL
```bash
docker-compose up -d
```

Это запустит PostgreSQL в Docker с правильными настройками.

### Шаг 2: Дождитесь готовности БД (5-10 секунд)
```bash
docker-compose ps
```

Статус должен быть "healthy".

### Шаг 3: Запустите приложение
```bash
./mvnw spring-boot:run
```

**Готово!** Приложение запущено на http://localhost:24110

---

## ✅ Проверка работы

```bash
# Получить всех пользователей
curl http://localhost:24110/api/v1.0/users
```

Ожидаемый ответ:
```json
{
  "content": [
    {
      "id": 1,
      "login": "nikita.malakhov022@gmail.com",
      "firstname": "Никита",
      "lastname": "Малахов"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1
}
```

---

## 📋 Что делает docker-compose?

1. ✅ Загружает PostgreSQL 15
2. ✅ Создает базу данных `distcomp`
3. ✅ Создает схему `distcomp`
4. ✅ Настраивает пользователя `postgres` с паролем `postgres`
5. ✅ Открывает порт `5432`

---

## 🛑 Остановка

```bash
# Остановить всё
docker-compose down

# Остановить и удалить данные
docker-compose down -v
```

---

## 🔧 Альтернативные способы

### Если нет Docker

См. полную инструкцию в `README.md` - раздел "Вариант 2: С локальным PostgreSQL"

### Если нет Maven

```bash
# Используйте Maven Wrapper (уже включен)
./mvnw spring-boot:run
```

### Если используете VS Code

1. Откройте проект в VS Code
2. Установите "Extension Pack for Java"
3. Запустите `Task320JpaApplication.java` (правая кнопка → Run)

---

## 🎯 Основные endpoints

```bash
# Пользователи (с пагинацией)
GET    http://localhost:24110/api/v1.0/users?page=0&size=10&sort=id,desc
POST   http://localhost:24110/api/v1.0/users
GET    http://localhost:24110/api/v1.0/users/{id}
PUT    http://localhost:24110/api/v1.0/users/{id}
PATCH  http://localhost:24110/api/v1.0/users/{id}
DELETE http://localhost:24110/api/v1.0/users/{id}

# Твиты
GET    http://localhost:24110/api/v1.0/tweets?page=0&size=10
POST   http://localhost:24110/api/v1.0/tweets
...

# Метки
GET    http://localhost:24110/api/v1.0/marks?page=0&size=10
POST   http://localhost:24110/api/v1.0/marks
...

# Заметки
GET    http://localhost:24110/api/v1.0/notes?page=0&size=10
POST   http://localhost:24110/api/v1.0/notes
...
```

---

## 📊 Пример: Создание твита

```bash
curl -X POST http://localhost:24110/api/v1.0/tweets \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "title": "Первый твит",
    "content": "Это мой первый твит с PostgreSQL!"
  }'
```

Ответ (201 Created):
```json
{
  "id": 1,
  "userId": 1,
  "title": "Первый твит",
  "content": "Это мой первый твит с PostgreSQL!"
}
```

---

## 🗄️ Просмотр базы данных

```bash
# Подключитесь к PostgreSQL
docker exec -it task320-postgres psql -U postgres -d distcomp

# Список таблиц
\dt distcomp.*

# Просмотр данных
SELECT * FROM distcomp.tbl_user;

# Выход
\q
```

---

## ✅ Чек-лист запуска

- [ ] Docker установлен и запущен
- [ ] Выполнена команда `docker-compose up -d`
- [ ] PostgreSQL запущен (проверить: `docker-compose ps`)
- [ ] Выполнена команда `./mvnw spring-boot:run`
- [ ] Приложение запустилось без ошибок
- [ ] API отвечает: `curl http://localhost:24110/api/v1.0/users`

---

**Все работает? Отлично! Теперь смотрите README.md для подробной документации.**

**Есть проблемы? Смотрите раздел "🐛 Решение проблем" в README.md**
