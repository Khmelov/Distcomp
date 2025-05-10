from .schemas import Story as StoryIN, StoryBD, StoryID
from models import Story
from sqlalchemy.ext.asyncio import AsyncSession
from fastapi import HTTPException, status
import api_v1.stories.crud as crud
from api_v1.creators.crud import get_creator


async def check_title(
    title: str,
    session: AsyncSession
):
    """
    Title must be unique 
    """
    title_exists = await crud.get_story_by_title(
        story_title=title,
        session=session
    )
    if title_exists:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Title already exists")
    

async def check_creator(
        creator_id: int,
        session: AsyncSession
):
    """
    We can't connect Story with defunct Creator
    """
    creator_exists = await get_creator(creator_id=creator_id, session=session)
    if not creator_exists:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Creator doesn't exist")

def story_to_bd(
        story: StoryIN
) -> StoryBD:
    return StoryBD(
        content= story.content,
        creator_id= story.creatorId,
        title= story.title,
    )


def bd_to_id(
        story: Story
) -> StoryID:
    return StoryID(
        content=story.content,
        creatorId=story.creator_id,
        id=story.id,
        title=story.title,
    )