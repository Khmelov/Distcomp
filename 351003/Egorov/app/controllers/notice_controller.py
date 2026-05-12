from typing import List

from fastapi import APIRouter, Depends, HTTPException, status

from app.dtos.notice_request import NoticeRequestTo
from app.dtos.notice_response import NoticeResponseTo
from app.repositories.notice_repository import NoticeRepository
from app.services.notice_service import NoticeService


router = APIRouter(prefix="/api/v1.0/notices", tags=["notices"])

def get_notice_service() -> NoticeService:
    from main import notice_service

    return notice_service


    service = NoticeService(NoticeRepository())
    try:
        res = service.create_notice(dto)
        if not res:
            return HTTPException(
            detail={"errorMessage": "стоп слово в тексте"},
            status_code=400
        )
        return res
    except ValueError as ex:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail={"errorMessage": str(ex), "errorCode": 40001},
        ) from ex


@router.get("", response_model=List[NoticeResponseTo])
def list_notices() -> List[NoticeResponseTo]:
    service = NoticeService(NoticeRepository())
    return service.get_all_notices()


@router.get("/{notice_id}", response_model=NoticeResponseTo)
def get_notice(notice_id: int) -> NoticeResponseTo:
    service = NoticeService(NoticeRepository())
    notice = service.get_notice(notice_id)
    if not notice:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Notice not found", "errorCode": 40401},
        )
    return notice


@router.put("/{notice_id}", response_model=NoticeResponseTo)
def update_notice(
    notice_id: int, dto: NoticeRequestTo
) -> NoticeResponseTo:
    service = NoticeService(NoticeRepository())
    try:
        updated = service.update_notice(notice_id, dto)
    except ValueError as ex:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail={"errorMessage": str(ex), "errorCode": 40001},
        ) from ex

    if not updated:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Notice not found", "errorCode": 40401},
        )
    return updated


@router.delete("/{notice_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_notice(notice_id: int) -> None:
    service = NoticeService(NoticeRepository())
    deleted = service.delete_notice(notice_id)
    if not deleted:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Notice not found", "errorCode": 40401},
        )

