from mailbox import Message
from sqlalchemy.ext.asyncio import AsyncSession
from fastapi import Depends, HTTPException, status
from api_v1.stories.crud import get_story
from api_v1.messages.crud import get_message
from db_helper import db_helper
from sqlalchemy import Result, select

async def check_story(
        story_id: int,
        session: AsyncSession
):
    """
    We can't connect Message with defunct Story
    """
    story_exists = await get_story(story_id=story_id, session=session)
    if not story_exists:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Story doesn't exist")
    

async def fetch_message(message_id: int):
    async for session in db_helper.session_dependency():
        message = await get_message(session=session, message_id=message_id)
        return message