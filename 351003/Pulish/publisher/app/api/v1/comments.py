from fastapi import APIRouter, status, Depends
from sqlalchemy.orm import Session
from app.db.database import get_db
from app.dto.comment import CommentRequestTo, CommentResponseTo
from app.services.comment_service import CommentService

router = APIRouter()


@router.post("/comments", response_model=CommentResponseTo,
             status_code=status.HTTP_201_CREATED)
def create_comment(dto: CommentRequestTo, db: Session = Depends(get_db)):
    return CommentService(db).create(dto)


@router.get("/comments", response_model=list[CommentResponseTo])
def get_comments(db: Session = Depends(get_db)):
    return CommentService(db).find_all()


@router.get("/comments/{id}", response_model=CommentResponseTo)
def get_comment(id: int, db: Session = Depends(get_db)):
    return CommentService(db).find_by_id(id)


@router.put("/comments/{id}", response_model=CommentResponseTo)
def update_comment(id: int, dto: CommentRequestTo,
                   db: Session = Depends(get_db)):
    dto.id = id
    return CommentService(db).update(dto)


@router.delete("/comments/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_comment(id: int, db: Session = Depends(get_db)):
    CommentService(db).delete(id)
