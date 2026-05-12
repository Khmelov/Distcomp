from fastapi import APIRouter, Depends, Query, HTTPException
from Task350.discussion.services.post_service import PostService
from Task350.discussion.domain.repositories.post_repository import CassandraPostRepository
from Task350.discussion.infrastructure.database import get_db

router = APIRouter(prefix="/posts")

def get_post_service(session=Depends(get_db)):
    repo = CassandraPostRepository(session)
    return PostService(repo)


@router.get("/by_tweet/{tweet_id}")
async def get_posts_by_tweet(tweet_id: int, page: int = Query(1), size: int = Query(20), service=Depends(get_post_service)):
    return await service.get_all(tweet_id, page, size)

@router.get("/{tweet_id}/{post_id}")
async def get_post(tweet_id: int, post_id: int, service=Depends(get_post_service)):
    return await service.get_one(tweet_id, post_id)

@router.post("/{tweet_id}")
async def create_post(tweet_id: int, content: str, service=Depends(get_post_service)):
    return await service.create(tweet_id, content)

@router.put("/{tweet_id}/{post_id}")
async def update_post(tweet_id: int, post_id: int, content: str, service=Depends(get_post_service)):
    return await service.update(tweet_id, post_id, content)

@router.delete("/{tweet_id}/{post_id}")
async def delete_post(tweet_id: int, post_id: int, service=Depends(get_post_service)):
    await service.delete(tweet_id, post_id)
    return {"status": "deleted"}

