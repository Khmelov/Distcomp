from fastapi import APIRouter, HTTPException
from starlette import status

from src.api.dependencies import TweetServiceDep
from src.api.v2.deps import CurrentUserDep, ensure_tweet_write
from src.dto.tweet import TweetRequestTo, TweetResponseTo
from src.models.user_role import UserRole

router = APIRouter(prefix="/tweets", tags=["tweets-v2"])


@router.get("", response_model=list[TweetResponseTo])
async def list_tweets(service: TweetServiceDep, user: CurrentUserDep):
    return await service.get_all()


@router.get("/{tweet_id}", response_model=TweetResponseTo)
async def get_tweet(tweet_id: int, service: TweetServiceDep, user: CurrentUserDep):
    return await service.get_by_id(tweet_id)


@router.post("", response_model=TweetResponseTo, status_code=status.HTTP_201_CREATED)
async def create_tweet_v2(
    data: TweetRequestTo,
    service: TweetServiceDep,
    user: CurrentUserDep,
) -> TweetResponseTo:
    ensure_tweet_write(user, data.editor_id)
    return await service.create(data)


@router.put("/{tweet_id}", response_model=TweetResponseTo)
async def update_tweet_v2(
    tweet_id: int,
    data: TweetRequestTo,
    service: TweetServiceDep,
    user: CurrentUserDep,
) -> TweetResponseTo:
    existing = await service.get_by_id(tweet_id)
    if user.role != UserRole.ADMIN:
        if existing.editor_id != user.editor_id:
            raise HTTPException(status_code=403, detail="Forbidden")
    ensure_tweet_write(user, data.editor_id)
    return await service.update(tweet_id, data)


@router.delete("/{tweet_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_tweet_v2(tweet_id: int, service: TweetServiceDep, user: CurrentUserDep):
    existing = await service.get_by_id(tweet_id)
    if user.role != UserRole.ADMIN and existing.editor_id != user.editor_id:
        raise HTTPException(status_code=403, detail="Forbidden")
    await service.delete(tweet_id)
