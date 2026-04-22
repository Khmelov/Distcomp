from fastapi import APIRouter, status, Depends, HTTPException
from sqlalchemy.orm import Session

from app.db.database import get_db
from app.dto.comment import CommentRequestTo, CommentResponseTo
from app.services.comment_service import CommentService
from app.core.security import get_current_user, require_admin
from app.models.user import User
from app.models.topic import Topic

router = APIRouter()


def _owns_topic(db: Session, topic_id: int, user_id: int) -> bool:
    topic = db.query(Topic).filter(Topic.id == topic_id).first()
    return topic is not None and topic.user_id == user_id


@router.post("/comments", response_model=CommentResponseTo,
             status_code=status.HTTP_201_CREATED)
def create_comment(
    dto: CommentRequestTo,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    if current_user.role != "ADMIN" and not _owns_topic(db, dto.topicId, current_user.id):
        raise HTTPException(
            status_code=403,
            detail={"errorMessage": "Forbidden", "errorCode": 40300},
        )
    return CommentService(db).create(dto)


@router.get("/comments", response_model=list[CommentResponseTo])
def get_comments(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    return CommentService(db).find_all()


@router.get("/comments/{id}", response_model=CommentResponseTo)
def get_comment(
    id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    return CommentService(db).find_by_id(id)


@router.put("/comments", response_model=CommentResponseTo)
def update_comment(
    dto: CommentRequestTo,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    if current_user.role != "ADMIN" and not _owns_topic(db, dto.topicId, current_user.id):
        raise HTTPException(
            status_code=403,
            detail={"errorMessage": "Forbidden", "errorCode": 40300},
        )
    return CommentService(db).update(dto)


@router.delete("/comments/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_comment(
    id: int,
    db: Session = Depends(get_db),
    _: User = Depends(require_admin),
):
    CommentService(db).delete(id)
