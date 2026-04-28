from fastapi import APIRouter, status, Depends, HTTPException
from sqlalchemy.orm import Session

from app.db.database import get_db
from app.dto.topic import TopicRequestTo, TopicResponseTo
from app.services.topic_service import TopicService
from app.core.security import get_current_user, require_admin
from app.models.user import User

router = APIRouter()


@router.post("/topics", response_model=TopicResponseTo,
             status_code=status.HTTP_201_CREATED)
def create_topic(
    dto: TopicRequestTo,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    return TopicService(db).create(dto)


@router.get("/topics", response_model=list[TopicResponseTo])
def get_topics(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    return TopicService(db).find_all()


@router.get("/topics/{id}", response_model=TopicResponseTo)
def get_topic(
    id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    return TopicService(db).find_by_id(id)


@router.put("/topics", response_model=TopicResponseTo)
def update_topic(
    dto: TopicRequestTo,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    if current_user.role != "ADMIN":
        topic = TopicService(db).find_by_id(dto.id)
        if topic.userId != current_user.id:
            raise HTTPException(
                status_code=403,
                detail={"errorMessage": "Forbidden", "errorCode": 40300},
            )
    return TopicService(db).update(dto)


@router.delete("/topics/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_topic(
    id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    if current_user.role != "ADMIN":
        topic = TopicService(db).find_by_id(id)
        if topic.userId != current_user.id:
            raise HTTPException(
                status_code=403,
                detail={"errorMessage": "Forbidden", "errorCode": 40300},
            )
    TopicService(db).delete(id)
