from sqlalchemy.ext.asyncio import AsyncSession
from app.repository.db import notice_repo, tweet_repo
from app.schemas.notice import NoticeRequestTo, NoticeResponseTo
from app.core.exceptions import AppException

class NoticeService:
    async def create(self, session: AsyncSession, dto: NoticeRequestTo) -> NoticeResponseTo:
        if not await tweet_repo.get_by_id(session, dto.tweetId):
            raise AppException(400, "Tweet not found", 12)
        
        data = {
            "tweet_id": dto.tweetId,
            "content": dto.content
        }
        notice = await notice_repo.create(session, data)
        return NoticeResponseTo(
            id=notice.id, 
            tweetId=notice.tweet_id, 
            content=notice.content
        )

    async def get_all(self, session: AsyncSession, page: int = 1):
        notices = await notice_repo.get_all(session, limit=10, offset=(page - 1) * 10)
        return [NoticeResponseTo(id=n.id, tweetId=n.tweet_id, content=n.content) for n in notices]

    async def get_by_id(self, session: AsyncSession, id: int):
        res = await notice_repo.get_by_id(session, id)
        if not res: raise AppException(404, "Notice not found", 13)
        return NoticeResponseTo(id=res.id, tweetId=res.tweet_id, content=res.content)

    async def update(self, session: AsyncSession, id: int, dto: NoticeRequestTo) -> NoticeResponseTo:
        if not await tweet_repo.get_by_id(session, dto.tweetId):
            raise AppException(400, "Tweet not found", 14)
            
        data = {
            "tweet_id": dto.tweetId,
            "content": dto.content
        }
        updated = await notice_repo.update(session, id, data)
        if not updated: raise AppException(404, "Notice not found", 15)
        return NoticeResponseTo(id=updated.id, tweetId=updated.tweet_id, content=updated.content)

    async def delete(self, session: AsyncSession, id: int):
        if not await notice_repo.delete(session, id):
            raise AppException(404, "Notice not found", 16)