from fastapi import APIRouter, HTTPException, status
from loguru import logger
from .schemas import Story, StoryID
from api_v1.util import clear_storage


logger.add(
        sink = "RV1Lab.log",
        mode="w",
        encoding="utf-8",
        format="{time} {level} {message}",)

router = APIRouter()
prefix = "/stories"

current_story = {
    "id": 0,
    "creatorId": 0,
    "title": "",
    "content": "",
}



@router.get(prefix + "/{get_id}",
            status_code=status.HTTP_200_OK,
            response_model=StoryID)
async def story_by_id(
    get_id: int
):
    global current_story
    logger.info(f"GET story by id {get_id}")
    if get_id != current_story["id"]:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such story"
        )
    return StoryID.model_validate(current_story)


@router.get(prefix,
            status_code=status.HTTP_200_OK,
            response_model=StoryID)
async def story():
    global current_story
    logger.info("GET story")
    return StoryID.model_validate(current_story)


@router.post(prefix,
            status_code= status.HTTP_201_CREATED,
            response_model=StoryID)
async def create_story(
    story: Story
):
    global current_story
    logger.info(f"POST creator with body: {story.model_dump()}")
    current_story = {"id":0, **story.model_dump() }
    return StoryID.model_validate(current_story)


@router.delete(prefix + "/{delete_id}",
               status_code=status.HTTP_204_NO_CONTENT)
async def delete_story(
    delete_id: int
):
    global current_story
    logger.info(f"DELETE creator with ID: {delete_id}")
    if delete_id != current_story["id"]:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such story"
        )
    current_story = clear_storage(current_story)
    current_story["creatorId"] = 100000
    return 


@router.put(prefix,
            status_code=status.HTTP_200_OK,
            response_model=StoryID)
async def put_story(
    story: StoryID
):
    global current_story
    logger.info(f"PUT creator with body: {story.model_dump()}")
    if story.title == "x":
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invlaid PUT data"
        )
    current_story = {**story.model_dump()}
    return story

