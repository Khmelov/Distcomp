from typing import List

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession

from publisher.database import get_session
from publisher.exceptions.app_exceptions import CreatorNotFoundError, StoryNotFoundError
from publisher.models.story import Story
from publisher.repositories.sqlalchemy import SQLAlchemyRepository
from publisher.schemas.common import Page, PaginationParams
from publisher.schemas.creator import CreatorRead
from publisher.schemas.marker import MarkerRead
from publisher.schemas.notice import NoticeRead
from publisher.schemas.story import StoryCreate, StoryRead, StoryUpdate
from publisher.services.creator_service import CreatorService
from publisher.services.marker_service import MarkerService
from publisher.services.notice_service import NoticeService
from publisher.services.story_service import StoryService
from publisher.models.creator import Creator
from publisher.models.marker import Marker
from publisher.models.notice import Notice


router = APIRouter(prefix="/api/v1.0", tags=["stories"])


def get_story_service(session: AsyncSession = Depends(get_session)) -> StoryService:
    repo = SQLAlchemyRepository[Story](Story, session)
    return StoryService(repo, session)


def get_creator_service(session: AsyncSession = Depends(get_session)) -> CreatorService:
    repo = SQLAlchemyRepository[Creator](Creator, session)
    return CreatorService(repo)


def get_marker_service(session: AsyncSession = Depends(get_session)) -> MarkerService:
    repo = SQLAlchemyRepository[Marker](Marker, session)
    return MarkerService(repo)


def get_notice_service(session: AsyncSession = Depends(get_session)) -> NoticeService:
    repo = SQLAlchemyRepository[Notice](Notice, session)
    return NoticeService(repo, session)


@router.post("/stories", response_model=StoryRead, status_code=status.HTTP_201_CREATED)
async def create_story(
    dto: StoryCreate,
    service: StoryService = Depends(get_story_service),
) -> StoryRead:
    try:
        return await service.create(dto)
    except ValueError as ex:
        # 40001 – validation/business error
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail={"errorMessage": str(ex), "errorCode": 40001},
        ) from ex


@router.get("/stories", response_model=Page[StoryRead])
async def list_stories(
    pagination: PaginationParams = Depends(),
    service: StoryService = Depends(get_story_service),
) -> Page[StoryRead]:
    return await service.get_all(pagination)


@router.get("/stories/{story_id}", response_model=StoryRead)
async def get_story(
    story_id: int,
    service: StoryService = Depends(get_story_service),
) -> StoryRead:
    story = await service.get(story_id)
    if not story:
        raise StoryNotFoundError(story_id)
    return story


@router.put("/stories/{story_id}", response_model=StoryRead)
async def update_story(
    story_id: int,
    dto: StoryUpdate,
    service: StoryService = Depends(get_story_service),
) -> StoryRead:
    try:
        updated = await service.update(story_id, dto)
    except ValueError as ex:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail={"errorMessage": str(ex), "errorCode": 40001},
        ) from ex
    if not updated:
        raise StoryNotFoundError(story_id)
    return updated


@router.delete("/stories/{story_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_story(
    story_id: int,
    service: StoryService = Depends(get_story_service),
) -> None:
    deleted = await service.delete(story_id)
    if not deleted:
        raise StoryNotFoundError(story_id)


@router.get("/story/{story_id}/creator", response_model=CreatorRead)
async def get_creator_by_story(
    story_id: int,
    story_service: StoryService = Depends(get_story_service),
    creator_service: CreatorService = Depends(get_creator_service),
) -> CreatorRead:
    story = await story_service.get(story_id)
    if not story:
        raise StoryNotFoundError(story_id)
    creator = await creator_service.get(story.creator_id)
    if not creator:
        raise CreatorNotFoundError(story.creator_id)
    return creator


@router.get("/story/{story_id}/markers", response_model=List[MarkerRead])
async def get_markers_by_story(
    story_id: int,
    story_service: StoryService = Depends(get_story_service),
    marker_service: MarkerService = Depends(get_marker_service),
) -> List[MarkerRead]:
    story = await story_service.get(story_id)
    if not story:
        raise StoryNotFoundError(story_id)

    markers: List[MarkerRead] = []
    for marker_id in story.marker_ids:
        marker = await marker_service.get(marker_id)
        if marker:
            markers.append(marker)
    return markers


@router.get("/story/{story_id}/notices", response_model=List[NoticeRead])
async def get_notices_by_story(
    story_id: int,
    notice_service: NoticeService = Depends(get_notice_service),
    pagination: PaginationParams = Depends(),
) -> List[NoticeRead]:
    try:
        return await notice_service.get_by_story(story_id, pagination)
    except ValueError as ex:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": str(ex), "errorCode": 40402},
        ) from ex

