from fastapi import APIRouter, Depends, Query
from typing import List, Optional
from http import HTTPStatus

from Task320.src.api.v1.dep import get_tweet_service
from Task320.src.schemas.tweet import TweetResponseTo, TweetRequestTo
from Task320.src.services.tweet import TweetService

router = APIRouter(prefix="/tweets")

@router.get("", response_model=List[TweetResponseTo], status_code=HTTPStatus.OK)
async def get_all(
    page: int = Query(1, ge=1),
    size: int = Query(20, ge=1, le=100),
    sort: str = Query("id"),
    service: TweetService = Depends(get_tweet_service)
):
    return await service.get_all(page, size, sort)

@router.get("/search", response_model=List[TweetResponseTo], status_code=HTTPStatus.OK)
async def search_tweets(
    marker_names: Optional[List[str]] = Query(None),
    marker_ids: Optional[List[int]] = Query(None),
    creator_login: Optional[str] = Query(None),
    title: Optional[str] = Query(None),
    content: Optional[str] = Query(None),
    page: int = Query(1, ge=1),
    size: int = Query(20, ge=1, le=100),
    sort: str = Query("id"),
    service: TweetService = Depends(get_tweet_service)
):
    return await service.search(
        marker_names=marker_names,
        marker_ids=marker_ids,
        creator_login=creator_login,
        title=title,
        content=content,
        page=page,
        size=size,
        sort=sort
    )

@router.get("/{tweet_id}", response_model=TweetResponseTo, status_code=HTTPStatus.OK)
async def get_by_id(tweet_id: str, service: TweetService = Depends(get_tweet_service)):
    return await service.get_one(tweet_id)

@router.post("", response_model=TweetResponseTo, status_code=HTTPStatus.CREATED)
async def create(dto: TweetRequestTo, service: TweetService = Depends(get_tweet_service)):
    return await service.create(dto)

@router.post("/{tweet_id}/markers", status_code=HTTPStatus.NO_CONTENT)
async def add_markers_to_tweet(
    tweet_id: int,
    marker_ids: List[int],
    service: TweetService = Depends(get_tweet_service)
):
    await service.add_markers(tweet_id, marker_ids)

@router.put("/{tweet_id}", response_model=TweetResponseTo, status_code=HTTPStatus.OK)
async def update(tweet_id: str, dto: TweetRequestTo, service: TweetService = Depends(get_tweet_service)):
    return await service.update(tweet_id, dto)

@router.delete("/{tweet_id}", status_code=HTTPStatus.NO_CONTENT)
async def delete(tweet_id: str, service: TweetService = Depends(get_tweet_service)):
    await service.delete(tweet_id)