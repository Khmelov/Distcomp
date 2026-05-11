from fastapi import APIRouter, Depends, HTTPException, status, Query, Response
from typing import List, Optional
from dto.requests import TopicRequestTo
from dto.responses import TopicResponseTo
from services.topic import TopicService
from dependencies import get_topic_service

router = APIRouter(prefix="/topics", tags=["topics"])


@router.get("", response_model=List[TopicResponseTo])
def get_all_topics(
    response: Response,
    page: int = Query(1, ge=1),
    size: int = Query(10, ge=1, le=100),
    sortBy: str = Query("id", pattern="^(id|userId|title|created|modified)$"),
    order: str = Query("asc", pattern="^(asc|desc)$"),
    userId: Optional[int] = Query(None),
    title: Optional[str] = Query(None),
    content: Optional[str] = Query(None),
    service: TopicService = Depends(get_topic_service),
):
    filters = {}
    if userId is not None:
        filters["user_id"] = userId  # колонка в БД — user_id
    if title:
        filters["title"] = title
    if content:
        filters["content"] = content

    # Маппинг sortBy на реальные колонки БД
    sort_mapping = {"userId": "user_id"}
    sort_column = sort_mapping.get(sortBy, sortBy)

    topics = service.get_list(
        filters=filters, page=page, size=size, sort_by=sort_column, order=order
    )
    total = service.count(filters)
    response.headers["X-Total-Count"] = str(total)
    return topics


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
