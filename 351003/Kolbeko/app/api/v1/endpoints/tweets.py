from fastapi import APIRouter, status
from app.schemas.tweet import TweetRequestTo, TweetResponseTo
from app.services.tweet_service import TweetService
from typing import List

router = APIRouter()
service = TweetService()

@router.post("", response_model=TweetResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: TweetRequestTo):
    return service.create(dto)

@router.get("", response_model=List[TweetResponseTo])
async def get_all():
    return service.get_all()

@router.get("/{id}", response_model=TweetResponseTo)
async def get_by_id(id: int):
    return service.get_by_id(id)

@router.put("/{id}", response_model=TweetResponseTo)
async def update(id: int, dto: TweetRequestTo):
    return service.update(id, dto)

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int):
    service.delete(id)