# Distcomp Labs: Kafka, Redis, Security

Проект реализован на Java 21, Spring Boot, Maven. Приложение запускает два REST-модуля:

- `publisher` на `localhost:24110`
- `discussion` на `localhost:24130`

Хранилища и инфраструктура поднимаются через Docker Compose:

- PostgreSQL: `localhost:5432`, база `distcomp`
- Cassandra: `localhost:9042`, keyspace `distcomp`
- Kafka: `localhost:9092`
- Redis: `localhost:6379`

## Kafka

Kafka добавлена для передачи операций с `Comment` между `publisher` и `discussion`.

Что сделано:

- Добавлена зависимость `spring-kafka` в `pom.xml`.
- В `docker-compose.yml` добавлены сервисы:
  - `zookeeper`
  - `kafka`
- В `application.yml` настроены:
  - `spring.kafka.bootstrap-servers`
  - topic `InTopic`
  - topic `OutTopic`
- Созданы классы обмена сообщениями:
  - `CommentKafkaRequest`
  - `CommentKafkaResponse`
  - `CommentOperation`
- В `publisher` класс `CommentProxyService` отправляет запросы по комментариям в `InTopic`.
- В `discussion` класс `DiscussionCommentKafkaHandler` читает `InTopic`, выполняет операцию с Cassandra и отправляет ответ в `OutTopic`.
- Для сообщений Kafka используется ключ `issueId`, чтобы комментарии одной issue попадали в одну partition.
- REST `localhost:24130/api/v1.0/comments` не отключен и продолжает работать.

Основные файлы:

- `src/main/java/by/bsuir/distcomp/service/CommentProxyService.java`
- `src/main/java/by/bsuir/discussion/kafka/DiscussionCommentKafkaHandler.java`
- `src/main/java/by/bsuir/distcomp/kafka/*`
- `src/main/resources/application.yml`
- `docker-compose.yml`

## Redis

Redis добавлен как общий кеш для `publisher`.

Что сделано:

- Добавлена зависимость `spring-boot-starter-data-redis` в `pom.xml`.
- В `docker-compose.yml` добавлен сервис `redis`.
- В `application.yml` добавлены настройки:
  - `spring.data.redis.host`
  - `spring.data.redis.port`
- Созданы классы:
  - `RedisConfig`
  - `RedisCacheService`
- Кеширование добавлено в сервисы:
  - `WriterService`
  - `IssueService`
  - `TagService`
  - `CommentProxyService`
- Для `GET by id` используется кеш по ключам вида:
  - `writer:{id}`
  - `issue:{id}`
  - `tag:{id}`
  - `comment:{id}`
- Для списков используется кеш вида:
  - `writer:list:{page}:{size}`
  - `issue:list:{page}:{size}`
  - `tag:list:{page}:{size}`
  - `comment:list:{page}:{size}`
- После `create`, `update`, `delete` кеш соответствующей сущности инвалидируется.
- Если Redis недоступен, приложение продолжает работать через основное хранилище.

Основные файлы:

- `src/main/java/by/bsuir/distcomp/cache/RedisConfig.java`
- `src/main/java/by/bsuir/distcomp/cache/RedisCacheService.java`
- `src/main/java/by/bsuir/distcomp/service/WriterService.java`
- `src/main/java/by/bsuir/distcomp/service/IssueService.java`
- `src/main/java/by/bsuir/distcomp/service/TagService.java`
- `src/main/java/by/bsuir/distcomp/service/CommentProxyService.java`

## Security

Security добавлена для новой версии API `/api/v2.0/**`.

Что сделано:

- Добавлена зависимость `spring-boot-starter-security` в `pom.xml`.
- `/api/v1.0/**` оставлен открытым без авторизации.
- `/api/v2.0/**` защищен JWT.
- Добавлены роли:
  - `ADMIN`
  - `CUSTOMER`
- В `Writer` добавлено поле `role`.
- В Liquibase добавлен changeset `003-add-writer-role`.
- Пароль при регистрации через `/api/v2.0/writers` сохраняется в BCrypt.
- Login выполняется через:
  - `POST /api/v2.0/login`
- Ответ login содержит:
  - `access_token`
  - `token_type`
- JWT содержит:
  - `sub`
  - `iat`
  - `exp`
  - `role`
- Для запросов к защищенным ресурсам используется заголовок:

```http
Authorization: Bearer <access_token>
```

Правила доступа:

- `ADMIN` имеет доступ ко всем операциям.
- `CUSTOMER` может читать данные.
- `CUSTOMER` может изменять свои данные: профиль, свои issues и comments к своим issues.
- Для `Tag` операции записи доступны только `ADMIN`.

Основные файлы:

- `src/main/java/by/bsuir/distcomp/security/SecurityConfig.java`
- `src/main/java/by/bsuir/distcomp/security/JwtService.java`
- `src/main/java/by/bsuir/distcomp/security/JwtAuthenticationFilter.java`
- `src/main/java/by/bsuir/distcomp/security/AuthService.java`
- `src/main/java/by/bsuir/distcomp/security/AuthorizationService.java`
- `src/main/java/by/bsuir/distcomp/controller/v2/*`
- `src/main/java/by/bsuir/distcomp/model/UserRole.java`
- `src/main/java/by/bsuir/distcomp/model/Writer.java`

## Запуск

```powershell
docker compose up -d --build
```

Проверка контейнеров:

```powershell
docker compose ps
```

Проверка открытого API:

```powershell
Invoke-WebRequest http://localhost:24110/api/v1.0/writers
```

Проверка защищенного API без токена должна вернуть `401`:

```powershell
Invoke-WebRequest http://localhost:24110/api/v2.0/writers
```

## Пример Security Flow

Регистрация:

```http
POST /api/v2.0/writers
Content-Type: application/json

{
  "login": "user@mail.ru",
  "password": "password123",
  "firstName": "Dmitry",
  "lastName": "Loginov",
  "role": "CUSTOMER"
}
```

Login:

```http
POST /api/v2.0/login
Content-Type: application/json

{
  "login": "user@mail.ru",
  "password": "password123"
}
```

Защищенный запрос:

```http
GET /api/v2.0/writers/me
Authorization: Bearer <access_token>
```

## Сборка

```powershell
mvn -DskipTests package
```

Тесты:

```powershell
mvn test
```
