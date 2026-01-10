import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI
from app.api import api_router
from app.core.cassandra import cassandra_config
from app.services.kafka_service import DiscussionKafkaService

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

kafka_service = None

@asynccontextmanager
async def lifespan(app: FastAPI):
    global kafka_service
    logger.info("Starting Discussion microservice...")
    
    if cassandra_config.connect():
        logger.info("Connected to Cassandra successfully")
    else:
        logger.error("Failed to connect to Cassandra")
    
    try:
        kafka_service = DiscussionKafkaService()
        logger.info("Kafka service started successfully")
    except Exception as e:
        logger.error(f"Failed to start Kafka service: {e}")
    
    yield
    
    logger.info("Shutting down Discussion microservice...")
    
    if kafka_service:
        kafka_service.close()
    
    cassandra_config.disconnect()

app = FastAPI(
    title="Discussion Microservice",
    description="Microservice for managing notes with Cassandra",
    version="1.0.0",
    lifespan=lifespan
)


class BufferBodyASGIMiddleware:
    def __init__(self, app):
        self.app = app

    async def __call__(self, scope, receive, send):
        if scope.get("type") != "http":
            return await self.app(scope, receive, send)

        body_chunks = []
        more_body = True
        while more_body:
            message = await receive()
            if message.get("type") != "http.request":
                continue
            chunk = message.get("body", b"")
            if chunk:
                body_chunks.append(chunk)
            more_body = message.get("more_body", False)

        body = b"".join(body_chunks)

        async def receive_replay():
            nonlocal body
            chunk = body
            body = b""
            return {"type": "http.request", "body": chunk, "more_body": False}

        return await self.app(scope, receive_replay, send)


app.include_router(api_router)

asgi_app = BufferBodyASGIMiddleware(app)

@app.get("/")
async def root():
    return {
        "message": "Discussion Microservice",
        "version": "1.0.0",
        "database": "Cassandra",
        "status": "running"
    }

if __name__ == "__main__":
    import uvicorn
    
    uvicorn.run(
        app,
        host="0.0.0.0",
        port=24130,
        loop="asyncio",
        http="h11",
        reload=False,
        log_level="info"
    )
