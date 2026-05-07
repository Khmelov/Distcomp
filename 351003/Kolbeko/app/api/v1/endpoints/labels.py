from fastapi import APIRouter, status, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.database import get_db
from app.schemas.label import LabelRequestTo, LabelResponseTo
from app.services.label_service import LabelService
from typing import List

router = APIRouter()
service = LabelService()

@router.post("", response_model=LabelResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: LabelRequestTo, session: AsyncSession = Depends(get_db)):
    return await service.create(session, dto)

@router.get("", response_model=List[LabelResponseTo])
async def get_all(page: int = 1, session: AsyncSession = Depends(get_db)):
    return await service.get_all(session, page)

@router.get("/{id}", response_model=LabelResponseTo)
async def get_by_id(id: int, session: AsyncSession = Depends(get_db)):
    return await service.get_by_id(session, id)

@router.put("/{id}", response_model=LabelResponseTo)
async def update(id: int, dto: LabelRequestTo, session: AsyncSession = Depends(get_db)):
    return await service.update(session, id, dto)

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int, session: AsyncSession = Depends(get_db)):
    await service.delete(session, id)