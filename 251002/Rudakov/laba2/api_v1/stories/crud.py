from sqlalchemy import Result, select
from .schemas import Story as StoryIN, StoryID, StoryBD 
from sqlalchemy.ext.asyncio import AsyncSession
from models import Story
# from loguru import logger

# logger.add(
#         sink = "RV2Lab.log",
#         mode="w",
#         encoding="utf-8",
#         format="{time} {level} {message}",)


async def create_story(
        story_info: StoryBD,
        session: AsyncSession
):
    story = Story(**story_info.model_dump())
    session.add(story)
    await session.commit()
    return story



async def get_story(
        session: AsyncSession,      
        story_id: int
):
    stat = select(Story).where(Story.id == story_id)
    result: Result = await session.execute(stat)
    # creators: Sequence = result.scalars().all()
    story: Story | None = result.scalar_one_or_none()

    return story


async def get_story_by_title(
        session: AsyncSession,      
        story_title: str
):
    stat = select(Story).where(Story.title == story_title)
    result: Result = await session.execute(stat)
    # creators: Sequence = result.scalars().all()
    story: Story | None = result.scalar_one_or_none()

    return story


async def delete_story(
        story_id: int,
        session: AsyncSession
):
    story = await get_story(story_id=story_id, session=session)
    if not story:
        return False
    await session.delete(story)
    await session.commit()
    return True


async def put_story(
        story_info: StoryID,
        session: AsyncSession
):
    story_id =story_info.id
    story_update = StoryIN(**story_info.model_dump())
    story = await get_story(story_id=story_id, session=session)
    if not story:
        return False
    
    for name, value in story_update.model_dump().items():
        setattr(story, name, value)
    await session.commit()
    return story