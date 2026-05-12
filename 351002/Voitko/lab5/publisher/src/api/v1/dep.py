import httpx
from fastapi import Depends
from redis.asyncio import Redis
from sqlalchemy.ext.asyncio import AsyncSession

from src.core import get_redis, get_db
from src.core.settings import settings
from src.services import WriterService, NewsService, NoteService, LabelService


def get_writer_service(
    session: AsyncSession = Depends(get_db)
) -> WriterService:
    return WriterService(session)

def get_news_service(
        session: AsyncSession = Depends(get_db)
) -> NewsService:
    return NewsService(session)

http_note_client = httpx.AsyncClient(base_url=settings.note_service_url)

def get_note_service(redis: Redis = Depends(get_redis)) -> NoteService:
    return NoteService(http_note_client, redis)

def get_label_service(
        session: AsyncSession = Depends(get_db)
) -> LabelService:
    return LabelService(session)