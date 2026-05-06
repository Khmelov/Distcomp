from fastapi import APIRouter, Depends, HTTPException, status
from typing import List
from dto.requests import NoticeRequestTo
from dto.responses import NoticeResponseTo
from services.notice import NoticeService
from dependencies import get_notice_service

router = APIRouter(prefix="/notices", tags=["notices"])


@router.get("", response_model=List[NoticeResponseTo])
def get_all_notices(service: NoticeService = Depends(get_notice_service)):
    return service.get_all()


@router.get("/{id}", response_model=NoticeResponseTo)
def get_notice(id: int, service: NoticeService = Depends(get_notice_service)):
    notice = service.get(id)
    if not notice:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Notice not found"
        )
    return notice


@router.post("", response_model=NoticeResponseTo, status_code=status.HTTP_201_CREATED)
def create_notice(
    request: NoticeRequestTo, service: NoticeService = Depends(get_notice_service)
):
    return service.create(request)


@router.put("/{id}", response_model=NoticeResponseTo)
def update_notice(
    id: int,
    request: NoticeRequestTo,
    service: NoticeService = Depends(get_notice_service),
):
    try:
        return service.update(id, request)
    except ValueError as e:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=str(e))


@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_notice(id: int, service: NoticeService = Depends(get_notice_service)):
    if not service.delete(id):
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Notice not found"
        )
