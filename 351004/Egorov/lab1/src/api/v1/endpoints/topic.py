from http import HTTPStatus
from typing import List

from fastapi import APIRouter, Depends

from src.api.v1.dep import get_topic_service
from src.schemas.topic import TopicRequestTo, TopicResponseTo
from src.services import TopicService

router = APIRouter(prefix="/topics")

@router.get("/{topic_id}", response_model=TopicResponseTo, status_code=HTTPStatus.OK)
def get_by_id(topic_id: int, service: TopicService = Depends(get_topic_service)):
    return service.get_one(topic_id)

@router.get("", response_model=List[TopicResponseTo], status_code=HTTPStatus.OK)
def get_all(service: TopicService = Depends(get_topic_service)):
    return service.get_all()

@router.post("", response_model=TopicResponseTo, status_code=HTTPStatus.CREATED)
def create(dto: TopicRequestTo, service: TopicService = Depends(get_topic_service)):
    return service.create(dto)

@router.put("/{topic_id}", response_model=TopicResponseTo, status_code=HTTPStatus.OK)
def update(topic_id: int, dto: TopicRequestTo, service: TopicService = Depends(get_topic_service)):
    return service.update(topic_id, dto)

@router.delete("/{topic_id}", status_code=HTTPStatus.NO_CONTENT)
def delete(topic_id: int, service: TopicService = Depends(get_topic_service)):
    service.delete(topic_id)