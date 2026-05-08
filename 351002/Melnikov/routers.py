from fastapi import APIRouter, status
from typing import List
from schemas import *
from services import AuthorService, IssueService, TagService, CommentService

router = APIRouter(prefix="/api/v1.0")

# --- Authors ---
@router.post("/authors", response_model=AuthorResponseTo, status_code=status.HTTP_201_CREATED)
def create_author(dto: AuthorRequestTo):
    return AuthorService().create(dto)

@router.get("/authors", response_model=List[AuthorResponseTo])
def get_authors():
    return AuthorService().get_all()

@router.get("/authors/{id}", response_model=AuthorResponseTo)
def get_author(id: int):
    return AuthorService().get_by_id(id)

@router.put("/authors/{id}", response_model=AuthorResponseTo)
def update_author(id: int, dto: AuthorRequestTo):
    return AuthorService().update(id, dto)

@router.delete("/authors/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_author(id: int):
    AuthorService().delete(id)
    return None

# --- Issues ---
@router.post("/issues", response_model=IssueResponseTo, status_code=status.HTTP_201_CREATED)
def create_issue(dto: IssueRequestTo):
    return IssueService().create(dto)

@router.get("/issues", response_model=List[IssueResponseTo])
def get_issues():
    return IssueService().get_all()

@router.get("/issues/{id}", response_model=IssueResponseTo)
def get_issue(id: int):
    return IssueService().get_by_id(id)

@router.put("/issues/{id}", response_model=IssueResponseTo)
def update_issue(id: int, dto: IssueRequestTo):
    return IssueService().update(id, dto)

@router.delete("/issues/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_issue(id: int):
    IssueService().delete(id)
    return None

# --- Tags ---
@router.post("/tags", response_model=TagResponseTo, status_code=status.HTTP_201_CREATED)
def create_tag(dto: TagRequestTo):
    return TagService().create(dto)

@router.get("/tags", response_model=List[TagResponseTo])
def get_tags():
    return TagService().get_all()

@router.get("/tags/{id}", response_model=TagResponseTo)
def get_tag(id: int):
    return TagService().get_by_id(id)

@router.put("/tags/{id}", response_model=TagResponseTo)
def update_tag(id: int, dto: TagRequestTo):
    return TagService().update(id, dto)

@router.delete("/tags/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_tag(id: int):
    TagService().delete(id)
    return None

# --- Comments ---
@router.post("/comments", response_model=CommentResponseTo, status_code=status.HTTP_201_CREATED)
def create_comment(dto: CommentRequestTo):
    return CommentService().create(dto)

@router.get("/comments", response_model=List[CommentResponseTo])
def get_comments():
    return CommentService().get_all()

@router.get("/comments/{id}", response_model=CommentResponseTo)
def get_comment(id: int):
    return CommentService().get_by_id(id)

@router.put("/comments/{id}", response_model=CommentResponseTo)
def update_comment(id: int, dto: CommentRequestTo):
    return CommentService().update(id, dto)

@router.delete("/comments/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_comment(id: int):
    CommentService().delete(id)
    return None