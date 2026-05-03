from fastapi import APIRouter, Depends, HTTPException, status
from typing import List
from dto.requests import TopicRequestTo
from dto.responses import TopicResponseTo
from services.topic import TopicService
from dependencies import get_topic_service

router = APIRouter(prefix="/topics", tags=["topics"])


@router.get("", response_model=List[TopicResponseTo])
def get_all_topics(service: TopicService = Depends(get_topic_service)):
    return service.get_all()


@router.get("/{id}", response_model=TopicResponseTo)
def get_topic(id: int, service: TopicService = Depends(get_topic_service)):
    topic = service.get(id)
    if not topic:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Topic not found"
        )
    return topic


@router.post("", response_model=TopicResponseTo, status_code=status.HTTP_201_CREATED)
def create_topic(
    request: TopicRequestTo, service: TopicService = Depends(get_topic_service)
):
    return service.create(request)


@router.put("/{id}", response_model=TopicResponseTo)
def update_topic(
    id: int, request: TopicRequestTo, service: TopicService = Depends(get_topic_service)
):
    try:
        return service.update(id, request)
    except ValueError as e:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=str(e))


@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_topic(id: int, service: TopicService = Depends(get_topic_service)):
    if not service.delete(id):
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Topic not found"
        )
