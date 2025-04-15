import asyncio
import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI, Request
from src.api.v1 import creators, stories, reactions, markers
from src.db.db import engine, Base
from src.models.models import Creator, Story, Reaction, Marker

from aiokafka.admin import AIOKafkaAdminClient, NewTopic
from src.utils.kafka_client import kafka_response_consumer


async def create_topics(bootstrap_servers: str):
    admin_client = AIOKafkaAdminClient(bootstrap_servers=bootstrap_servers)
    await admin_client.start()
    try:
        topics = [
            NewTopic(name="InTopic", num_partitions=3, replication_factor=1),
            NewTopic(name="OutTopic", num_partitions=3, replication_factor=1)
        ]
        await admin_client.create_topics(new_topics=topics, validate_only=False)
        logging.info("Топики успешно созданы")
    except Exception as e:
        logging.info(f"Ошибка при создании топиков: {e}")
    finally:
        await admin_client.close()


@asynccontextmanager
async def lifespan(app: FastAPI):
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.drop_all)
        await conn.run_sync(Base.metadata.create_all)

    await create_topics(bootstrap_servers="broker:29092")
    asyncio.create_task(kafka_response_consumer())

    yield

app = FastAPI(lifespan=lifespan)

app.include_router(creators.router, prefix="/api/v1.0", tags=["Creators"])
app.include_router(stories.router, prefix="/api/v1.0", tags=["Stories"])
app.include_router(reactions.router, prefix="/api/v1.0", tags=["Reactions"])
app.include_router(markers.router, prefix="/api/v1.0", tags=["Markers"])

logging.basicConfig(level=logging.INFO)

@app.middleware("http")
async def log_requests(request: Request, call_next):
    body_bytes = await request.body()
    body_str = body_bytes.decode("utf-8")
    logging.info(f"Incoming request {request.method} {request.url.path} with body: {body_str}")

    async def receive() -> dict:
        return {"type": "http.request", "body": body_bytes}
    request._receive = receive

    response = await call_next(request)
    return response


