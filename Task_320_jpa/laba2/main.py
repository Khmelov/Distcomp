from fastapi import Depends, FastAPI
from db_helper import db_helper
from contextlib import asynccontextmanager
from models import Base
from api_v1 import router as api_v1_router
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import Column, Integer, String
from sqlalchemy.orm import declarative_base

# Base = declarative_base()

# class Writer(Base):
#     __tablename__ = 'writers'

#     id = Column(Integer, primary_key=True, index=True)
#     name = Column(String, index=True)

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
    title = "Laba 2 service", 
)




# @app.post("/writers/")
# async def create_writer(name: str, 
#                       db: AsyncSession = Depends(db_helper.session_dependency)):
#     new_writer = Writer(name=name)
#     db.add(new_writer)
#     await db.commit()
#     await db.refresh(new_writer)
#     return new_writer

# @app.get("/writers/")
# async def read_writers(skip: int = 0, limit: int = 10, db: AsyncSession = Depends(db_helper.session_dependency)):
#     result = await db.execute("SELECT * FROM writers LIMIT :limit OFFSET :skip", {"limit": limit, "skip": skip})
#     writers = result.scalars().all()
#     return writers


app.include_router(api_v1_router, prefix="/api")