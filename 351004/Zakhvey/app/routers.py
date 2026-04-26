from fastapi import APIRouter, status, Depends, Query
from sqlalchemy.orm import Session
from typing import List
from database import get_db
from schemas import (
    UserRequestTo, UserResponseTo,
    IssueRequestTo, ArticleResponseTo,
    LabelRequestTo, LabelResponseTo,
    CommentRequestTo, CommentResponseTo
)
from services import UserService, IssueService, LabelService, CommentService

router = APIRouter(prefix="/api/v1.0")

# --- Users Endpoints ---
@router.post("/users", response_model=UserResponseTo, status_code=status.HTTP_201_CREATED)
def create_user(dto: UserRequestTo, db: Session = Depends(get_db)):
    return UserService(db).create(dto)

@router.get("/users", response_model=List[UserResponseTo])
def get_users(skip: int = Query(0, ge=0), limit: int = Query(10, le=100), sort_by: str = "id", db: Session = Depends(get_db)):
    return UserService(db).get_all(skip=skip, limit=limit, sort_by=sort_by)

@router.get("/users/{id}", response_model=UserResponseTo)
def get_user(id: int, db: Session = Depends(get_db)):
    return UserService(db).get_by_id(id)

@router.put("/users/{id}", response_model=UserResponseTo)
def update_user(id: int, dto: UserRequestTo, db: Session = Depends(get_db)):
    return UserService(db).update(id, dto)

@router.delete("/users/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_user(id: int, db: Session = Depends(get_db)):
    UserService(db).delete(id)
    return None

# Аналогично обновляем остальные эндпоинты (передаем db и параметры пагинации)
# --- Issues Endpoints ---
@router.post("/issues", response_model=ArticleResponseTo, status_code=status.HTTP_201_CREATED)
def create_issue(dto: IssueRequestTo, db: Session = Depends(get_db)):
    return IssueService(db).create(dto)

@router.get("/issues", response_model=List[ArticleResponseTo])
def get_issues(skip: int = 0, limit: int = 10, sort_by: str = "id", db: Session = Depends(get_db)):
    return IssueService(db).get_all(skip, limit, sort_by)

@router.get("/issues/{id}", response_model=ArticleResponseTo)
def get_issue(id: int, db: Session = Depends(get_db)):
    return IssueService(db).get_by_id(id)

@router.put("/issues/{id}", response_model=ArticleResponseTo)
def update_issue(id: int, dto: IssueRequestTo, db: Session = Depends(get_db)):
    return IssueService(db).update(id, dto)

@router.delete("/issues/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_issue(id: int, db: Session = Depends(get_db)):
    IssueService(db).delete(id)
    return None

@router.get("/issues/{id}/labels", response_model=List[LabelResponseTo])
def get_issue_labels(id: int, db: Session = Depends(get_db)):
    return IssueService(db).get_labels_for_issue(id)

# --- Label Endpoints ---
@router.post("/labels", response_model=LabelResponseTo, status_code=status.HTTP_201_CREATED)
def create_label(dto: LabelRequestTo, db: Session = Depends(get_db)):
    return LabelService(db).create(dto)

@router.get("/labels", response_model=List[LabelResponseTo])
def get_labels(skip: int = 0, limit: int = 10, sort_by: str = "id", db: Session = Depends(get_db)):
    return LabelService(db).get_all(skip, limit, sort_by)

@router.get("/labels/{id}", response_model=LabelResponseTo)
def get_label(id: int, db: Session = Depends(get_db)):
    return LabelService(db).get_by_id(id)

@router.put("/labels/{id}", response_model=LabelResponseTo)
def update_label(id: int, dto: LabelRequestTo, db: Session = Depends(get_db)):
    return LabelService(db).update(id, dto)

@router.delete("/labels/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_label(id: int, db: Session = Depends(get_db)):
    LabelService(db).delete(id)
    return None

# --- Comments Endpoints ---
@router.post("/comments", response_model=CommentResponseTo, status_code=status.HTTP_201_CREATED)
def create_comment(dto: CommentRequestTo, db: Session = Depends(get_db)):
    return CommentService(db).create(dto)

@router.get("/comments", response_model=List[CommentResponseTo])
def get_comments(skip: int = 0, limit: int = 10, sort_by: str = "id", db: Session = Depends(get_db)):
    return CommentService(db).get_all(skip, limit, sort_by)

@router.get("/comments/{id}", response_model=CommentResponseTo)
def get_comment(id: int, db: Session = Depends(get_db)):
    return CommentService(db).get_by_id(id)

@router.put("/comments/{id}", response_model=CommentResponseTo)
def update_comment(id: int, dto: CommentRequestTo, db: Session = Depends(get_db)):
    return CommentService(db).update(id, dto)

@router.delete("/comments/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_comment(id: int, db: Session = Depends(get_db)):
    CommentService(db).delete(id)
    return None