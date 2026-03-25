from fastapi import APIRouter, status, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.database import get_db
from app.schemas.author import AuthorRequestTo, AuthorResponseTo
from app.services.author_service import AuthorService
from typing import List

router = APIRouter()
service = AuthorService()

@router.post("", response_model=AuthorResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: AuthorRequestTo, session: AsyncSession = Depends(get_db)):
    return await service.create(session, dto)

@router.get("", response_model=List[AuthorResponseTo])
async def get_all(page: int = 1, size: int = 100, session: AsyncSession = Depends(get_db)):
    # Увеличили size до 100, чтобы тест, ожидающий данные, их получил
    return await service.get_all(session, page, size)

@router.get("/{id}", response_model=AuthorResponseTo)
async def get_by_id(id: int, session: AsyncSession = Depends(get_db)):
    return await service.get_by_id(session, id)

@router.put("/{id}", response_model=AuthorResponseTo)
async def update(id: int, dto: AuthorRequestTo, session: AsyncSession = Depends(get_db)):
    return await service.update(session, id, dto)

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int, session: AsyncSession = Depends(get_db)):
    await service.delete(session, id)