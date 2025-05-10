from sqlalchemy import Result, select
from .schemas import News as NewsIN, NewsID, NewsBD 
from sqlalchemy.ext.asyncio import AsyncSession
from models import News
# from loguru import logger

# logger.add(
#         sink = "RV2Lab.log",
#         mode="w",
#         encoding="utf-8",
#         format="{time} {level} {comment}",)


async def create_news(
        news_info: NewsBD,
        session: AsyncSession
):
    news = News(**news_info.model_dump())
    session.add(news)
    await session.commit()
    return news



async def get_news(
        session: AsyncSession,      
        news_id: int
):
    stat = select(News).where(News.id == news_id)
    result: Result = await session.execute(stat)
    # editors: Sequence = result.scalars().all()
    news: News | None = result.scalar_one_or_none()

    return news


async def get_news_by_title(
        session: AsyncSession,      
        news_title: str
):
    stat = select(News).where(News.title == news_title)
    result: Result = await session.execute(stat)
    # editors: Sequence = result.scalars().all()
    news: News | None = result.scalar_one_or_none()

    return news


async def delete_news(
        news_id: int,
        session: AsyncSession
):
    news = await get_news(news_id=news_id, session=session)
    if not news:
        return False
    await session.delete(news)
    await session.commit()
    return True


async def put_news(
        news_info: NewsID,
        session: AsyncSession
):
    news_id =news_info.id
    news_update = NewsIN(**news_info.model_dump())
    news = await get_news(news_id=news_id, session=session)
    if not news:
        return False
    
    for name, value in news_update.model_dump().items():
        setattr(news, name, value)
    await session.commit()
    return news