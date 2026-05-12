from contextlib import asynccontextmanager
from collections.abc import AsyncIterator

import uvicorn
from fastapi import FastAPI

from sqlalchemy import text as sa_text

import models
from database import engine
from kafka_handler import kafka_handler
from redis_cache import close_redis
from routers.tweet import router as tweet_router
from routers.writer import router as writer_router
from routers.comment import router as comment_router
from routers.sticker import router as sticker_router
from routers.v2_auth import router as v2_auth_router
from routers.v2_writer import router as v2_writer_router
from routers.v2_tweet import router as v2_tweet_router
from routers.v2_sticker import router as v2_sticker_router
from routers.v2_comment import router as v2_comment_router


async def init_models() -> None:
    async with engine.begin() as conn:
        await conn.run_sync(models.Base.metadata.create_all)
        # Add role column to tbl_writer if it doesn't exist
        await conn.execute(
            sa_text(
                "ALTER TABLE tbl_writer ADD COLUMN IF NOT EXISTS role VARCHAR(16) DEFAULT 'CUSTOMER'"
            )
        )
        # Widen password column for BCrypt hashes
        await conn.execute(
            sa_text(
                "ALTER TABLE tbl_writer ALTER COLUMN password TYPE VARCHAR(256)"
            )
        )


@asynccontextmanager
async def life_span(app: FastAPI) -> AsyncIterator[None]:
    await init_models()
    await kafka_handler.start()
    yield
    await kafka_handler.stop()
    await close_redis()


app = FastAPI(lifespan=life_span)

app.include_router(router=tweet_router)
app.include_router(router=writer_router)
app.include_router(router=comment_router)
app.include_router(router=sticker_router)
app.include_router(router=v2_auth_router)
app.include_router(router=v2_writer_router)
app.include_router(router=v2_tweet_router)
app.include_router(router=v2_sticker_router)
app.include_router(router=v2_comment_router)

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=24110)
