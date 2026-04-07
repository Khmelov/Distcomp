from contextlib import asynccontextmanager

import uvicorn
from fastapi import FastAPI

from app.db.session import dispose_engine, init_engine
from app.exceptions import register_exception_handlers
from app.middleware import ReplayBodyMiddleware, StripTrailingSlashMiddleware
from app.routers import issue_router, notice_router, sticker_router, user_router
from app.settings import settings


@asynccontextmanager
async def lifespan(_: FastAPI):
    if settings.storage == "postgres":
        init_engine()
    yield
    if settings.storage == "postgres":
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
