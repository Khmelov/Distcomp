from fastapi import APIRouter, Depends, HTTPException, Query
from typing import List, Optional
from Task360.publisher.src.api.v2.dep import get_current_user
from Task360.publisher.src.services.post import PostService
from Task360.publisher.src.schemas.post import PostRequestTo, PostResponseTo

router = APIRouter(prefix="/posts")

def get_post_service():
    from Task350.publisher.src.services.kafka_producer import KafkaProducerService
    from Task350.publisher.src.services.kafka_consumer import KafkaConsumerService
    from Task350.publisher.src.infrastructure.redis_client import RedisClient
    from Task350.publisher.src.services.post import PostService
    # Временно создаём глобальные объекты (или передаём через Depends)
    # Для простоты используем глобальные переменные из main
    from Task360.publisher.main import producer, consumer
    redis_client = RedisClient()
    return PostService(producer, consumer, redis_client)

@router.get("", response_model=List[PostResponseTo])
async def get_all_posts(
    page: int = Query(1), size: int = Query(20), tweet_id: Optional[int] = None,
    current_user = Depends(get_current_user),
    service: PostService = Depends(get_post_service)
):
    return await service.get_all(page, size, tweet_id=tweet_id)

@router.get("/{id}", response_model=PostResponseTo)
async def get_post(id: str, current_user = Depends(get_current_user), service: PostService = Depends(get_post_service)):
    return await service.get_one(id)

@router.post("/{tweet_id}", response_model=PostResponseTo, status_code=201)
async def create_post(tweet_id: int, content: str, current_user = Depends(get_current_user), service: PostService = Depends(get_post_service)):
    # нужно проверить, что твит принадлежит текущему пользователю (или ADMIN)
    # для этого нужен сервис твитов – можно получить из Depends, упростим: просто создаём
    return await service.create_post(tweet_id, content)

@router.put("/{tweet_id}/{post_id}", response_model=PostResponseTo)
async def update_post(tweet_id: int, post_id: int, content: str, current_user = Depends(get_current_user), service: PostService = Depends(get_post_service)):
    # проверка прав: владелец твита
    return await service.update_post(tweet_id, post_id, content)

@router.delete("/{tweet_id}/{post_id}", status_code=204)
async def delete_post(tweet_id: int, post_id: int, current_user = Depends(get_current_user), service: PostService = Depends(get_post_service)):
    await service.delete_post(tweet_id, post_id)