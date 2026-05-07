from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession

from publisher.database import get_session
from publisher.exceptions.app_exceptions import NoticeNotFoundError
from publisher.models.notice import Notice
from publisher.repositories.sqlalchemy import SQLAlchemyRepository
from publisher.schemas.common import Page, PaginationParams
from publisher.schemas.notice import NoticeCreate, NoticeRead, NoticeUpdate
from publisher.services.notice_service import NoticeService


router = APIRouter(prefix="/api/v1.0/notices", tags=["notices"])


def get_notice_service(session: AsyncSession = Depends(get_session)) -> NoticeService:
    repo = SQLAlchemyRepository[Notice](Notice, session)
    return NoticeService(repo, session)


@router.post("", response_model=NoticeRead, status_code=status.HTTP_201_CREATED)
async def create_notice(
    dto: NoticeCreate,
    service: NoticeService = Depends(get_notice_service),
) -> NoticeRead:
    try:
        return await service.create(dto)
    except ValueError as ex:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail={"errorMessage": str(ex), "errorCode": 40001},
        ) from ex


@router.get("", response_model=Page[NoticeRead])
async def list_notices(
    pagination: PaginationParams = Depends(),
    service: NoticeService = Depends(get_notice_service),
) -> Page[NoticeRead]:
    return await service.get_all(pagination)


@router.get("/{notice_id}", response_model=NoticeRead)
async def get_notice(
    notice_id: int,
    service: NoticeService = Depends(get_notice_service),
) -> NoticeRead:
    notice = await service.get(notice_id)
    if not notice:
        raise NoticeNotFoundError(notice_id)
    return notice


@router.put("/{notice_id}", response_model=NoticeRead)
async def update_notice(
    notice_id: int,
    dto: NoticeUpdate,
    service: NoticeService = Depends(get_notice_service),
) -> NoticeRead:
    try:
        updated = await service.update(notice_id, dto)
    except ValueError as ex:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail={"errorMessage": str(ex), "errorCode": 40001},
        ) from ex

    if not updated:
        raise NoticeNotFoundError(notice_id)
    return updated


@router.delete("/{notice_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_notice(
    notice_id: int,
    service: NoticeService = Depends(get_notice_service),
) -> None:
    deleted = await service.delete(notice_id)
    if not deleted:
        raise NoticeNotFoundError(notice_id)

