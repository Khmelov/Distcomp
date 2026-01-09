# Задачи 310 и 320 - Разделение проектов

Проекты разделены на две отдельные задачи:

## Task 310 - REST API (In-Memory)
**Директория:** `task310-rest/`

- In-Memory хранилище (ConcurrentHashMap)
- Простые POJO модели без JPA
- InMemoryCrudRepository
- Без базы данных
- Без Liquibase
- Без TestContainers

## Task 320 - REST API with JPA
**Директория:** `task320-jpa/`

- PostgreSQL через JPA
- JPA сущности с аннотациями
- Spring Data JPA репозитории
- Liquibase миграции
- TestContainers для тестов
- База данных: PostgreSQL (localhost:5432/distcomp)

## Запуск

### Task 310 (In-Memory)
```bash
cd task310-rest
mvn spring-boot:run
```

### Task 320 (JPA)
```bash
cd task320-jpa
mvn spring-boot:run
```

**Важно:** Для Task 320 требуется запущенный PostgreSQL на localhost:5432

## Примечание о компиляции

Если возникают проблемы с компиляцией через Maven (ошибка Lombok), рекомендуется:
1. Запускать через IDE (IntelliJ IDEA или Eclipse)
2. Или использовать Java 17 вместо Java 21

