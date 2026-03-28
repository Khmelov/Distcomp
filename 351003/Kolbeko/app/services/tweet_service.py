from datetime import datetime
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, and_
from app.repository.db import tweet_repo, author_repo
from app.models.tweet import Tweet
from app.models.tweet_label import TweetLabel
from app.models.label import Label
from app.schemas.tweet import TweetRequestTo, TweetResponseTo
from app.core.exceptions import AppException

class TweetService:
    async def create(self, session: AsyncSession, dto: TweetRequestTo) -> TweetResponseTo:
        if not await author_repo.get_by_id(session, dto.authorId):
            raise AppException(400, "Author not found", 4)
        
        # Проверка уникальности заголовка для автора
        query = select(Tweet).where(and_(Tweet.author_id == dto.authorId, Tweet.title == dto.title))
        if (await session.execute(query)).scalar_one_or_none():
            raise AppException(403, "Tweet with this title already exists for this author", 18)

        now = datetime.now()
        data = {
            "author_id": dto.authorId,
            "title": dto.title,
            "content": dto.content,
            "created": now,
            "modified": now
        }
        
        tweet = await tweet_repo.create(session, data)

        # Обработка меток
        if dto.labelIds:
            from app.services.label_service import LabelService
            label_service = LabelService()
            for label_name in dto.labelIds:
                label = await label_service.get_or_create(session, str(label_name))
                session.add(TweetLabel(tweet_id=tweet.id, label_id=label.id))
        
        await session.commit()
        
        return TweetResponseTo(
            id=tweet.id,
            authorId=tweet.author_id,
            title=tweet.title,
            content=tweet.content,
            created=tweet.created.isoformat(),
            modified=tweet.modified.isoformat()
        )

    async def get_all(self, session: AsyncSession, page: int = 1, size: int = 100):
        tweets = await tweet_repo.get_all(session, limit=size, offset=(page - 1) * size)
        return [TweetResponseTo(
            id=t.id, authorId=t.author_id, title=t.title, 
            content=t.content, created=t.created.isoformat(), modified=t.modified.isoformat()
        ) for t in tweets]

    async def get_by_id(self, session: AsyncSession, id: int):
        res = await tweet_repo.get_by_id(session, id)
        if not res: raise AppException(404, "Tweet not found", 5)
        return TweetResponseTo(
            id=res.id, authorId=res.author_id, title=res.title, 
            content=res.content, created=res.created.isoformat(), modified=res.modified.isoformat()
        )

    async def update(self, session: AsyncSession, id: int, dto: TweetRequestTo) -> TweetResponseTo:
        if not await author_repo.get_by_id(session, dto.authorId):
            raise AppException(400, "Author not found", 7)
            
        data = {
            "author_id": dto.authorId,
            "title": dto.title,
            "content": dto.content,
            "modified": datetime.now()
        }
        updated = await tweet_repo.update(session, id, data)
        if not updated: raise AppException(404, "Tweet not found", 6)
        return TweetResponseTo(
            id=updated.id, authorId=updated.author_id, title=updated.title, 
            content=updated.content, created=updated.created.isoformat(), modified=updated.modified.isoformat()
        )

    async def delete(self, session: AsyncSession, id: int):
        if not await tweet_repo.delete(session, id):
            raise AppException(404, "Tweet not found", 8)