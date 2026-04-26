import asyncio
from fastapi import FastAPI
from Task350.discussion.api.v1.endpoints import post
from Task350.discussion.services.kafka_consumer import DiscussionKafkaConsumer
from Task350.discussion.services.kafka_producer import KafkaProducerService
from Task350.discussion.services.post_service import PostService
from Task350.discussion.infrastructure.database import cassandra_db
from Task350.discussion.domain.repositories.post_repository import CassandraPostRepository

app = FastAPI(title="Discussion Service", version="1.0")

# Глобальные переменные для Kafka-компонентов (будут инициализированы в startup)
producer = None
consumer = None


@app.on_event("startup")
async def startup():
    global producer, consumer

    # 1. Инициализация репозитория и сервиса
    post_repo = CassandraPostRepository(cassandra_db.get_session())
    post_service = PostService(post_repo)

    # 2. Создание и запуск Kafka продюсера и консьюмера
    producer = KafkaProducerService()
    consumer = DiscussionKafkaConsumer(post_service, producer)

    await producer.start()
    await consumer.start()


@app.on_event("shutdown")
async def shutdown():
    global producer, consumer
    if producer:
        await producer.stop()
    if consumer and hasattr(consumer, 'stop'):
        await consumer.stop()
    cassandra_db.close()


# Подключаем роутеры
app.include_router(post.router, prefix="/api/v1.0")

if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="127.0.0.1", port=24130)