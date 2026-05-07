from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import select
from starlette import status as starlette_status

from auth import current_user_dependency, require_role
from dto import TweetRequestTo, TweetResponseTo
from models import Tweet, Writer
from routers.db_router import db_dependency
from redis_cache import cache_get, cache_set, cache_delete

router = APIRouter(
    prefix="/api/v2.0/tweets",
    tags=["v2-tweets"],
)

CACHE_PREFIX = "v2:tweet"


@router.get("", response_model=list[TweetResponseTo])
async def get_tweets(db: db_dependency, _user: current_user_dependency = None):
    cached = await cache_get(f"{CACHE_PREFIX}:all")
    if cached is not None:
        return cached
    tweets = await db.execute(select(Tweet))
    tweets = tweets.scalars().all()
    data = [TweetResponseTo.model_validate(t, from_attributes=True).model_dump(mode="json") for t in tweets]
    await cache_set(f"{CACHE_PREFIX}:all", data)
    return data


@router.get("/{tweet_id}", response_model=TweetResponseTo)
async def get_tweet(tweet_id: int, db: db_dependency, _user: current_user_dependency = None):
    cached = await cache_get(f"{CACHE_PREFIX}:{tweet_id}")
    if cached is not None:
        return cached
    result = await db.execute(select(Tweet).where(Tweet.id == tweet_id))
    tweet = result.scalars().first()
    if not tweet:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Tweet not found", "errorCode": 40404},
        )
    data = TweetResponseTo.model_validate(tweet, from_attributes=True).model_dump(mode="json")
    await cache_set(f"{CACHE_PREFIX}:{tweet_id}", data)
    return data


@router.post("", response_model=TweetResponseTo, status_code=201)
async def create_tweet(data: TweetRequestTo, db: db_dependency, _user: current_user_dependency = None):
    try:
        stmt = select(Tweet).where(Tweet.title == data.title)
        result = await db.execute(stmt)
        if result.scalars().first():
            raise HTTPException(
                status_code=starlette_status.HTTP_403_FORBIDDEN,
                detail={"errorMessage": "Tweet with this title already exists", "errorCode": 40303},
            )

        tweet = Tweet(**data.dict())
        db.add(tweet)
        await db.commit()

        resp = TweetResponseTo.model_validate(tweet, from_attributes=True).model_dump(mode="json")
        await cache_set(f"{CACHE_PREFIX}:{tweet.id}", resp)
        await cache_delete(f"{CACHE_PREFIX}:all")
        return resp
    except HTTPException:
        raise
    except Exception:
        await db.rollback()
        raise HTTPException(
            status_code=starlette_status.HTTP_403_FORBIDDEN,
            detail={"errorMessage": "Invalid association: writerId does not exist.", "errorCode": 40304},
        )


@router.put("/{tweet_id}", response_model=TweetResponseTo)
async def update_tweet(
    tweet_id: int,
    data: TweetRequestTo,
    db: db_dependency,
    _admin: Writer = Depends(require_role("ADMIN")),
):
    result = await db.execute(select(Tweet).where(Tweet.id == tweet_id))
    tweet = result.scalars().first()
    if not tweet:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Tweet not found", "errorCode": 40405},
        )
    for key, value in data.dict().items():
        setattr(tweet, key, value)
    db.add(tweet)
    await db.commit()
    await db.refresh(tweet)

    resp = TweetResponseTo.model_validate(tweet, from_attributes=True).model_dump(mode="json")
    await cache_set(f"{CACHE_PREFIX}:{tweet_id}", resp)
    await cache_delete(f"{CACHE_PREFIX}:all")
    return resp


@router.delete("/{tweet_id}", status_code=204)
async def delete_tweet(
    tweet_id: int,
    db: db_dependency,
    _admin: Writer = Depends(require_role("ADMIN")),
):
    result = await db.execute(select(Tweet).where(Tweet.id == tweet_id))
    tweet = result.scalars().first()
    if not tweet:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Tweet not found", "errorCode": 40406},
        )
    await db.delete(tweet)
    await db.commit()

    await cache_delete(f"{CACHE_PREFIX}:{tweet_id}")
    await cache_delete(f"{CACHE_PREFIX}:all")
