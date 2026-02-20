from fastapi import APIRouter, status, Response
from typing import List
from app.dtos.tweet_dto import TweetRequestTo, TweetResponseTo
from app.services.tweet_service import TweetService

router = APIRouter(prefix="/api/v1.0/tweets", tags=["tweets"])
service = TweetService()

@router.post("", status_code=status.HTTP_201_CREATED, response_model=TweetResponseTo)
def create_tweet(dto: TweetRequestTo):
    return service.create(dto)

@router.get("", response_model=List[TweetResponseTo])
def get_tweets():
    return service.get_all()

@router.get("/{id}", response_model=TweetResponseTo)
def get_tweet(id: int):
    return service.get_by_id(id)

@router.put("/{id}", response_model=TweetResponseTo)
def update_tweet(id: int, dto: TweetRequestTo):
    return service.update(id, dto)

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_tweet(id: int):
    service.delete(id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
