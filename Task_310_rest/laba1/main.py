
from fastapi import FastAPI
from db_helper import db_helper
from contextlib import asynccontextmanager
from models import Base
from api_v1 import router as api_v1_router


from loguru import logger
import os
from pathlib import Path

#  папку для логов
log_path = Path("logs")
log_path.mkdir(exist_ok=True)

logger.add(log_path / "app.log", rotation="1 MB", catch=True)


import sys
# Настройте логгер с обработкой ошибок
#logger.remove()  # Удаляем дефолтный обработчик
#logger.add(sys.stderr, format="<green>{time:YYYY-MM-DD HH:mm:ss}</green> | <level>{level: <8}</level> | <cyan>{name}</cyan>:<cyan>{function}</cyan>:<cyan>{line}</cyan> - <level>{message}</level>", catch=True)


@asynccontextmanager
async def lifespan(app: FastAPI):
    async with db_helper.engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    yield

app = FastAPI(
    lifespan=lifespan,
    title = "Laba 1 service", 
)



app.include_router(api_v1_router, prefix="/api")