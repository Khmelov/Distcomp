from fastapi import APIRouter, status, HTTPException, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from loguru import logger

from db_helper import db_helper

from .schemas import Story, StoryID
import api_v1.stories.crud as crud

from .helpers import story_to_bd, bd_to_id, check_title, check_creator


logger.add(
    sink="RV2Lab.log",
    mode="w",
    encoding="utf-8",
    format="{level} {message}",
)

router = APIRouter(prefix="/stories")


costyl_id = 0


@router.get("/{get_id}", status_code=status.HTTP_200_OK, response_model=StoryID)
async def story_by_id(
    get_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"GET definite Story with id: {get_id}")
    story = await crud.get_story(session=session, story_id=get_id)
    if not story:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such Story"
        )
    return bd_to_id(story)


@router.get("", status_code=status.HTTP_200_OK, response_model=StoryID)
async def story(session: AsyncSession = Depends(db_helper.session_dependency)):
    logger.info("GET Sticker")
    global costyl_id

    story = await crud.get_story(session=session, story_id=costyl_id)
    if not story:
        return {
            "id": 0,
            "creatorId": 0,
            "title": "sdsds",
            "content": "dsdsds",
        }
    return bd_to_id(story)


@router.post("", status_code=status.HTTP_201_CREATED, response_model=StoryID)
async def create_story(
    story_info: Story, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"POST Story with body: {story_info.model_dump()}")

    await check_creator(
        creator_id=story_info.creatorId,
        session=session,
    )

    await check_title(session=session, title=story_info.title)

    story_info = story_to_bd(story_info)
    story = await crud.create_story(session=session, story_info=story_info)

    if not story:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Incorrect data"
        )

    global costyl_id
    costyl_id = story.id

    return bd_to_id(story=story)


@router.delete("/{delete_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_story(
    delete_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):

    logger.info(f"DELETE Story with ID: {delete_id}")
    delete_state = await crud.delete_story(story_id=delete_id, session=session)
    if not delete_state:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such Story"
        )
    return


@router.put("", status_code=status.HTTP_200_OK, response_model=StoryID)
async def put_story(
    story_info: StoryID, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"PUT Story with body: {story_info.model_dump()}")
    story = await crud.put_story(story_info=story_info, session=session)
    if not story:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Invlaid PUT data"
        )
    return story
