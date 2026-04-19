from typing import List

from fastapi import APIRouter, Depends, HTTPException, status

from app.dtos.creator_response import CreatorResponseTo
from app.dtos.marker_response import MarkerResponseTo
from app.dtos.notice_response import NoticeResponseTo
from app.dtos.story_request import StoryRequestTo
from app.dtos.story_response import StoryResponseTo
from app.services.creator_service import CreatorService
from app.services.marker_service import MarkerService
from app.services.notice_service import NoticeService
from app.services.story_service import StoryService


router = APIRouter(prefix="/api/v1.0", tags=["stories"])


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


@router.post("/stories", response_model=StoryResponseTo, status_code=status.HTTP_201_CREATED)
def create_story(dto: StoryRequestTo, service: StoryService = Depends(get_story_service)) -> StoryResponseTo:
    try:
        return service.create_story(dto)
    except ValueError as ex:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail={"errorMessage": str(ex), "errorCode": 40001},
        ) from ex


@router.get("/stories", response_model=List[StoryResponseTo])
def list_stories(service: StoryService = Depends(get_story_service)) -> List[StoryResponseTo]:
    return service.get_all_stories()


@router.get("/stories/{story_id}", response_model=StoryResponseTo)
def get_story(story_id: int, service: StoryService = Depends(get_story_service)) -> StoryResponseTo:
    story = service.get_story(story_id)
    if not story:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Story not found", "errorCode": 40401},
        )
    return story


@router.put("/stories/{story_id}", response_model=StoryResponseTo)
def update_story(
    story_id: int, dto: StoryRequestTo, service: StoryService = Depends(get_story_service)
) -> StoryResponseTo:
    try:
        updated = service.update_story(story_id, dto)
    except ValueError as ex:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail={"errorMessage": str(ex), "errorCode": 40001},
        ) from ex

    if not updated:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Story not found", "errorCode": 40401},
        )
    return updated


@router.delete("/stories/{story_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_story(story_id: int, service: StoryService = Depends(get_story_service)) -> None:
    deleted = service.delete_story(story_id)
    if not deleted:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Story not found", "errorCode": 40401},
        )


@router.get("/story/{story_id}/creator", response_model=CreatorResponseTo)
def get_creator_by_story(
    story_id: int,
    story_service: StoryService = Depends(get_story_service),
    creator_service: CreatorService = Depends(get_creator_service),
) -> CreatorResponseTo:
    story = story_service.get_story(story_id)
    if not story:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Story not found", "errorCode": 40401},
        )
    creator = creator_service.get_creator(story.creator_id)
    if not creator:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Creator not found", "errorCode": 40401},
        )
    return creator


@router.get("/story/{story_id}/markers", response_model=List[MarkerResponseTo])
def get_markers_by_story(
    story_id: int,
    story_service: StoryService = Depends(get_story_service),
    marker_service: MarkerService = Depends(get_marker_service),
) -> List[MarkerResponseTo]:
    story = story_service.get_story(story_id)
    if not story:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Story not found", "errorCode": 40401},
        )
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
) -> List[NoticeResponseTo]:
    try:
        return notice_service.get_notices_by_story(story_id)
    except ValueError as ex:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": str(ex), "errorCode": 40401},
        ) from ex

