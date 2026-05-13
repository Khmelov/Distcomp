from typing import List

from fastapi import APIRouter, Depends, HTTPException, status

from app.auth.dependencies import get_current_user
from app.dtos.notice_request import NoticeRequestTo
from app.dtos.notice_response import NoticeResponseTo
from app.models.creator import Creator, CreatorRole
from app.services.notice_service import NoticeService
from app.services.story_service import StoryService


router = APIRouter(prefix="/api/v2.0/notices", tags=["v2-notices"])


def get_notice_service() -> NoticeService:
    from main import notice_service

    return notice_service


def get_story_service() -> StoryService:
    from main import story_service

    return story_service


def _is_admin(user: Creator) -> bool:
    return user.role == CreatorRole.ADMIN


@router.post("", response_model=NoticeResponseTo, status_code=status.HTTP_201_CREATED)
def create_notice(
    dto: NoticeRequestTo,
    service: NoticeService = Depends(get_notice_service),
    story_service: StoryService = Depends(get_story_service),
    current_user: Creator = Depends(get_current_user),
) -> NoticeResponseTo:
    story = story_service.get_story(dto.story_id)
    if not story:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Story not found")
    if not _is_admin(current_user) and story.creator_id != current_user.id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Forbidden")
    try:
        notice = service.create_notice(dto)
        if not notice:
            raise HTTPException(status_code=status.HTTP_422_UNPROCESSABLE_ENTITY, detail="Cannot create notice")
        return notice
    except ValueError as ex:
        raise HTTPException(status_code=status.HTTP_422_UNPROCESSABLE_ENTITY, detail=str(ex)) from ex


@router.get("", response_model=List[NoticeResponseTo])
def list_notices(
    service: NoticeService = Depends(get_notice_service),
    _: Creator = Depends(get_current_user),
) -> List[NoticeResponseTo]:
    return service.get_all_notices()


@router.get("/{notice_id}", response_model=NoticeResponseTo)
def get_notice(
    notice_id: int,
    service: NoticeService = Depends(get_notice_service),
    _: Creator = Depends(get_current_user),
) -> NoticeResponseTo:
    notice = service.get_notice(notice_id)
    if not notice:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Notice not found")
    return notice


@router.put("/{notice_id}", response_model=NoticeResponseTo)
def update_notice(
    notice_id: int,
    dto: NoticeRequestTo,
    service: NoticeService = Depends(get_notice_service),
    story_service: StoryService = Depends(get_story_service),
    current_user: Creator = Depends(get_current_user),
) -> NoticeResponseTo:
    existing = service.get_notice(notice_id)
    if not existing:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Notice not found")
    owner_story = story_service.get_story(existing.story_id)
    if not owner_story:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Story not found")
    if not _is_admin(current_user) and owner_story.creator_id != current_user.id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Forbidden")
    if not _is_admin(current_user):
        target_story = story_service.get_story(dto.story_id)
        if not target_story or target_story.creator_id != current_user.id:
            raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Forbidden")

    try:
        updated = service.update_notice(notice_id, dto)
    except ValueError as ex:
        raise HTTPException(status_code=status.HTTP_422_UNPROCESSABLE_ENTITY, detail=str(ex)) from ex

    if not updated:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Notice not found")
    return updated


@router.delete("/{notice_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_notice(
    notice_id: int,
    service: NoticeService = Depends(get_notice_service),
    story_service: StoryService = Depends(get_story_service),
    current_user: Creator = Depends(get_current_user),
) -> None:
    existing = service.get_notice(notice_id)
    if not existing:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Notice not found")
    owner_story = story_service.get_story(existing.story_id)
    if not owner_story:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Story not found")
    if not _is_admin(current_user) and owner_story.creator_id != current_user.id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Forbidden")

    deleted = service.delete_notice(notice_id)
    if not deleted:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Notice not found")
