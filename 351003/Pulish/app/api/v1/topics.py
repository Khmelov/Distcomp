from fastapi import APIRouter, status
from app.dto.topic import TopicRequestTo, TopicResponseTo
from app.services.topic_service import TopicService

router = APIRouter()
service = TopicService()


@router.post("/topics",
             response_model=TopicResponseTo,
             status_code=status.HTTP_201_CREATED)
def create_topic(dto: TopicRequestTo):
    return service.create(dto)


@router.get("/topics", response_model=list[TopicResponseTo])
def get_topics():
    return service.find_all()


@router.get("/topics/{id}", response_model=TopicResponseTo)
def get_topic(id: int):
    return service.find_by_id(id)


@router.put("/topics/{id}", response_model=TopicResponseTo)
def update_topic(id: int, dto: TopicRequestTo):
    return service.update(id, dto)


@router.delete("/topics/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_topic(id: int):
    service.delete(id)
