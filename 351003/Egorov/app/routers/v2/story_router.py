from typing import List

from fastapi import APIRouter, Depends, HTTPException, status

from app.auth.dependencies import get_current_user
from app.dtos.creator_response import CreatorResponseTo
from app.dtos.marker_response import MarkerResponseTo
from app.dtos.notice_response import NoticeResponseTo
from app.dtos.story_request import StoryRequestTo
from app.dtos.story_response import StoryResponseTo
from app.models.creator import Creator, CreatorRole
from app.services.creator_service import CreatorService
from app.services.marker_service import MarkerService
from app.services.notice_service import NoticeService
from app.services.story_service import StoryService


router = APIRouter(prefix="/api/v2.0", tags=["v2-stories"])


def get_story_service() -> StoryService:
    from main import story_service

    return story_service


def get_creator_service() -> CreatorService:
    from main import creator_service

    return creator_service


def get_marker_service() -> MarkerService:
    from main import marker_service

    return marker_service


def get_notice_service() -> NoticeService:
    from main import notice_service

    return notice_service


def _is_admin(user: Creator) -> bool:
    return user.role == CreatorRole.ADMIN


@router.post("/stories", response_model=StoryResponseTo, status_code=status.HTTP_201_CREATED)
def create_story(
    dto: StoryRequestTo,
    service: StoryService = Depends(get_story_service),
    current_user: Creator = Depends(get_current_user),
) -> StoryResponseTo:
    if not _is_admin(current_user) and dto.creator_id != current_user.id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Forbidden")
    try:
        return service.create_story(dto)
    except ValueError as ex:
        raise HTTPException(status_code=status.HTTP_422_UNPROCESSABLE_ENTITY, detail=str(ex)) from ex


@router.get("/stories", response_model=List[StoryResponseTo])
def list_stories(
    service: StoryService = Depends(get_story_service),
    _: Creator = Depends(get_current_user),
) -> List[StoryResponseTo]:
    return service.get_all_stories()


@router.get("/stories/{story_id}", response_model=StoryResponseTo)
def get_story(
    story_id: int,
    service: StoryService = Depends(get_story_service),
    _: Creator = Depends(get_current_user),
) -> StoryResponseTo:
    story = service.get_story(story_id)
    if not story:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Story not found")
    return story


@router.put("/stories/{story_id}", response_model=StoryResponseTo)
def update_story(
    story_id: int,
    dto: StoryRequestTo,
    service: StoryService = Depends(get_story_service),
    current_user: Creator = Depends(get_current_user),
) -> StoryResponseTo:
    existing = service.get_story(story_id)
    if not existing:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Story not found")
    if not _is_admin(current_user) and existing.creator_id != current_user.id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Forbidden")
    if not _is_admin(current_user) and dto.creator_id != current_user.id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Forbidden")

    try:
        updated = service.update_story(story_id, dto)
    except ValueError as ex:
        raise HTTPException(status_code=status.HTTP_422_UNPROCESSABLE_ENTITY, detail=str(ex)) from ex

    if not updated:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Story not found")
    return updated


@router.delete("/stories/{story_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_story(
    story_id: int,
    service: StoryService = Depends(get_story_service),
    current_user: Creator = Depends(get_current_user),
) -> None:
    existing = service.get_story(story_id)
    if not existing:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Story not found")
    if not _is_admin(current_user) and existing.creator_id != current_user.id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Forbidden")

    deleted = service.delete_story(story_id)
    if not deleted:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Story not found")


@router.get("/story/{story_id}/creator", response_model=CreatorResponseTo)
def get_creator_by_story(
    story_id: int,
    story_service: StoryService = Depends(get_story_service),
    creator_service: CreatorService = Depends(get_creator_service),
    _: Creator = Depends(get_current_user),
) -> CreatorResponseTo:
    story = story_service.get_story(story_id)
    if not story:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Story not found")
    creator = creator_service.get_creator(story.creator_id)
    if not creator:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Creator not found")
    return creator


@router.get("/story/{story_id}/markers", response_model=List[MarkerResponseTo])
def get_markers_by_story(
    story_id: int,
    story_service: StoryService = Depends(get_story_service),
    marker_service: MarkerService = Depends(get_marker_service),
    _: Creator = Depends(get_current_user),
) -> List[MarkerResponseTo]:
    story = story_service.get_story(story_id)
    if not story:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Story not found")
    markers: List[MarkerResponseTo] = []
    for marker_id in story.marker_ids:
        marker = marker_service.get_marker(marker_id)
        if marker:
            markers.append(marker)
    return markers


@router.get("/story/{story_id}/notices", response_model=List[NoticeResponseTo])
def get_notices_by_story(
    story_id: int,
    notice_service: NoticeService = Depends(get_notice_service),
    _: Creator = Depends(get_current_user),
) -> List[NoticeResponseTo]:
    try:
        return notice_service.get_notices_by_story(story_id)
    except ValueError as ex:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=str(ex)) from ex
