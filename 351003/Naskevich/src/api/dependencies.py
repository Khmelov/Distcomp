from typing import Annotated, AsyncGenerator

from fastapi import Depends, Request
from sqlalchemy.ext.asyncio import AsyncSession

from src.cache.redis_cache import RedisCache
from src.config import RedisConfig
from src.database.pool import create_pool
from src.database.repositories.editor import EditorRepository
from src.database.repositories.marker import MarkerRepository
from src.database.repositories.tweet import TweetRepository
from src.database.uow import SQLAlchemyUoW
from src.services.auth_service import AuthService
from src.services.editor import EditorService
from src.services.marker import MarkerService
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


def get_redis_cache(request: Request) -> RedisCache:
    return request.app.state.redis_cache


RedisCacheDep = Annotated[RedisCache, Depends(get_redis_cache)]


def _cache_ttl() -> int:
    return RedisConfig().default_ttl_seconds


def get_editor_service(
    session: SessionDep,
    uow: UoWDep,
    cache: RedisCacheDep,
) -> EditorService:
    return EditorService(
        repository=EditorRepository(session),
        uow=uow,
        cache=cache,
        cache_ttl_seconds=_cache_ttl(),
    )


def get_auth_service(
    session: SessionDep,
    uow: UoWDep,
    cache: RedisCacheDep,
) -> AuthService:
    return AuthService(
        repository=EditorRepository(session),
        uow=uow,
        cache=cache,
    )


def get_tweet_service(
    session: SessionDep,
    uow: UoWDep,
    cache: RedisCacheDep,
) -> TweetService:
    return TweetService(
        repository=TweetRepository(session),
        editor_repository=EditorRepository(session),
        marker_repository=MarkerRepository(session),
        uow=uow,
        cache=cache,
        cache_ttl_seconds=_cache_ttl(),
    )


def get_marker_service(
    session: SessionDep,
    uow: UoWDep,
    cache: RedisCacheDep,
) -> MarkerService:
    return MarkerService(
        repository=MarkerRepository(session),
        uow=uow,
        cache=cache,
        cache_ttl_seconds=_cache_ttl(),
    )


EditorServiceDep = Annotated[EditorService, Depends(get_editor_service)]
AuthServiceDep = Annotated[AuthService, Depends(get_auth_service)]
TweetServiceDep = Annotated[TweetService, Depends(get_tweet_service)]
MarkerServiceDep = Annotated[MarkerService, Depends(get_marker_service)]
