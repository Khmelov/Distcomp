from contextlib import asynccontextmanager
from collections.abc import AsyncIterator

import uvicorn
from fastapi import FastAPI

import models
from database import engine
from routers.tweet import router as tweet_router
from routers.writer import router as writer_router
from routers.comment import router as comment_router
from routers.sticker import router as sticker_router


async def init_models() -> None:
    async with engine.begin() as conn:
        await conn.run_sync(models.Base.metadata.create_all)

@asynccontextmanager
async def life_span(app: FastAPI) -> AsyncIterator[None]:
    await init_models()
    yield

app = FastAPI(lifespan=life_span)

app.include_router(router=tweet_router)
app.include_router(router=writer_router)
app.include_router(router=comment_router)
app.include_router(router=sticker_router)

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=24110)
