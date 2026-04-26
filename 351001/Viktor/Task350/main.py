import asyncio
from fastapi import FastAPI
from sqlalchemy import text
from aiokafka.admin import AIOKafkaAdminClient, NewTopic

from Task350.publisher.src.api.v1 import router_v1
from Task350.publisher.src.core.errors import register_error_handlers
from Task350.publisher.src.infrastructure.database import engine, Base
from Task350.publisher.src.infrastructure.redis_client import RedisClient
from Task350.publisher.src.services.kafka_producer import KafkaProducerService
from Task350.publisher.src.services.kafka_consumer import KafkaConsumerService
from Task350.publisher.src.services.post import PostService

app = FastAPI(title="DistComp", version="1.0")

# Глобальные переменные для Kafka (чтобы были доступны в dep.py)
producer = None
consumer = None

# --- Инициализация PostgreSQL ---
@app.on_event("startup")
async def init_db():
    async with engine.begin() as conn:
        await conn.execute(text("CREATE SCHEMA IF NOT EXISTS distcomp"))
        await conn.execute(text("ALTER USER postgres SET search_path TO distcomp, public"))
        await conn.run_sync(Base.metadata.create_all)

# --- Создание топиков Kafka ---
async def create_topics():
    admin = AIOKafkaAdminClient(bootstrap_servers='127.0.0.1:9092')
    await admin.start()
    topics = [
        NewTopic(name="InTopic", num_partitions=3, replication_factor=1),
        NewTopic(name="OutTopic", num_partitions=3, replication_factor=1)
    ]
    await admin.create_topics(new_topics=topics, validate_only=False)
    await admin.close()

# --- Инициализация Kafka и Redis ---
@app.on_event("startup")
async def startup_kafka():
    global producer, consumer
    await create_topics()
    producer = KafkaProducerService()
    consumer = KafkaConsumerService()
    await producer.start()
    await consumer.start()
    redis_client = RedisClient()
    post_service = PostService(producer, consumer, redis_client)
    asyncio.create_task(consumer.get_responses(post_service.pending_requests))

@app.on_event("shutdown")
async def shutdown_kafka():
    global producer, consumer
    if producer:
        await producer.stop()
    if consumer:
        await consumer.stop()

# --- Регистрация маршрутов ---
register_error_handlers(app)
app.include_router(router_v1, prefix="/api")

if __name__ == "__main__":
    import hypercorn.asyncio
    from hypercorn.config import Config
    config = Config()
    config.bind = ["127.0.0.1:24110"]
    config.use_reloader = True
    asyncio.run(hypercorn.asyncio.serve(app, config))