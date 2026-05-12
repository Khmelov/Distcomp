from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from Task320.src.infrastructure.database import get_db
from Task320.src.domain.repositories.sqlalchemy.creator_repository import SQLAlchemyCreatorRepository
from Task320.src.domain.repositories.sqlalchemy.tweet_repository import SQLAlchemyTweetRepository
from Task320.src.domain.repositories.sqlalchemy.post_repository import SQLAlchemyPostRepository
from Task320.src.domain.repositories.sqlalchemy.marker_repository import SQLAlchemyMarkerRepository
from Task320.src.services import CreatorService, TweetService, PostService, MarkerService


def get_creator_repo(db: AsyncSession = Depends(get_db)) -> SQLAlchemyCreatorRepository:
    return SQLAlchemyCreatorRepository(db)


def get_tweet_repo(db: AsyncSession = Depends(get_db)) -> SQLAlchemyTweetRepository:
    return SQLAlchemyTweetRepository(db)


def get_post_repo(db: AsyncSession = Depends(get_db)) -> SQLAlchemyPostRepository:
    return SQLAlchemyPostRepository(db)


def get_marker_repo(db: AsyncSession = Depends(get_db)) -> SQLAlchemyMarkerRepository:
    return SQLAlchemyMarkerRepository(db)


async def get_creator_service(
    repo: SQLAlchemyCreatorRepository = Depends(get_creator_repo),
    tweet_repo: SQLAlchemyTweetRepository = Depends(get_tweet_repo)
) -> CreatorService:
    return CreatorService(repo, tweet_repo)


async def get_tweet_service(
    tweet_repo: SQLAlchemyTweetRepository = Depends(get_tweet_repo),
    creator_repo: SQLAlchemyCreatorRepository = Depends(get_creator_repo)
) -> TweetService:
    return TweetService(tweet_repo, creator_repo)


async def get_post_service(
    repo: SQLAlchemyPostRepository = Depends(get_post_repo)
) -> PostService:
    return PostService(repo)


async def get_marker_service(
    repo: SQLAlchemyMarkerRepository = Depends(get_marker_repo)
) -> MarkerService:
    return MarkerService(repo)