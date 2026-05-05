from typing import List

from motor.motor_asyncio import AsyncIOMotorCollection

from src.domain.repositories import NewsRepository
from src.schemas.news import NewsResponseTo, NewsRequestTo


class NewsService:
    def __init__(self, collection: AsyncIOMotorCollection):
        self.news_repo = NewsRepository(collection)

    async def get_one(self, news_id: int) -> NewsResponseTo:
        news = await self.news_repo.get_one(news_id)
        return NewsResponseTo.model_validate(news)

    async def get_all(self) -> List[NewsResponseTo]:
        newss = await self.news_repo.get_all()
        return [NewsResponseTo.model_validate(news) for news in newss]

    async def create(self, dto: NewsRequestTo) -> NewsResponseTo:
        news_args = dto.model_dump()
        created_news = await self.news_repo.create(**news_args)
        return NewsResponseTo.model_validate(created_news)

    async def update(self, news_id: int, dto: NewsRequestTo) -> NewsResponseTo:
        news_args = dto.model_dump()
        updated_news = await self.news_repo.update(news_id, **news_args)
        return NewsResponseTo.model_validate(updated_news)

    async def delete(self, news_id: int) -> None:
        await self.news_repo.delete(news_id)