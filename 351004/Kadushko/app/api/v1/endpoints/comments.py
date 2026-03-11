from fastapi import APIRouter, Depends, status
from fastapi.responses import JSONResponse
from typing import List
from sqlalchemy.orm import Session
from app.database import get_db
from app.schemas.comment import CommentCreate, CommentUpdate, CommentResponse
from app.services.comment_service import CommentService

router = APIRouter(prefix="/comments", tags=["comments"])


@router.get("", response_model=List[CommentResponse], status_code=status.HTTP_200_OK)
def get_comments(
    page: int = 0,
    size: int = 10000,
    sort_by: str = "id",
    sort_order: str = "asc",
    db: Session = Depends(get_db)
):
    results = CommentService(db).get_all(page=page, size=size, sort_by=sort_by, sort_order=sort_order)
    return JSONResponse(content=[r.model_dump(by_alias=True) for r in results])


@router.get("/{comment_id}", response_model=CommentResponse, status_code=status.HTTP_200_OK)
def get_comment(comment_id: int, db: Session = Depends(get_db)):
    result = CommentService(db).get_by_id(comment_id)
    return JSONResponse(content=result.model_dump(by_alias=True))


@router.post("", response_model=CommentResponse, status_code=status.HTTP_201_CREATED)
def create_comment(data: CommentCreate, db: Session = Depends(get_db)):
    result = CommentService(db).create(data)
    return JSONResponse(status_code=201, content=result.model_dump(by_alias=True))


@router.put("/{comment_id}", response_model=CommentResponse, status_code=status.HTTP_200_OK)
def update_comment(comment_id: int, data: CommentUpdate, db: Session = Depends(get_db)):
    data.id = comment_id
    result = CommentService(db).update(data)
    return JSONResponse(content=result.model_dump(by_alias=True))


@router.delete("/{comment_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_comment(comment_id: int, db: Session = Depends(get_db)):
    CommentService(db).delete(comment_id)