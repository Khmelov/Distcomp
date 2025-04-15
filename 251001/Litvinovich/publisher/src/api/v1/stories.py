from fastapi import APIRouter, Depends
from typing import List, Annotated

from src.api.v1.dependencies import get_stories_service
from src.schemas.stories import StoryResponseTo, StoryRequestToUpdate
from src.schemas.stories import StoryRequestToAdd
from src.services.stories import StoriesService

router = APIRouter(prefix="/stories")


@router.get("", response_model=List[StoryResponseTo])
async def get_stories(stories_service: Annotated[StoriesService, Depends(get_stories_service)]):
    return await stories_service.get_stories()


@router.get("/{story_id}", response_model=StoryResponseTo)
async def get_story_by_id(story_id: int,
                            stories_service: Annotated[StoriesService, Depends(get_stories_service)]):
    return await stories_service.get_story_by_id(story_id)


@router.post("", response_model=StoryResponseTo, status_code=201)
async def create_story(story: StoryRequestToAdd,
                         stories_service: Annotated[StoriesService, Depends(get_stories_service)]):
    story_response = await stories_service.create_story(story)
    return story_response


@router.delete("/{story_id}", status_code=204)
async def delete_story(story_id: int,
                         stories_service: Annotated[StoriesService, Depends(get_stories_service)]):
    await stories_service.delete_story(story_id)
    return story_id


@router.put("", response_model=StoryResponseTo)
async def update_story(story: StoryRequestToUpdate,
                         stories_service: Annotated[StoriesService, Depends(get_stories_service)]):
    return await stories_service.update_story(story)