from fastapi import APIRouter
from starlette import status

from src.api.dependencies import TweetServiceDep
from src.dto.tweet import TweetRequestTo, TweetResponseTo

router = APIRouter(prefix="/tweets", tags=["tweets"])


@router.get("", response_model=list[TweetResponseTo])
async def get_tweets(service: TweetServiceDep):
    return await service.get_all()


@router.get("/{tweet_id}", response_model=TweetResponseTo)
async def get_tweet(tweet_id: int, service: TweetServiceDep):
    return await service.get_by_id(tweet_id)


@router.post("", response_model=TweetResponseTo, status_code=status.HTTP_201_CREATED)
async def create_tweet(data: TweetRequestTo, service: TweetServiceDep):
    return await service.create(data)


@router.put("/{tweet_id}", response_model=TweetResponseTo)
async def update_tweet(tweet_id: int, data: TweetRequestTo, service: TweetServiceDep):
    return await service.update(tweet_id, data)


@router.delete("/{tweet_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_tweet(tweet_id: int, service: TweetServiceDep):
    await service.delete(tweet_id)
