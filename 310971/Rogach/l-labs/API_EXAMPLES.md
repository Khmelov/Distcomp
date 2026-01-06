# Примеры использования REST API

## 🚀 Способы отправки запросов

### 1. Через терминал (curl)

**Получить всех writers:**
```bash
curl http://localhost:8090/api/v1/writers
```

**Создать нового writer:**
```bash
curl -X POST http://localhost:8090/api/v1/writers \
  -H "Content-Type: application/json" \
  -d '{
    "login": "test@example.com",
    "password": "password123",
    "firstname": "John",
    "lastname": "Doe"
  }'
```

**Получить writer по ID:**
```bash
curl http://localhost:8090/api/v1/writers/1
```

**Обновить writer:**
```bash
curl -X PUT http://localhost:8090/api/v1/writers/1 \
  -H "Content-Type: application/json" \
  -d '{
    "login": "updated@example.com",
    "password": "newpassword123",
    "firstname": "Jane",
    "lastname": "Smith"
  }'
```

**Удалить writer:**
```bash
curl -X DELETE http://localhost:8090/api/v1/writers/1
```

---

### 2. Через браузер (только GET запросы)

Откройте в браузере:
- `http://localhost:8090/api/v1/writers`
- `http://localhost:8090/api/v1/tweets`
- `http://localhost:8090/api/v1/messages`
- `http://localhost:8090/api/v1/labels`

---

### 3. Через Postman / Insomnia / HTTPie

**HTTPie (если установлен):**
```bash
http GET http://localhost:8090/api/v1/writers
http POST http://localhost:8090/api/v1/writers login=test@example.com password=pass123 firstname=John lastname=Doe
```

---

## 📋 Все доступные endpoints

### Writers
- `GET /api/v1/writers` - получить всех
- `GET /api/v1/writers/{id}` - получить по ID
- `POST /api/v1/writers` - создать
- `PUT /api/v1/writers/{id}` - обновить
- `DELETE /api/v1/writers/{id}` - удалить

### Tweets
- `GET /api/v1/tweets` - получить всех (с фильтрацией)
- `GET /api/v1/tweets/{id}` - получить по ID
- `POST /api/v1/tweets` - создать
- `PUT /api/v1/tweets/{id}` - обновить
- `DELETE /api/v1/tweets/{id}` - удалить
- `GET /api/v1/tweets/{id}/writer` - получить writer по tweet ID
- `GET /api/v1/tweets/{id}/labels` - получить labels по tweet ID
- `GET /api/v1/tweets/{id}/messages` - получить messages по tweet ID

### Messages
- `GET /api/v1/messages` - получить всех
- `GET /api/v1/messages/{id}` - получить по ID
- `POST /api/v1/messages` - создать
- `PUT /api/v1/messages/{id}` - обновить
- `DELETE /api/v1/messages/{id}` - удалить

### Labels
- `GET /api/v1/labels` - получить всех
- `GET /api/v1/labels/{id}` - получить по ID
- `POST /api/v1/labels` - создать
- `PUT /api/v1/labels/{id}` - обновить
- `DELETE /api/v1/labels/{id}` - удалить

---

## 🔍 Примеры сложных запросов

**Создать tweet:**
```bash
curl -X POST http://localhost:8090/api/v1/tweets \
  -H "Content-Type: application/json" \
  -d '{
    "writerId": 1,
    "title": "My First Tweet",
    "content": "This is the content of my tweet"
  }'
```

**Фильтрация tweets:**
```bash
curl "http://localhost:8090/api/v1/tweets?writerLogin=test@example.com&title=My"
```

**Создать message:**
```bash
curl -X POST http://localhost:8090/api/v1/messages \
  -H "Content-Type: application/json" \
  -d '{
    "tweetId": 1,
    "content": "This is a comment on the tweet"
  }'
```

**Создать label:**
```bash
curl -X POST http://localhost:8090/api/v1/labels \
  -H "Content-Type: application/json" \
  -d '{
    "name": "technology"
  }'
```



