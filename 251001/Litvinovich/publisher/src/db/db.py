import os

from sqlalchemy.ext.asyncio import create_async_engine, async_sessionmaker
from sqlalchemy.orm import DeclarativeBase

DATABASE_URL: str = os.getenv("DATABASE_URL")

engine = create_async_engine(url = DATABASE_URL, echo = True)
async_session_maker = async_sessionmaker(engine, expire_on_commit = False)

class Base(DeclarativeBase):
    pass
