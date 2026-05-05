from http import HTTPStatus
from typing import List

from fastapi import APIRouter, Depends

from src.deps import get_notice_service
from src.schemas.notice import NoticeRequestTo, NoticeResponseTo
from src.services import NoticeService

router = APIRouter(prefix="/notices")

@router.get("/{notice_id}", response_model=NoticeResponseTo, status_code=HTTPStatus.OK)
async def get_by_id(notice_id: int, service: NoticeService = Depends(get_notice_service)):
    return await service.get_one(notice_id)

@router.get("", response_model=List[NoticeResponseTo], status_code=HTTPStatus.OK)
async def get_all(service: NoticeService = Depends(get_notice_service)):
    return await service.get_all()

@router.post("", response_model=NoticeResponseTo, status_code=HTTPStatus.CREATED)
async def create(dto: NoticeRequestTo, service: NoticeService = Depends(get_notice_service)):
    return await service.create(dto)

@router.put("/{notice_id}", response_model=NoticeResponseTo, status_code=HTTPStatus.OK)
async def update(notice_id: int, dto: NoticeRequestTo, service: NoticeService = Depends(get_notice_service)):
    return await service.update(notice_id, dto)

@router.delete("/{notice_id}", status_code=HTTPStatus.NO_CONTENT)
async def delete(notice_id: int, service: NoticeService = Depends(get_notice_service)):
    await service.delete(notice_id)