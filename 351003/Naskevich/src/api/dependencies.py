from typing import Annotated, AsyncGenerator

from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from src.database.pool import create_pool
from src.database.repositories.editor import EditorRepository
from src.database.repositories.marker import MarkerRepository
from src.database.repositories.post import PostRepository
from src.database.repositories.tweet import TweetRepository
from src.database.uow import SQLAlchemyUoW
from src.services.editor import EditorService
from src.services.marker import MarkerService
from src.services.post import PostService
from src.services.tweet import TweetService
from src.config import PostgresConfig


async def get_session() -> AsyncGenerator[AsyncSession, None]:
    config = PostgresConfig()
    session_maker = create_pool(config.url())
    async with session_maker() as session:
        yield session


SessionDep = Annotated[AsyncSession, Depends(get_session)]


def get_uow(session: SessionDep) -> SQLAlchemyUoW:
    return SQLAlchemyUoW(session)


UoWDep = Annotated[SQLAlchemyUoW, Depends(get_uow)]


def get_editor_service(session: SessionDep, uow: UoWDep) -> EditorService:
    return EditorService(repository=EditorRepository(session), uow=uow)


def get_tweet_service(session: SessionDep, uow: UoWDep) -> TweetService:
    return TweetService(
        repository=TweetRepository(session),
        editor_repository=EditorRepository(session),
        marker_repository=MarkerRepository(session),
        uow=uow,
    )


def get_marker_service(session: SessionDep, uow: UoWDep) -> MarkerService:
    return MarkerService(repository=MarkerRepository(session), uow=uow)


def get_post_service(session: SessionDep, uow: UoWDep) -> PostService:
    return PostService(
        repository=PostRepository(session),
        tweet_repository=TweetRepository(session),
        uow=uow,
    )


EditorServiceDep = Annotated[EditorService, Depends(get_editor_service)]
TweetServiceDep = Annotated[TweetService, Depends(get_tweet_service)]
MarkerServiceDep = Annotated[MarkerService, Depends(get_marker_service)]
PostServiceDep = Annotated[PostService, Depends(get_post_service)]
