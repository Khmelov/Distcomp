from typing import List

from sqlalchemy.ext.asyncio import AsyncSession

from src.domain.repositories import NewsRepository, LabelRepository
from src.schemas.news import NewsResponseTo, NewsRequestTo


class NewsService:
    def __init__(self, session: AsyncSession) -> None:
        self.session = session
        self.label_repo = LabelRepository(session)
        self.news_repo = NewsRepository(session)

    async def get_one(self, news_id: int) -> NewsResponseTo:
        news = await self.news_repo.get_one(news_id)
        return NewsResponseTo.model_validate(news)

    async def get_all(self) -> List[NewsResponseTo]:
        news = await self.news_repo.get_all()
        return [NewsResponseTo.model_validate(t) for t in news]

    async def create(self, dto: NewsRequestTo) -> NewsResponseTo:
        news_args = dto.model_dump()
        label_names = news_args.pop("labels", [])
        label_objects = await self.label_repo.get_or_create_many(label_names)
        created_news = await self.news_repo.create(**news_args, labels=label_objects)
        await self.session.commit()
        return NewsResponseTo.model_validate(created_news)

    async def update(self, news_id: int, dto: NewsRequestTo) -> NewsResponseTo:
        news_args = dto.model_dump()
        label_names = news_args.pop("labels", []) or []
        news_args["labels"] = await self.label_repo.get_or_create_many(label_names)
        updated_news = await self.news_repo.update(news_id, **news_args)
        await self.session.commit()
        return NewsResponseTo.model_validate(updated_news)

    async def delete(self, news_id: int) -> None:
        await self.news_repo.delete_with_labels(news_id)
        await self.session.commit()