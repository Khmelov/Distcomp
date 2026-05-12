from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from Task350.publisher.src.infrastructure.database import get_db
from Task350.publisher.src.infrastructure.redis_client import RedisClient
from Task350.publisher.src.domain.repositories.sqlalchemy.creator_repository import SQLAlchemyCreatorRepository
from Task350.publisher.src.domain.repositories.sqlalchemy.tweet_repository import SQLAlchemyTweetRepository
from Task350.publisher.src.domain.repositories.sqlalchemy.marker_repository import SQLAlchemyMarkerRepository
from Task350.publisher.src.services import CreatorService, TweetService, MarkerService
from Task350.publisher.src.services.post import PostService

# Глобальный экземпляр Redis (можно создать здесь или передавать из main)
_redis_client = RedisClient()

def get_creator_repo(db: AsyncSession = Depends(get_db)) -> SQLAlchemyCreatorRepository:
    return SQLAlchemyCreatorRepository(db)

def get_tweet_repo(db: AsyncSession = Depends(get_db)) -> SQLAlchemyTweetRepository:
    return SQLAlchemyTweetRepository(db)

def get_marker_repo(db: AsyncSession = Depends(get_db)) -> SQLAlchemyMarkerRepository:
    return SQLAlchemyMarkerRepository(db)

async def get_creator_service(
    repo: SQLAlchemyCreatorRepository = Depends(get_creator_repo),
    tweet_repo: SQLAlchemyTweetRepository = Depends(get_tweet_repo)
) -> CreatorService:
    return CreatorService(repo, tweet_repo, _redis_client)

async def get_tweet_service(
    tweet_repo: SQLAlchemyTweetRepository = Depends(get_tweet_repo),
    creator_repo: SQLAlchemyCreatorRepository = Depends(get_creator_repo)
) -> TweetService:
    return TweetService(tweet_repo, creator_repo, _redis_client)

async def get_post_service() -> PostService:
    # Получаем глобальные producer и consumer из main (или создаём их здесь)
    # Для простоты импортируем из main (но лучше передавать через Depends)
    from Task350.main import producer, consumer
    return PostService(producer, consumer, _redis_client)

async def get_marker_service(
    repo: SQLAlchemyMarkerRepository = Depends(get_marker_repo)
) -> MarkerService:
    return MarkerService(repo, _redis_client)