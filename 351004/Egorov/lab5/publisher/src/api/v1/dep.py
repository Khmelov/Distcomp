import httpx
from fastapi import Depends
from redis.asyncio import Redis
from sqlalchemy.ext.asyncio import AsyncSession

from src.core import get_redis, get_db
from src.core.settings import settings
from src.services import AuthorService, TopicService, NoteService, TagService


def get_author_service(
    session: AsyncSession = Depends(get_db)
) -> AuthorService:
    return AuthorService(session)

def get_topic_service(
        session: AsyncSession = Depends(get_db)
) -> TopicService:
    return TopicService(session)

http_note_client = httpx.AsyncClient(base_url=settings.note_service_url)

def get_note_service(redis: Redis = Depends(get_redis)) -> NoteService:
    return NoteService(http_note_client, redis)

def get_tag_service(
        session: AsyncSession = Depends(get_db)
) -> TagService:
    return TagService(session)