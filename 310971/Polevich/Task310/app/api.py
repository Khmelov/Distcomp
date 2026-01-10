from __future__ import annotations

from typing import List, Optional

from fastapi import FastAPI, Query, Response, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from app import dto
from app.exceptions import AppError, make_error_code
from app.models import Article, Creator, Message, Tag
from app.services import ArticleService, CreatorService, MessageService, TagService
from app.storage import InMemoryRepository

app = FastAPI(title="Task310 REST API", version="1.0.0")
API_PREFIX = "/api/v1.0"

# Repositories
creator_repo = InMemoryRepository[Creator]()
article_repo = InMemoryRepository[Article]()
tag_repo = InMemoryRepository[Tag]()
message_repo = InMemoryRepository[Message]()

# Seed the expected first creator
if not creator_repo.list_all():
    creator_repo.create(
        Creator(
            id=0,
            login="sirilium.fox@icloud.com",
            password="P@ssw0rd!23",
            firstname="Максим",
            lastname="Полевич",
        )
    )

# Services
creator_service = CreatorService(creator_repo)
tag_service = TagService(tag_repo)
article_service = ArticleService(article_repo, creator_repo, tag_repo)
message_service = MessageService(message_repo, article_repo)


@app.exception_handler(AppError)
async def handle_app_error(request: Request, exc: AppError):
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "errorMessage": exc.error_message,
            "errorCode": exc.error_code,
            "details": exc.details or {},
        },
    )


@app.exception_handler(RequestValidationError)
async def handle_validation_error(request: Request, exc: RequestValidationError):
    return JSONResponse(
        status_code=400,
        content={
            "errorMessage": "Invalid request payload",
            "errorCode": make_error_code(400, 2),
            "details": exc.errors(),
        },
    )


# Creator endpoints
@app.get(f"{API_PREFIX}/creators", response_model=list[dto.CreatorResponseTo])
async def list_creators():
    return creator_service.list_creators()


@app.get(f"{API_PREFIX}/creators/{{creator_id}}", response_model=dto.CreatorResponseTo)
async def get_creator(creator_id: int):
    return creator_service.get_creator(creator_id)


@app.post(f"{API_PREFIX}/creators", status_code=201, response_model=dto.CreatorResponseTo)
async def create_creator(payload: dto.CreatorRequestTo):
    return creator_service.create_creator(payload)


@app.put(f"{API_PREFIX}/creators/{{creator_id}}", response_model=dto.CreatorResponseTo)
async def update_creator(creator_id: int, payload: dto.CreatorRequestTo):
    return creator_service.update_creator(creator_id, payload)


@app.delete(f"{API_PREFIX}/creators/{{creator_id}}", status_code=204)
async def delete_creator(creator_id: int):
    creator_service.delete_creator(creator_id)
    return Response(status_code=204)


# Tag endpoints
@app.get(f"{API_PREFIX}/tags", response_model=list[dto.TagResponseTo])
async def list_tags():
    return tag_service.list_tags()


@app.get(f"{API_PREFIX}/tags/{{tag_id}}", response_model=dto.TagResponseTo)
async def get_tag(tag_id: int):
    return tag_service.get_tag(tag_id)


@app.post(f"{API_PREFIX}/tags", status_code=201, response_model=dto.TagResponseTo)
async def create_tag(payload: dto.TagRequestTo):
    return tag_service.create_tag(payload)


@app.put(f"{API_PREFIX}/tags/{{tag_id}}", response_model=dto.TagResponseTo)
async def update_tag(tag_id: int, payload: dto.TagRequestTo):
    return tag_service.update_tag(tag_id, payload)


@app.delete(f"{API_PREFIX}/tags/{{tag_id}}", status_code=204)
async def delete_tag(tag_id: int):
    tag_service.delete_tag(tag_id)
    return Response(status_code=204)


# Article endpoints
@app.get(f"{API_PREFIX}/articles", response_model=list[dto.ArticleResponseTo])
async def list_articles():
    return article_service.list_articles()


@app.get(f"{API_PREFIX}/articles/{{article_id}}", response_model=dto.ArticleResponseTo)
async def get_article(article_id: int):
    return article_service.get_article(article_id)


@app.post(f"{API_PREFIX}/articles", status_code=201, response_model=dto.ArticleResponseTo)
async def create_article(payload: dto.ArticleRequestTo):
    return article_service.create_article(payload)


@app.put(f"{API_PREFIX}/articles/{{article_id}}", response_model=dto.ArticleResponseTo)
async def update_article(article_id: int, payload: dto.ArticleRequestTo):
    return article_service.update_article(article_id, payload)


@app.delete(f"{API_PREFIX}/articles/{{article_id}}", status_code=204)
async def delete_article(article_id: int):
    article_service.delete_article(article_id)
    return Response(status_code=204)


@app.get(f"{API_PREFIX}/articles/{{article_id}}/creator", response_model=dto.CreatorResponseTo)
async def get_creator_by_article(article_id: int):
    return article_service.get_creator_by_article(article_id)


@app.get(f"{API_PREFIX}/articles/{{article_id}}/tags", response_model=list[dto.TagResponseTo])
async def get_tags_by_article(article_id: int):
    return article_service.get_tags_by_article(article_id)


@app.get(f"{API_PREFIX}/articles/{{article_id}}/messages", response_model=list[dto.MessageResponseTo])
async def get_messages_by_article(article_id: int):
    return message_service.get_by_article(article_id)


@app.get(f"{API_PREFIX}/articles/search", response_model=list[dto.ArticleResponseTo])
async def search_articles(
    tagNames: Optional[List[str]] = Query(None),
    tagIds: Optional[List[int]] = Query(None),
    creatorLogin: Optional[str] = None,
    title: Optional[str] = None,
    content: Optional[str] = None,
):
    return article_service.search_articles(
        tag_names=tagNames,
        tag_ids=tagIds,
        creator_login=creatorLogin,
        title=title,
        content=content,
    )


# Message endpoints
@app.get(f"{API_PREFIX}/messages", response_model=list[dto.MessageResponseTo])
async def list_messages():
    return message_service.list_messages()


@app.get(f"{API_PREFIX}/messages/{{message_id}}", response_model=dto.MessageResponseTo)
async def get_message(message_id: int):
    return message_service.get_message(message_id)


@app.post(f"{API_PREFIX}/messages", status_code=201, response_model=dto.MessageResponseTo)
async def create_message(payload: dto.MessageRequestTo):
    return message_service.create_message(payload)


@app.put(f"{API_PREFIX}/messages/{{message_id}}", response_model=dto.MessageResponseTo)
async def update_message(message_id: int, payload: dto.MessageRequestTo):
    return message_service.update_message(message_id, payload)


@app.delete(f"{API_PREFIX}/messages/{{message_id}}", status_code=204)
async def delete_message(message_id: int):
    message_service.delete_message(message_id)
    return Response(status_code=204)
