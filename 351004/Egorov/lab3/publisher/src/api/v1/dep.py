import httpx
from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from src.core.database import get_db
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

def get_note_service() -> NoteService:
    return NoteService(http_note_client)

def get_tag_service(
        session: AsyncSession = Depends(get_db)
) -> TagService:
    return TagService(session)