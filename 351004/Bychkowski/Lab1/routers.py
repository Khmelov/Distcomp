from fastapi import APIRouter, status
from typing import List
from schemas import (
    WriterRequestTo, WriterResponseTo,
    ArticleRequestTo, ArticleResponseTo,
    LabelRequestTo, LabelResponseTo,
    PostRequestTo, PostResponseTo
)
from services import WriterService, ArticleService, LabelService, PostService

router = APIRouter(prefix="/api/v1.0")

# --- Writer Endpoints ---
@router.post("/writers", response_model=WriterResponseTo, status_code=status.HTTP_201_CREATED)
def create_writer(dto: WriterRequestTo):
    return WriterService.create(dto)

@router.get("/writers", response_model=List[WriterResponseTo])
def get_writers():
    return WriterService.get_all()

@router.get("/writers/{id}", response_model=WriterResponseTo)
def get_writer(id: int):
    return WriterService.get_by_id(id)

@router.put("/writers/{id}", response_model=WriterResponseTo)
def update_writer(id: int, dto: WriterRequestTo):
    return WriterService.update(id, dto)

@router.delete("/writers/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_writer(id: int):
    WriterService.delete(id)
    return None

# --- Article Endpoints ---
@router.post("/articles", response_model=ArticleResponseTo, status_code=status.HTTP_201_CREATED)
def create_article(dto: ArticleRequestTo):
    return ArticleService.create(dto)

@router.get("/articles", response_model=List[ArticleResponseTo])
def get_articles():
    return ArticleService.get_all()

@router.get("/articles/{id}", response_model=ArticleResponseTo)
def get_article(id: int):
    return ArticleService.get_by_id(id)

@router.put("/articles/{id}", response_model=ArticleResponseTo)
def update_article(id: int, dto: ArticleRequestTo):
    return ArticleService.update(id, dto)

@router.delete("/articles/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_article(id: int):
    ArticleService.delete(id)
    return None

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

# --- Post Endpoints ---
@router.post("/posts", response_model=PostResponseTo, status_code=status.HTTP_201_CREATED)
def create_post(dto: PostRequestTo):
    return PostService.create(dto)

@router.get("/posts", response_model=List[PostResponseTo])
def get_posts():
    return PostService.get_all()

@router.get("/posts/{id}", response_model=PostResponseTo)
def get_post(id: int):
    return PostService.get_by_id(id)

@router.put("/posts/{id}", response_model=PostResponseTo)
def update_post(id: int, dto: PostRequestTo):
    return PostService.update(id, dto)

@router.delete("/posts/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_post(id: int):
    PostService.delete(id)
    return None