from contextlib import asynccontextmanager

from fastapi import FastAPI

from src.api.v1.router import router_v1
from src.core.database import init_db, close_db
from src.core.handlers import register_error_handlers


@asynccontextmanager
async def lifespan(_: FastAPI):
    await init_db()
    yield
    await close_db()

app = FastAPI(title="DistComp_discussion", version="1.0", lifespan=lifespan)

register_error_handlers(app)
app.include_router(router_v1, prefix="/api")