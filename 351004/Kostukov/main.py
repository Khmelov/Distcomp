from contextlib import asynccontextmanager

from fastapi import FastAPI
import uvicorn

from publisher.app.api.endpoints.writers import router as writers_router
from publisher.app.api.endpoints.articles import router as articles_router
from publisher.app.api.endpoints.markers import router as markers_router
from publisher.app.api.endpoints.notes import router as notes_router
from publisher.app.core.exceptions import AppError, app_error_handler
from publisher.app.services.note_service_kafka import service as note_service
from publisher.app.infrastructure.cache.redis_client import RedisClient
from publisher.app.api.endpoints.writers_v2 import router as writers_v2_router


@asynccontextmanager
async def lifespan(app: FastAPI):
    await note_service.start()
    app.state.redis = RedisClient()
    note_service.redis = app.state.redis
    try:
        yield
    finally:
        await note_service.stop()
        await app.state.redis.close()
        await note_service.redis.close()


app = FastAPI(lifespan=lifespan)
app.add_exception_handler(AppError, app_error_handler)
app.include_router(writers_router)
app.include_router(articles_router)
app.include_router(markers_router)
app.include_router(notes_router)
app.include_router(writers_v2_router)


if __name__ == "__main__":
    uvicorn.run("main:app", reload=True, host="0.0.0.0", port=24110, http="h11")