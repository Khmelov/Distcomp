from fastapi import APIRouter, status, Depends
from typing import List
from sqlalchemy.orm import Session
from schemas import *
from services import WriterService, ArticleService, LabelService, PostService, AuthService
from database import get_db
from models import Writer
from security import get_current_user
from exceptions import AppError

router_v2 = APIRouter(prefix="/api/v2.0")

# --- AUTH ---
@router_v2.post("/writers", response_model=WriterResponseTo, status_code=status.HTTP_201_CREATED)
def register_writer(dto: WriterRequestTo, db: Session = Depends(get_db)):
    return WriterService(db).create(dto)

@router_v2.post("/login", response_model=TokenResponseTo)
def login(dto: LoginRequestTo, db: Session = Depends(get_db)):
    return AuthService(db).login(dto)

# --- WRITERS ---
@router_v2.get("/writers", response_model=List[WriterResponseTo])
def get_writers(db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    return WriterService(db).get_all()

@router_v2.get("/writers/{id}", response_model=WriterResponseTo)
def get_writer(id: int, db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    return WriterService(db).get_by_id(id)

@router_v2.put("/writers/{id}", response_model=WriterResponseTo)
def update_writer(id: int, dto: WriterRequestTo, db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    if current_user.role != 'ADMIN' and current_user.id != id:
        raise AppError(403, 40300, "Access denied. You can only edit your own profile.")
    return WriterService(db).update(id, dto)

@router_v2.delete("/writers/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_writer(id: int, db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    if current_user.role != 'ADMIN' and current_user.id != id:
        raise AppError(403, 40300, "Access denied.")
    return WriterService(db).delete(id)

# --- ARTICLES ---
@router_v2.post("/articles", response_model=ArticleResponseTo, status_code=status.HTTP_201_CREATED)
def create_article(dto: ArticleRequestTo, db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    if current_user.role != 'ADMIN' and current_user.id != dto.writerId:
        raise AppError(403, 40300, "Access denied. You can only create articles for yourself.")
    return ArticleService(db).create(dto)

@router_v2.get("/articles", response_model=List[ArticleResponseTo])
def get_articles(db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    return ArticleService(db).get_all()

@router_v2.get("/articles/{id}", response_model=ArticleResponseTo)
def get_article(id: int, db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    return ArticleService(db).get_by_id(id)

@router_v2.put("/articles/{id}", response_model=ArticleResponseTo)
def update_article(id: int, dto: ArticleRequestTo, db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    article = ArticleService(db).get_by_id(id)
    if current_user.role != 'ADMIN' and current_user.id != article.writerId:
        raise AppError(403, 40300, "Access denied. You can only edit your own articles.")
    return ArticleService(db).update(id, dto)

@router_v2.delete("/articles/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_article(id: int, db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    article = ArticleService(db).get_by_id(id)
    if current_user.role != 'ADMIN' and current_user.id != article.writerId:
        raise AppError(403, 40300, "Access denied.")
    return ArticleService(db).delete(id)

# --- LABELS ---
@router_v2.post("/labels", response_model=LabelResponseTo, status_code=status.HTTP_201_CREATED)
def create_label(dto: LabelRequestTo, db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    if current_user.role != 'ADMIN': raise AppError(403, 40300, "Access denied. Admin role required.")
    return LabelService(db).create(dto)

@router_v2.get("/labels", response_model=List[LabelResponseTo])
def get_labels(db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    return LabelService(db).get_all()

@router_v2.get("/labels/{id}", response_model=LabelResponseTo)
def get_label(id: int, db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    return LabelService(db).get_by_id(id)

@router_v2.put("/labels/{id}", response_model=LabelResponseTo)
def update_label(id: int, dto: LabelRequestTo, db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    if current_user.role != 'ADMIN': raise AppError(403, 40300, "Access denied.")
    return LabelService(db).update(id, dto)

@router_v2.delete("/labels/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_label(id: int, db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    if current_user.role != 'ADMIN': raise AppError(403, 40300, "Access denied.")
    return LabelService(db).delete(id)

# --- POSTS ---
@router_v2.post("/posts", response_model=PostResponseTo, status_code=status.HTTP_201_CREATED)
def create_post(dto: PostRequestTo, db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    return PostService(db).create(dto)

@router_v2.get("/posts", response_model=List[PostResponseTo])
def get_posts(db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    return PostService(db).get_all()

@router_v2.get("/posts/{id}", response_model=PostResponseTo)
def get_post(id: int, db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    return PostService(db).get_by_id(id)

@router_v2.put("/posts/{id}", response_model=PostResponseTo)
def update_post(id: int, dto: PostRequestTo, db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    if current_user.role != 'ADMIN': raise AppError(403, 40300, "Access denied.")
    return PostService(db).update(id, dto)

@router_v2.delete("/posts/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_post(id: int, db: Session = Depends(get_db), current_user: Writer = Depends(get_current_user)):
    if current_user.role != 'ADMIN': raise AppError(403, 40300, "Access denied.")
    return PostService(db).delete(id)