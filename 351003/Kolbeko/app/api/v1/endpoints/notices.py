from fastapi import APIRouter, status
from app.schemas.notice import NoticeRequestTo, NoticeResponseTo
from app.services.notice_service import NoticeService
from typing import List

router = APIRouter()
service = NoticeService()

@router.post("", response_model=NoticeResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: NoticeRequestTo):
    return service.create(dto)

@router.get("", response_model=List[NoticeResponseTo])
async def get_all():
    return service.get_all()

@router.get("/{id}", response_model=NoticeResponseTo)
async def get_by_id(id: int):
    return service.get_by_id(id)

@router.put("/{id}", response_model=NoticeResponseTo)
async def update(id: int, dto: NoticeRequestTo):
    return service.update(id, dto)

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int):
    service.delete(id)