from fastapi import APIRouter, Depends, HTTPException, Query
from typing import List, Optional
from Task360.publisher.src.api.v2.dep import get_current_user
from Task360.publisher.src.api.v1.dep import get_tweet_service
from Task360.publisher.src.schemas.tweet import TweetRequestTo, TweetResponseTo
from Task360.publisher.src.services.tweet import TweetService

router = APIRouter(prefix="/tweets")

@router.get("", response_model=List[TweetResponseTo])
async def get_all_tweets(
    page: int = Query(1), size: int = Query(20), sort: str = Query("id"),
    current_user = Depends(get_current_user),
    service: TweetService = Depends(get_tweet_service)
):
    return await service.get_all(page, size, sort)

@router.get("/{id}", response_model=TweetResponseTo)
async def get_tweet(id: str, current_user = Depends(get_current_user), service: TweetService = Depends(get_tweet_service)):
    return await service.get_one(id)

@router.post("", response_model=TweetResponseTo, status_code=201)
async def create_tweet(dto: TweetRequestTo, current_user = Depends(get_current_user), service: TweetService = Depends(get_tweet_service)):
    # создатель твита – текущий пользователь
    dto.creator_id = current_user.id
    return await service.create(dto)

@router.put("/{id}", response_model=TweetResponseTo)
async def update_tweet(id: str, dto: TweetRequestTo, current_user = Depends(get_current_user), service: TweetService = Depends(get_tweet_service)):
    # проверим, что твит принадлежит текущему пользователю (или ADMIN)
    tweet = await service.get_one(id)
    if current_user.role != 'ADMIN' and tweet.creator_id != current_user.id:
        raise HTTPException(status_code=403, detail="Not allowed")
    dto.creator_id = current_user.id
    return await service.update(id, dto)

@router.delete("/{id}", status_code=204)
async def delete_tweet(id: str, current_user = Depends(get_current_user), service: TweetService = Depends(get_tweet_service)):
    tweet = await service.get_one(id)
    if current_user.role != 'ADMIN' and tweet.creator_id != current_user.id:
        raise HTTPException(status_code=403, detail="Not allowed")
    await service.delete(id)