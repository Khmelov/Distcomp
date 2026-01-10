import logging
from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError
from starlette.exceptions import HTTPException as StarletteHTTPException
from app.exceptions.handlers import http_exception_handler, validation_exception_handler
from app.api.v1.api import api_router
from app.services.kafka_service import PublisherKafkaService

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

kafka_service = None

@asynccontextmanager
async def lifespan(app: FastAPI):
    global kafka_service
    logger.info("Starting Publisher microservice...")
    
    try:
        kafka_service = PublisherKafkaService()
        logger.info("Kafka service started successfully")
    except Exception as e:
        logger.error(f"Failed to start Kafka service: {e}")
    
    yield
    
    logger.info("Shutting down Publisher microservice...")
    
    if kafka_service:
        kafka_service.close()

app = FastAPI(title="REST API on Python", version="1.0.0", lifespan=lifespan)

app.add_exception_handler(StarletteHTTPException, http_exception_handler)
app.add_exception_handler(RequestValidationError, validation_exception_handler)

app.include_router(api_router, prefix="/api/v1.0")

if __name__ == "__main__":
    import uvicorn
    from app.core.config import APP_HOST, APP_PORT
    uvicorn.run("app.main:app", host=APP_HOST, port=APP_PORT, reload=False)