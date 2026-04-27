import logging
from fastapi import FastAPI
from app.api.v1 import users, topics, marks, comments
from app.api.v2 import users as v2_users, topics as v2_topics
from app.api.v2 import marks as v2_marks, comments as v2_comments
from app.api.v2 import auth as v2_auth
from app.core.error_handler import register_exception_handlers
from app.db.database import Base, engine
from app.core.config import settings
from app.models import user, topic, mark
from app.kafka import manager as kafka
from app.cache.redis_client import get_redis

logger = logging.getLogger(__name__)

Base.metadata.create_all(bind=engine)

app = FastAPI(
    title=settings.APP_NAME,
    version=settings.APP_VERSION,
    debug=settings.DEBUG
)

# v1 
app.include_router(users.router, prefix="/api/v1.0")
app.include_router(topics.router, prefix="/api/v1.0")
app.include_router(marks.router, prefix="/api/v1.0")
app.include_router(comments.router, prefix="/api/v1.0")

# v2 
app.include_router(v2_auth.router, prefix="/api/v2.0")
app.include_router(v2_users.router, prefix="/api/v2.0")
app.include_router(v2_topics.router, prefix="/api/v2.0")
app.include_router(v2_marks.router, prefix="/api/v2.0")
app.include_router(v2_comments.router, prefix="/api/v2.0")

register_exception_handlers(app)


@app.on_event("startup")
async def startup_event():
    kafka.init_kafka(settings.KAFKA_BOOTSTRAP_SERVERS)
    try:
        get_redis().ping()
        logger.info("Redis connected successfully")
    except Exception as e:
        logger.warning(f"Redis not available at startup: {e}")


@app.get("/")
async def root():
    return {
        "message": settings.APP_NAME,
        "version": settings.APP_VERSION,
        "database": settings.POSTGRES_DB,
        "debug": settings.DEBUG
    }


@app.get("/health")
async def health_check():
    return {"status": "healthy"}
