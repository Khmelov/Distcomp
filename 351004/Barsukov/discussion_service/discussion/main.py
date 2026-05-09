import uvicorn
from fastapi import FastAPI
from contextlib import asynccontextmanager
from discussion.api.v1.router import api_router
from discussion.db.thecassandra import cassandra_client
from discussion.core.config import settings
from discussion.kafka_consumer import KafkaConsumerDiscussion
from discussion.kafka_producer import kafka_producer


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    print("Starting discussion service...")
    cassandra_client.connect()
    print(f"Connected to Cassandra at {settings.CASSANDRA_HOSTS}:{settings.CASSANDRA_PORT}")

    # Запускаем Kafka
    await kafka_producer.start()
    kafka_consumer = KafkaConsumerDiscussion()
    await kafka_consumer.start()
    print("Kafka consumer started")

    yield

    # Shutdown
    await kafka_producer.stop()
    print("Discussion service stopped")


app = FastAPI(
    title="Discussion Service",
    description="Microservice for managing notes with Cassandra",
    version="1.0.0",
    lifespan=lifespan
)

app.include_router(api_router)

if __name__ == "__main__":
    uvicorn.run(
        "discussion.main:app",
        host="127.0.0.1",
        port=24130,
        reload=True
    )