# Task 320 - REST API with JPA

Проект для задачи 320 - REST API с JPA и PostgreSQL.

## Особенности

- **Хранилище**: PostgreSQL через JPA
- **База данных**: PostgreSQL (localhost:5432/distcomp)
- **Миграции**: Liquibase
- **Репозитории**: Spring Data JPA
- **Модели**: JPA сущности с аннотациями

## Структура

- `model/` - JPA сущности (Writer, Tweet, Label, Message, TweetLabel)
- `repository/` - Spring Data JPA репозитории
- `service/` - бизнес-логика
- `controller/` - REST контроллеры
- `dto/` - объекты передачи данных
- `mapper/` - MapStruct мапперы
- `resources/db/changelog/` - Liquibase миграции

## Требования

- PostgreSQL запущен на localhost:5432
- База данных `distcomp` создана
- Пользователь: postgres, пароль: postgres

## Запуск

```bash
mvn spring-boot:run
```

Приложение будет доступно на `http://localhost:24110`

## API Endpoints

- `/api/v1.0/writers` - управление писателями
- `/api/v1.0/tweets` - управление твитами
- `/api/v1.0/labels` - управление метками
- `/api/v1.0/messages` - управление сообщениями

## Тестирование

```bash
mvn test
```

Тесты используют TestContainers для изоляции базы данных.

