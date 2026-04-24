from contextlib import asynccontextmanager

import uvicorn
from fastapi import FastAPI

from app.cache.client import RedisCacheClient
from app.db.session import dispose_engine, init_engine
from app.exceptions import register_exception_handlers
from app.middleware import ReplayBodyMiddleware, StripTrailingSlashMiddleware
from app.routers import issue_router, notice_router, sticker_router, user_router
from app.settings import settings


@asynccontextmanager
async def lifespan(_: FastAPI):
    cache_client: RedisCacheClient | None = None
    if settings.redis_enabled:
        cache_client = RedisCacheClient(
            host=settings.redis_host,
            port=settings.redis_port,
            db=settings.redis_db,
            ttl_seconds=settings.redis_ttl_seconds,
        )
        if not cache_client.ping():
            cache_client = None
    from app.services.dependencies import set_cache_client

    set_cache_client(cache_client)
    if settings.storage == "postgres":
        init_engine()
        from app.services.dependencies import start_publisher_kafka_transport

        start_publisher_kafka_transport()
    yield
    from app.services.dependencies import set_cache_client

    set_cache_client(None)
    if cache_client is not None:
        cache_client.close()
    if settings.storage == "postgres":
        from app.services.dependencies import shutdown_publisher_kafka_transport

        shutdown_publisher_kafka_transport()
        dispose_engine()


app = FastAPI(title="Task310 REST API", version="1.0", lifespan=lifespan)
app.add_middleware(StripTrailingSlashMiddleware)
app.add_middleware(ReplayBodyMiddleware)

app.include_router(user_router)
app.include_router(issue_router)
app.include_router(sticker_router)
app.include_router(notice_router)

register_exception_handlers(app)


if __name__ == "__main__":
    uvicorn.run(
        "app.main:app",
        host="localhost",
        port=24110,
        reload=False,
        http="h11",
    )
