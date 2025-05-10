from mailbox import Message
from sqlalchemy.ext.asyncio import AsyncSession
from fastapi import Depends, HTTPException, status
from api_v1.news.crud import get_news
from api_v1.comments.crud import get_comment
from db_helper import db_helper
from sqlalchemy import Result, select

async def check_news(
        news_id: int,
        session: AsyncSession
):
    """
    We can't connect Comment with defunct News
    """
    news_exists = await get_news(news_id=news_id, session=session)
    if not news_exists:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="News doesn't exist")
    

async def fetch_comment(comment_id: int):
    async for session in db_helper.session_dependency():
        comment = await get_comment(session=session, comment_id=comment_id)
        return comment