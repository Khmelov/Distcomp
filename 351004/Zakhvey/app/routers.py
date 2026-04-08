from fastapi import APIRouter, status
from typing import List
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
def create_user(dto: UserRequestTo):
    return UserService.create(dto)

@router.get("/users", response_model=List[UserResponseTo])
def get_users():
    return UserService.get_all()

@router.get("/users/{id}", response_model=UserResponseTo)
def get_user(id: int):
    return UserService.get_by_id(id)

@router.put("/users/{id}", response_model=UserResponseTo)
def update_user(id: int, dto: UserRequestTo):
    return UserService.update(id, dto)

@router.delete("/users/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_user(id: int):
    UserService.delete(id)
    return None

# --- Issues Endpoints ---
@router.post("/issues", response_model=ArticleResponseTo, status_code=status.HTTP_201_CREATED)
def create_issue(dto: IssueRequestTo):
    return IssueService.create(dto)

@router.get("/issues", response_model=List[ArticleResponseTo])
def get_issues():
    return IssueService.get_all()

@router.get("/issues/{id}", response_model=ArticleResponseTo)
def get_issue(id: int):
    return IssueService.get_by_id(id)

@router.put("/issues/{id}", response_model=ArticleResponseTo)
def update_issue(id: int, dto: IssueRequestTo):
    return IssueService.update(id, dto)

@router.delete("/issues/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_issue(id: int):
    IssueService.delete(id)
    return None

@router.get("/issues/{id}/labels", response_model=List[LabelResponseTo])
def get_issue_labels(id: int):
    return IssueService.get_labels_for_issue(id)

# --- Label Endpoints ---
@router.post("/labels", response_model=LabelResponseTo, status_code=status.HTTP_201_CREATED)
def create_label(dto: LabelRequestTo):
    return LabelService.create(dto)

@router.get("/labels", response_model=List[LabelResponseTo])
def get_labels():
    return LabelService.get_all()

@router.get("/labels/{id}", response_model=LabelResponseTo)
def get_label(id: int):
    return LabelService.get_by_id(id)

@router.put("/labels/{id}", response_model=LabelResponseTo)
def update_label(id: int, dto: LabelRequestTo):
    return LabelService.update(id, dto)

@router.delete("/labels/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_label(id: int):
    LabelService.delete(id)
    return None

# --- Comments Endpoints ---
@router.post("/comments", response_model=CommentResponseTo, status_code=status.HTTP_201_CREATED)
def create_post(dto: CommentRequestTo):
    return CommentService.create(dto)

@router.get("/comments", response_model=List[CommentResponseTo])
def get_posts():
    return CommentService.get_all()

@router.get("/comments/{id}", response_model=CommentResponseTo)
def get_post(id: int):
    return CommentService.get_by_id(id)

@router.put("/comments/{id}", response_model=CommentResponseTo)
def update_post(id: int, dto: CommentRequestTo):
    return CommentService.update(id, dto)

@router.delete("/comments/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_post(id: int):
    CommentService.delete(id)
    return None