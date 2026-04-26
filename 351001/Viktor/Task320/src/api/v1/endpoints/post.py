from fastapi import APIRouter, Depends, Query
from typing import List, Optional
from http import HTTPStatus

from Task320.src.api.v1.dep import get_post_service
from Task320.src.schemas.post import PostResponseTo, PostRequestTo
from Task320.src.services.post import PostService

router = APIRouter(prefix="/posts")

@router.get("", response_model=List[PostResponseTo], status_code=HTTPStatus.OK)
async def get_all(
    page: int = Query(1, ge=1),
    size: int = Query(20, ge=1, le=100),
    sort: str = Query("id"),
    content: Optional[str] = Query(None, description="Filter by content (partial match)"),
    tweet_id: Optional[int] = Query(None, description="Filter by tweet ID"),
    service: PostService = Depends(get_post_service)
):
    return await service.get_all(
        page=page, size=size, sort=sort,
        content=content, tweet_id=tweet_id
    )

@router.get("/{post_id}", response_model=PostResponseTo, status_code=HTTPStatus.OK)
async def get_by_id(post_id: str, service: PostService = Depends(get_post_service)):
    return await service.get_one(post_id)

@router.get("/by_tweet/{tweet_id}", response_model=List[PostResponseTo], status_code=HTTPStatus.OK)
async def get_by_tweet_id(tweet_id: int, service: PostService = Depends(get_post_service)):
    return await service.get_posts_by_tweet_id(tweet_id)

@router.post("", response_model=PostResponseTo, status_code=HTTPStatus.CREATED)
async def create(dto: PostRequestTo, service: PostService = Depends(get_post_service)):
    return await service.create(dto)

@router.put("/{post_id}", response_model=PostResponseTo, status_code=HTTPStatus.OK)
async def update(post_id: str, dto: PostRequestTo, service: PostService = Depends(get_post_service)):
    return await service.update(post_id, dto)

@router.delete("/{post_id}", status_code=HTTPStatus.NO_CONTENT)
async def delete(post_id: str, service: PostService = Depends(get_post_service)):
    await service.delete(post_id)