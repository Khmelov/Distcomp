from fastapi import APIRouter, status, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.database import get_db
from app.schemas.notice import NoticeRequestTo, NoticeResponseTo
from app.services.notice_service import NoticeService
from typing import List

router = APIRouter()
service = NoticeService()

@router.post("", response_model=NoticeResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: NoticeRequestTo, session: AsyncSession = Depends(get_db)):
    return await service.create(session, dto)

@router.get("", response_model=List[NoticeResponseTo])
async def get_all(page: int = 1, session: AsyncSession = Depends(get_db)):
    return await service.get_all(session, page)

@router.get("/{id}", response_model=NoticeResponseTo)
async def get_by_id(id: int, session: AsyncSession = Depends(get_db)):
    return await service.get_by_id(session, id)

@router.put("/{id}", response_model=NoticeResponseTo)
async def update(id: int, dto: NoticeRequestTo, session: AsyncSession = Depends(get_db)):
    return await service.update(session, id, dto)

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int, session: AsyncSession = Depends(get_db)):
    await service.delete(session, id)