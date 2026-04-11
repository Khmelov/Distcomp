# Task 340 - Apache Kafka Integration

## Обзор
Интеграция Apache Kafka для асинхронной передачи сущности Reaction между модулями publisher и discussion.

## Архитектура

```
Publisher (localhost:24110)
├── REST API: POST /api/v1.0/reactions
├── Kafka Producer → InTopic
└── Kafka Consumer ← OutTopic

Discussion (localhost:24130)
├── Kafka Consumer ← InTopic
│   ├── Читает реакцию
│   ├── Выполняет модерацию
│   └── Отправляет результат
├── Kafka Producer → OutTopic
└── REST API: /api/v1.0/reactions (для управления)

Kafka (localhost:9092)
├── InTopic (publisher → discussion)
└── OutTopic (discussion → publisher)
```

## Подготовка окружения

### 1. Запуск Kafka с Docker

```bash
# Создать сеть
docker network create kafkanet

# Запустить Zookeeper
docker run -d --network=kafkanet --name=zookeeper \
  -e ZOOKEEPER_CLIENT_PORT=2181 \
  -e ZOOKEEPER_TICK_TIME=2000 \
  -p 2181:2181 \
  confluentinc/cp-zookeeper

# Запустить Kafka
docker run -d --network=kafkanet --name=kafka \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -p 9092:9092 \
  confluentinc/cp-kafka

# Проверить, что Kafka запущена
docker logs kafka
```

### 2. Запуск PostgreSQL

```bash
docker run -d --name postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=distcomp \
  -p 5432:5432 \
  postgres:15
```

### 3. Запуск Cassandra

```bash
docker run -d --name cassandra \
  -e CASSANDRA_DC=datacenter1 \
  -p 9042:9042 \
  cassandra:4
```

## Запуск приложения

### Publisher модуль
```bash
cd publisher
mvn spring-boot:run
# Порт: 24110, контекст: /api/v1.0
```

### Discussion модуль
```bash
cd discussion
mvn spring-boot:run
# Порт: 24130, контекст: /api/v1.0
```

## API Endpoints

### Publisher
- **POST** `/api/v1.0/reactions` - Создать реакцию
  ```json
  {
    "tweetId": 1,
    "content": "Great post!"
  }
  ```
  
- **GET** `/api/v1.0/reactions` - Получить все реакции
- **GET** `/api/v1.0/reactions/{id}` - Получить реакцию по ID
- **PUT** `/api/v1.0/reactions/{id}` - Обновить реакцию
- **DELETE** `/api/v1.0/reactions/{id}` - Удалить реакцию

### Discussion
- **GET** `/api/v1.0/reactions` - Получить все реакции
- **GET** `/api/v1.0/reactions/{id}` - Получить реакцию по ID

## Реакции (состояния)

Реакция проходит следующие состояния:
1. **PENDING** - Создана в publisher, ожидает модерации
2. **APPROVE** - Одобрена в discussion
3. **DECLINE** - Отклонена в discussion (содержит стоп-слова)

## Алгоритм модерации

Discussion модуль сканирует содержимое на наличие стоп-слов:
- `bad`, `hate`, `spam`, `abuse`

Если найден стоп-слово → `DECLINE`, иначе → `APPROVE`

## Тестирование

### 1. Создать твит в Publisher (перед тестированием реакций)
```bash
curl -X POST http://localhost:24110/api/v1.0/tweets \
  -H "Content-Type: application/json" \
  -d '{"title": "Test Tweet", "content": "test content", "authorId": 1}'
```

### 2. Создать реакцию
```bash
curl -X POST http://localhost:24110/api/v1.0/reactions \
  -H "Content-Type: application/json" \
  -d '{
    "tweetId": 1,
    "content": "This is a great reaction!"
  }'
```

Ответ должен содержать `state: "APPROVE"` (положительная модерация)

### 3. Создать реакцию с стоп-словом
```bash
curl -X POST http://localhost:24110/api/v1.0/reactions \
  -H "Content-Type: application/json" \
  -d '{
    "tweetId": 1,
    "content": "This is spam and bad"
  }'
```

Ответ должен содержать `state: "DECLINE"` (отклонено)

### 4. Получить все реакции
```bash
curl http://localhost:24110/api/v1.0/reactions
```

### 5. Получить реакцию в Discussion
```bash
curl http://localhost:24130/api/v1.0/reactions?page=0&size=10
```

## Ключевые компоненты

### Publisher
- `ReactionProducer` - Отправляет сообщения в InTopic
- `ReactionConsumer` - Получает результаты из OutTopic
- `ReactionResponseManager` - Управляет ожиданием ответов (sync over async)
- `ReactionServiceImpl` - Интегрирует Kafka в бизнес-логику

### Discussion
- `ReactionConsumer` - Получает сообщения с InTopic, выполняет модерацию
- `ReactionProducer` - Отправляет результаты в OutTopic
- Алгоритм модерации вымается в consumer

### Kafka Configuration
- `KafkaProducerConfig` - Настройки producer
- `KafkaConsumerConfig` - Настройки consumer с ConcurrentKafkaListenerContainerFactory
- Используется JSON serialization/deserialization

## Особенности реализации

1. **Partition Key** - Используется `tweetId` как ключ партиции,  все реакции для одного твита гарантированно попадают в одну partition

2. **Async Response** - Используется `ReactionResponseManager` с `CountDownLatch` для синхронного ожидания асинхронного Kafka ответа с таймаутом 1 сек

3. **State Management** - Состояние Reaction сохраняется в БД и отправляется через Kafka

4. **Error Handling** - Включена обработка ошибок, повторные попытки и сжатие сообщений

## Логирование

Оба модуля логируют вся операции Kafka:
- Отправка сообщений
- Получение сообщений
- Ошибки и таймауты
- Изменение состояний

Логи можно смотреть в консоли при запуске приложения.

## Проблемы и решения

### Kafka не подключается
- Проверьте что Kafka запущена: `docker ps`
- Проверьте порт 9092: `netstat -an | grep 9092`

### Таймаут при создании реакции
- Проверьте что discussion модуль запущен
- Проверьте логи consumer в discussion модуле
- Увеличьте таймаут в `ReactionServiceImpl.create()`

### Реакции не сохраняются в Discussion
- Проверьте что Cassandra запущена и доступна
- Проверьте логи discussion модуля
- Убедитесь что keyspace `distcomp` существует

## Расширения для продакшена (опционально)

1. **Performance** - Добавить кеширование (Redis) для быстрого доступа
2. **Monitoring** - Интегрировать Prometheus + Grafana
3. **Testcontainers** - Настроить интеграционные тесты с embedded Kafka
4. **Error Queue** - Реализовать обработку failed messages в отдельной queue
