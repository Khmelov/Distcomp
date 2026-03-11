from http import HTTPStatus
from typing import List

from fastapi import APIRouter, Depends

from src.api.v1.dep import get_tweet_service
from src.schemas.tweet import TweetResponseTo, TweetRequestTo
from src.services.tweet import TweetService

router = APIRouter(prefix="/tweets")

@router.get("", response_model=List[TweetResponseTo], status_code=HTTPStatus.OK)
def get_all(service: TweetService = Depends(get_tweet_service)):
    return service.get_all()

@router.get("/{tweet_id}", response_model=TweetResponseTo, status_code=HTTPStatus.OK)
def get_by_id(tweet_id: str, service: TweetService = Depends(get_tweet_service)):
    return service.get_one(tweet_id)

@router.post("", response_model=TweetResponseTo, status_code=HTTPStatus.CREATED)
def create(dto: TweetRequestTo, service: TweetService = Depends(get_tweet_service)):
    return service.create(dto)

@router.put("/{tweet_id}", response_model=TweetResponseTo, status_code=HTTPStatus.OK)
def update(tweet_id: str, dto: TweetRequestTo, service: TweetService = Depends(get_tweet_service)):
    return service.update(tweet_id, dto)

@router.delete("/{tweet_id}", status_code=HTTPStatus.NO_CONTENT)
def delete(tweet_id: str, service: TweetService = Depends(get_tweet_service)):
    service.delete(tweet_id)