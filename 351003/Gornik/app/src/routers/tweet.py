from fastapi import APIRouter, HTTPException, Request
from sqlalchemy import select
from starlette import status

from dto import TweetRequestTo, TweetResponseTo
from routers.db_router import db_dependency
from models import Tweet

router = APIRouter(
    prefix="/api/v1.0/tweets",
    tags=["tweet"],
)

@router.get("", response_model=list[TweetResponseTo])
async def get_tweets(db: db_dependency):
    tweets = await db.execute(select(Tweet))
    tweets = tweets.scalars().all()
    return tweets

@router.get("/{tweet_id}", response_model=TweetResponseTo)
async def get_tweet(tweet_id: int, db: db_dependency):
    result = await db.execute(select(Tweet).where(Tweet.id == tweet_id))
    tweet = result.scalars().first()
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
        return tweet
    except Exception as e:
        await db.rollback()

        # 2. Return a proper 4xx error (400 Bad Request)
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
    return tweet

@router.delete("/{tweet_id}", status_code=204)
async def delete_tweet(tweet_id: int, db: db_dependency):
    tweet = await db.execute(select(Tweet).where(Tweet.id == tweet_id))
    tweet = tweet.scalars().first()
    if not tweet:
        raise HTTPException(status_code=404, detail="Tweet not found")
    await db.delete(tweet)
    await db.commit()

