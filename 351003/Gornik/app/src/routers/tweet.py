from fastapi import APIRouter, HTTPException
from sqlalchemy import select
from starlette import status

from dto import TweetRequestTo, TweetResponseTo
from routers.db_router import db_dependency
from models import Tweet
from redis_cache import cache_get, cache_set, cache_delete

router = APIRouter(
    prefix="/api/v1.0/tweets",
    tags=["tweet"],
)

CACHE_PREFIX = "tweet"


@router.get("", response_model=list[TweetResponseTo])
async def get_tweets(db: db_dependency):
    cached = await cache_get(f"{CACHE_PREFIX}:all")
    if cached is not None:
        return cached
    tweets = await db.execute(select(Tweet))
    tweets = tweets.scalars().all()
    data = [TweetResponseTo.model_validate(t, from_attributes=True).model_dump(mode="json") for t in tweets]
    await cache_set(f"{CACHE_PREFIX}:all", data)
    return data


@router.get("/{tweet_id}", response_model=TweetResponseTo)
async def get_tweet(tweet_id: int, db: db_dependency):
    cached = await cache_get(f"{CACHE_PREFIX}:{tweet_id}")
    if cached is not None:
        return cached
    result = await db.execute(select(Tweet).where(Tweet.id == tweet_id))
    tweet = result.scalars().first()
    if tweet:
        data = TweetResponseTo.model_validate(tweet, from_attributes=True).model_dump(mode="json")
        await cache_set(f"{CACHE_PREFIX}:{tweet_id}", data)
        return data
    return tweet


@router.post("", status_code=201)
async def create_tweet(data: TweetRequestTo, db: db_dependency):
    try:
        stmt = select(Tweet).where(Tweet.title == data.title)
        result = await db.execute(stmt)
        ex_title = result.scalars().first()

        if ex_title:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Tweet with this title already exists"
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
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Invalid association: writerId does not exist."
        )


@router.put("/{tweet_id}", response_model=TweetResponseTo, status_code=200)
async def update_tweet(tweet_id: int, data: TweetRequestTo, db: db_dependency):
    tweet = await db.execute(select(Tweet).where(Tweet.id == tweet_id))
    tweet = tweet.scalars().first()
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
async def delete_tweet(tweet_id: int, db: db_dependency):
    tweet = await db.execute(select(Tweet).where(Tweet.id == tweet_id))
    tweet = tweet.scalars().first()
    if not tweet:
        raise HTTPException(status_code=404, detail="Tweet not found")
    await db.delete(tweet)
    await db.commit()

    await cache_delete(f"{CACHE_PREFIX}:{tweet_id}")
    await cache_delete(f"{CACHE_PREFIX}:all")
