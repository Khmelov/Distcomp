from __future__ import annotations

from typing import Optional

from fastapi import APIRouter, Depends, FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from sqlalchemy import text
from sqlalchemy.orm import Session

from app import crud, schemas
from app.db import get_session, init_db
from app.exceptions import AppError, error_code

API_PREFIX = "/api/v1.0"


def get_db():
    with get_session() as session:
        yield session


app = FastAPI(title="Task320 Python API", version="1.0.0")


@app.on_event("startup")
def on_startup():
    init_db()
    # seed first creator if missing (no schema, public tables)
    with get_session() as session:
        existing = session.execute(
            text("select id from tbl_creator where login = 'sirilium.fox@icloud.com'")
        ).first()
        if not existing:
            session.execute(
                text(
                    "insert into tbl_creator (login, password, firstname, lastname) "
                    "values ('sirilium.fox@icloud.com','P@ssw0rd!23','Максим','Полевич')"
                )
            )


@app.exception_handler(AppError)
async def handle_app_error(request: Request, exc: AppError):
    return JSONResponse(
        status_code=exc.status_code,
        content={"errorMessage": exc.error_message, "errorCode": exc.error_code},
    )


@app.exception_handler(RequestValidationError)
async def handle_validation_error(request: Request, exc: RequestValidationError):
    return JSONResponse(
        status_code=400,
        content={"errorMessage": "Invalid request payload", "errorCode": error_code(400, 2), "details": exc.errors()},
    )


router = APIRouter(prefix=API_PREFIX)


def pagination(page: int = 0, size: int = 10):
    page = max(page, 0)
    size = max(min(size, 100), 1)
    return page, size


@router.get("/creators", response_model=list[schemas.CreatorResponse])
def list_creators(login: Optional[str] = None, page: int = 0, size: int = 10, db: Session = Depends(get_db)):
    page, size = pagination(page, size)
    return crud.list_creators(db, login, page, size)


@router.get("/creators/{creator_id}", response_model=schemas.CreatorResponse)
def get_creator(creator_id: int, db: Session = Depends(get_db)):
    return crud.get_creator(db, creator_id)


@router.post("/creators", response_model=schemas.CreatorResponse, status_code=201)
def create_creator(payload: schemas.CreatorRequest, db: Session = Depends(get_db)):
    return crud.create_creator(db, payload)


@router.put("/creators/{creator_id}", response_model=schemas.CreatorResponse)
def update_creator(creator_id: int, payload: schemas.CreatorRequest, db: Session = Depends(get_db)):
    return crud.update_creator(db, creator_id, payload)


@router.put("/creators", response_model=schemas.CreatorResponse)
def update_creator_body(payload: schemas.CreatorUpdate, db: Session = Depends(get_db)):
    return crud.update_creator(db, payload.id, payload)


@router.delete("/creators/{creator_id}", status_code=204)
def delete_creator(creator_id: int, db: Session = Depends(get_db)):
    crud.delete_creator(db, creator_id)


@router.get("/tags", response_model=list[schemas.TagResponse])
def list_tags(name: Optional[str] = None, page: int = 0, size: int = 10, db: Session = Depends(get_db)):
    page, size = pagination(page, size)
    return crud.list_tags(db, name, page, size)


@router.get("/tags/{tag_id}", response_model=schemas.TagResponse)
def get_tag(tag_id: int, db: Session = Depends(get_db)):
    return crud.get_tag(db, tag_id)


@router.post("/tags", response_model=schemas.TagResponse, status_code=201)
def create_tag(payload: schemas.TagRequest, db: Session = Depends(get_db)):
    return crud.create_tag(db, payload)


@router.put("/tags/{tag_id}", response_model=schemas.TagResponse)
def update_tag(tag_id: int, payload: schemas.TagRequest, db: Session = Depends(get_db)):
    return crud.update_tag(db, tag_id, payload)


@router.put("/tags", response_model=schemas.TagResponse)
def update_tag_body(payload: schemas.TagUpdate, db: Session = Depends(get_db)):
    return crud.update_tag(db, payload.id, payload)


@router.delete("/tags/{tag_id}", status_code=204)
def delete_tag(tag_id: int, db: Session = Depends(get_db)):
    crud.delete_tag(db, tag_id)


@router.get("/articles", response_model=list[schemas.ArticleResponse])
def list_articles(creatorId: Optional[int] = None, page: int = 0, size: int = 10, db: Session = Depends(get_db)):
    page, size = pagination(page, size)
    return crud.list_articles(db, creatorId, page, size)


@router.get("/articles/{article_id}", response_model=schemas.ArticleResponse)
def get_article(article_id: int, db: Session = Depends(get_db)):
    return crud.get_article(db, article_id)


@router.post("/articles", response_model=schemas.ArticleResponse, status_code=201)
def create_article(payload: schemas.ArticleRequest, db: Session = Depends(get_db)):
    return crud.create_article(db, payload)


@router.put("/articles/{article_id}", response_model=schemas.ArticleResponse)
def update_article(article_id: int, payload: schemas.ArticleRequest, db: Session = Depends(get_db)):
    return crud.update_article(db, article_id, payload)


@router.put("/articles", response_model=schemas.ArticleResponse)
def update_article_body(payload: schemas.ArticleUpdate, db: Session = Depends(get_db)):
    return crud.update_article(db, payload.id, payload)


@router.delete("/articles/{article_id}", status_code=204)
def delete_article(article_id: int, db: Session = Depends(get_db)):
    crud.delete_article(db, article_id)


@router.get("/articles/{article_id}/creator", response_model=schemas.CreatorResponse)
def get_article_creator(article_id: int, db: Session = Depends(get_db)):
    return crud.get_article_creator(db, article_id)


@router.get("/articles/{article_id}/tags", response_model=list[schemas.TagResponse])
def get_article_tags(article_id: int, db: Session = Depends(get_db)):
    return crud.get_article_tags(db, article_id)


@router.get("/articles/{article_id}/messages", response_model=list[schemas.MessageResponse])
def get_article_messages(article_id: int, db: Session = Depends(get_db)):
    return crud.get_article_messages(db, article_id)


@router.get("/messages", response_model=list[schemas.MessageResponse])
def list_messages(articleId: Optional[int] = None, page: int = 0, size: int = 10, db: Session = Depends(get_db)):
    page, size = pagination(page, size)
    return crud.list_messages(db, articleId, page, size)


@router.get("/messages/{message_id}", response_model=schemas.MessageResponse)
def get_message(message_id: int, db: Session = Depends(get_db)):
    return crud.get_message(db, message_id)


@router.post("/messages", response_model=schemas.MessageResponse, status_code=201)
def create_message(payload: schemas.MessageRequest, db: Session = Depends(get_db)):
    return crud.create_message(db, payload)


@router.put("/messages/{message_id}", response_model=schemas.MessageResponse)
def update_message(message_id: int, payload: schemas.MessageRequest, db: Session = Depends(get_db)):
    return crud.update_message(db, message_id, payload)


@router.put("/messages", response_model=schemas.MessageResponse)
def update_message_body(payload: schemas.MessageUpdate, db: Session = Depends(get_db)):
    return crud.update_message(db, payload.id, payload)


@router.delete("/messages/{message_id}", status_code=204)
def delete_message(message_id: int, db: Session = Depends(get_db)):
    crud.delete_message(db, message_id)


app.include_router(router)
