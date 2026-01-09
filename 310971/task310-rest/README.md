# Task 310 - REST API (In-Memory)

Проект для задачи 310 - REST API с in-memory хранилищем данных.

## Особенности

- **Хранилище**: In-Memory (ConcurrentHashMap)
- **База данных**: Не требуется
- **Репозитории**: InMemoryCrudRepository
- **Модели**: Простые POJO классы без JPA аннотаций

## Структура

- `model/` - простые POJO модели (Writer, Tweet, Label, Message)
- `repository/` - InMemoryCrudRepository и его реализации
- `service/` - бизнес-логика
- `controller/` - REST контроллеры
- `dto/` - объекты передачи данных
- `mapper/` - MapStruct мапперы

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

