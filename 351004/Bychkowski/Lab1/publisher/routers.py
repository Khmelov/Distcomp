from fastapi import APIRouter, status, Depends
from typing import List
from sqlalchemy.orm import Session
from schemas import *
from services import WriterService, ArticleService, LabelService, PostService
from database import get_db

router = APIRouter(prefix="/api/v1.0")

@router.post("/writers", response_model=WriterResponseTo, status_code=status.HTTP_201_CREATED)
def create_writer(dto: WriterRequestTo, db: Session = Depends(get_db)): return WriterService(db).create(dto)
@router.get("/writers", response_model=List[WriterResponseTo])
def get_writers(db: Session = Depends(get_db)): return WriterService(db).get_all()
@router.get("/writers/{id}", response_model=WriterResponseTo)
def get_writer(id: int, db: Session = Depends(get_db)): return WriterService(db).get_by_id(id)
@router.put("/writers/{id}", response_model=WriterResponseTo)
def update_writer(id: int, dto: WriterRequestTo, db: Session = Depends(get_db)): return WriterService(db).update(id, dto)
@router.delete("/writers/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_writer(id: int, db: Session = Depends(get_db)): return WriterService(db).delete(id)

@router.post("/articles", response_model=ArticleResponseTo, status_code=status.HTTP_201_CREATED)
def create_article(dto: ArticleRequestTo, db: Session = Depends(get_db)): return ArticleService(db).create(dto)
@router.get("/articles", response_model=List[ArticleResponseTo])
def get_articles(db: Session = Depends(get_db)): return ArticleService(db).get_all()
@router.get("/articles/{id}", response_model=ArticleResponseTo)
def get_article(id: int, db: Session = Depends(get_db)): return ArticleService(db).get_by_id(id)
@router.put("/articles/{id}", response_model=ArticleResponseTo)
def update_article(id: int, dto: ArticleRequestTo, db: Session = Depends(get_db)): return ArticleService(db).update(id, dto)
@router.delete("/articles/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_article(id: int, db: Session = Depends(get_db)): return ArticleService(db).delete(id)

@router.post("/labels", response_model=LabelResponseTo, status_code=status.HTTP_201_CREATED)
def create_label(dto: LabelRequestTo, db: Session = Depends(get_db)): return LabelService(db).create(dto)
@router.get("/labels", response_model=List[LabelResponseTo])
def get_labels(db: Session = Depends(get_db)): return LabelService(db).get_all()
@router.get("/labels/{id}", response_model=LabelResponseTo)
def get_label(id: int, db: Session = Depends(get_db)): return LabelService(db).get_by_id(id)
@router.put("/labels/{id}", response_model=LabelResponseTo)
def update_label(id: int, dto: LabelRequestTo, db: Session = Depends(get_db)): return LabelService(db).update(id, dto)
@router.delete("/labels/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_label(id: int, db: Session = Depends(get_db)): return LabelService(db).delete(id)

@router.post("/posts", response_model=PostResponseTo, status_code=status.HTTP_201_CREATED)
def create_post(dto: PostRequestTo, db: Session = Depends(get_db)): return PostService(db).create(dto)
@router.get("/posts", response_model=List[PostResponseTo])
def get_posts(db: Session = Depends(get_db)): return PostService(db).get_all()
@router.get("/posts/{id}", response_model=PostResponseTo)
def get_post(id: int, db: Session = Depends(get_db)): return PostService(db).get_by_id(id)
@router.put("/posts/{id}", response_model=PostResponseTo)
def update_post(id: int, dto: PostRequestTo, db: Session = Depends(get_db)): return PostService(db).update(id, dto)
@router.delete("/posts/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_post(id: int, db: Session = Depends(get_db)): return PostService(db).delete(id)