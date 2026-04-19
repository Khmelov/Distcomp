from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from src.core.database import get_db
from src.services import AuthorService, TopicService, NoteService, TagService


def get_author_service(
    session: AsyncSession = Depends(get_db)
) -> AuthorService:
    return AuthorService(session)

def get_topic_service(
        session: AsyncSession = Depends(get_db)
) -> TopicService:
    return TopicService(session)

def get_note_service(
        session: AsyncSession = Depends(get_db)
) -> NoteService:
    return NoteService(session)

def get_tag_service(
        session: AsyncSession = Depends(get_db)
) -> TagService:
    return TagService(session)