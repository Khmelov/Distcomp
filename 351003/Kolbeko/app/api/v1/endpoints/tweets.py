from fastapi import APIRouter, status, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.database import get_db
from app.schemas.tweet import TweetRequestTo, TweetResponseTo
from app.services.tweet_service import TweetService
from app.core.exceptions import AppException
from typing import List

router = APIRouter()
service = TweetService()

@router.post("", response_model=TweetResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: TweetRequestTo, session: AsyncSession = Depends(get_db)):
    return await service.create(session, dto)

@router.get("", response_model=List[TweetResponseTo])
async def get_all(page: int = 1, session: AsyncSession = Depends(get_db)):
    return await service.get_all(session, page)

@router.get("/{id}", response_model=TweetResponseTo)
async def get_by_id(id: str, session: AsyncSession = Depends(get_db)):
    if not id.isdigit():
        raise AppException(400, "Invalid ID format", 40000)
    return await service.get_by_id(session, int(id))

@router.put("/{id}", response_model=TweetResponseTo)
async def update(id: str, dto: TweetRequestTo, session: AsyncSession = Depends(get_db)):
    if not id.isdigit():
        raise AppException(400, "Invalid ID format", 40000)
    return await service.update(session, int(id), dto)

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: str, session: AsyncSession = Depends(get_db)):
    if not id.isdigit():
        raise AppException(400, "Invalid ID format", 40000)
    await service.delete(session, int(id))