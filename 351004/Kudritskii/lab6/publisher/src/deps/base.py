import httpx
from fastapi import Depends
from redis.asyncio import Redis
from sqlalchemy.ext.asyncio import AsyncSession

from src.core import get_redis, get_db
from src.core.settings import settings
from src.services import UserService, NoticeService, NewsService, LabelService


def get_user_service(
    session: AsyncSession = Depends(get_db)
) -> UserService:
    return UserService(session)

def get_notice_service(
        session: AsyncSession = Depends(get_db)
) -> NoticeService:
    return NoticeService(session)

http_news_client = httpx.AsyncClient(base_url=settings.news_service_url)

def get_news_service(redis: Redis = Depends(get_redis)) -> NewsService:
    return NewsService(http_news_client, redis)

def get_label_service(
        session: AsyncSession = Depends(get_db)
) -> LabelService:
    return LabelService(session)