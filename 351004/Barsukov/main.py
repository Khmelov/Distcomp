import uvicorn
from fastapi import FastAPI
from contextlib import asynccontextmanager
from api.v1.router import api_router
from kafka_producer import kafka_producer
from kafka_consumer import kafka_consumer_publisher
from redis_config import redis_client

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    print("Starting Publisher service with Kafka...")
    await kafka_producer.start()
    await kafka_consumer_publisher.start()
    await redis_client.ping()
    print("Kafka and Redis started")
    yield
    # Shutdown
    await kafka_producer.stop()
    await kafka_consumer_publisher.stop()
    await redis_client.close()
    print("Publisher service stopped")

app = FastAPI(lifespan=lifespan)

app.include_router(api_router, prefix="/api/v1.0")

if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=24110)
