from fastapi import APIRouter, status, Depends
from sqlalchemy.orm import Session

from app.db.database import get_db
from app.dto.topic import TopicRequestTo, TopicResponseTo
from app.services.topic_service import TopicService

router = APIRouter()


@router.post("/topics",
             response_model=TopicResponseTo,
             status_code=status.HTTP_201_CREATED)
def create_topic(dto: TopicRequestTo, db: Session = Depends(get_db)):
    return TopicService(db).create(dto)


@router.get("/topics", response_model=list[TopicResponseTo])
def get_topics(db: Session = Depends(get_db)):
    return TopicService(db).find_all()


@router.get("/topics/{id}", response_model=TopicResponseTo)
def get_topic(id: int, db: Session = Depends(get_db)):
    return TopicService(db).find_by_id(id)


@router.put("/topics/{id}", response_model=TopicResponseTo)
def update_topic(id: int, dto: TopicRequestTo, db: Session = Depends(get_db)):
    return TopicService(db).update(id, dto)


@router.delete("/topics/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_topic(id: int, db: Session = Depends(get_db)):
    TopicService(db).delete(id)
