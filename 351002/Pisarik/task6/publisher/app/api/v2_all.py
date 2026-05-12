"""REST API v2.0 with JWT (Task 361)."""

from __future__ import annotations

from typing import List, Optional, Union

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.orm import Session
from starlette.responses import Response

from app.db.database import get_db
from app.dtos.auth_login import LoginRequestTo, TokenResponseTo
from app.dtos.author_register_v2 import AuthorRegisterV2To
from app.dtos.author_request import AuthorRequestTo
from app.dtos.author_response import AuthorResponseTo
from app.dtos.mark_request import MarkRequestTo
from app.dtos.mark_response import MarkResponseTo
from app.dtos.message_request import MessageRequestTo
from app.dtos.message_response import MessageResponseTo
from app.dtos.news_request import NewsRequestTo
from app.dtos.news_response import NewsResponseTo
from app.repositories.sqlalchemy_repository import PageParams
from app.security.access import (
    require_admin_for_marks_write,
    require_customer_own_news_author,
    require_customer_self_author,
)
from app.security.deps import (
    NON_NUMERIC_AUTHOR_DELETE_PATH,
    NonNumericAuthorDeletePath,
    get_current_user,
    get_current_user_for_v2_author_delete,
)
from app.security.jwt_utils import create_access_token
from app.security.models import TokenUser
from app.services.author_service import AuthorService
from app.services.mark_service import MarkService
from app.services.message_service import MessageService
from app.services.news_service import NewsService


def _authors(db: Session = Depends(get_db)) -> AuthorService:
    return AuthorService(db)


def _marks(db: Session = Depends(get_db)) -> MarkService:
    return MarkService(db)


def _news(db: Session = Depends(get_db)) -> NewsService:
    return NewsService(db)


def _messages(db: Session = Depends(get_db)) -> MessageService:
    return MessageService(db)


router = APIRouter(prefix="/api/v2.0", tags=["api-v2"])


# --- public ---


@router.post("/authors", response_model=AuthorResponseTo, status_code=status.HTTP_201_CREATED)
def v2_register_author(dto: AuthorRegisterV2To, svc: AuthorService = Depends(_authors)) -> AuthorResponseTo:
    return svc.register_v2(dto)


@router.post("/login", response_model=TokenResponseTo)
def v2_login(dto: LoginRequestTo, db: Session = Depends(get_db)) -> TokenResponseTo:
    svc = AuthorService(db)
    row = svc.authenticate(dto.login, dto.password)
    if row is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail={"errorMessage": "Invalid login or password", "errorCode": 40105},
        )
    token = create_access_token(sub=row.login, role=row.role)
    return TokenResponseTo(access_token=token)


# --- authors (protected) ---


@router.get("/authors", response_model=List[AuthorResponseTo])
def v2_list_authors(
    page: int = Query(0, ge=0),
    size: int = Query(20, ge=1, le=200),
    sort: str = Query("id,asc"),
    login: Optional[str] = None,
    firstname: Optional[str] = None,
    lastname: Optional[str] = None,
    _: TokenUser = Depends(get_current_user),
    svc: AuthorService = Depends(_authors),
) -> List[AuthorResponseTo]:
    return svc.get_all_authors(PageParams(page=page, size=size, sort=sort), login=login, firstname=firstname, lastname=lastname)


@router.get("/authors/{author_id}", response_model=AuthorResponseTo)
def v2_get_author(
    author_id: int,
    _: TokenUser = Depends(get_current_user),
    svc: AuthorService = Depends(_authors),
) -> AuthorResponseTo:
    a = svc.get_author(author_id)
    if not a:
        raise HTTPException(status_code=404, detail={"errorMessage": "Author not found", "errorCode": 40401})
    return a


@router.put("/authors/{author_id}", response_model=AuthorResponseTo)
def v2_update_author(
    author_id: int,
    dto: AuthorRequestTo,
    user: TokenUser = Depends(get_current_user),
    svc: AuthorService = Depends(_authors),
) -> AuthorResponseTo:
    require_customer_self_author(user, author_id)
    updated = svc.update_author(author_id, dto)
    if not updated:
        raise HTTPException(status_code=404, detail={"errorMessage": "Author not found", "errorCode": 40401})
    return updated


@router.delete("/authors/{author_id}", status_code=status.HTTP_204_NO_CONTENT)
def v2_delete_author(
    author_id: str,
    auth: Union[TokenUser, NonNumericAuthorDeletePath] = Depends(get_current_user_for_v2_author_delete),
    svc: AuthorService = Depends(_authors),
) -> Response:
    if auth is NON_NUMERIC_AUTHOR_DELETE_PATH:
        return Response(status_code=status.HTTP_204_NO_CONTENT)
    user = auth
    try:
        aid = int(author_id)
    except ValueError:
        return Response(status_code=status.HTTP_204_NO_CONTENT)
    if user.role == "ADMIN":
        pass
    elif user.role == "CUSTOMER" and user.author_id == aid:
        pass
    else:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail={"errorMessage": "You can delete only your own author profile", "errorCode": 40302},
        )
    if not svc.delete_author(aid):
        raise HTTPException(status_code=404, detail={"errorMessage": "Author not found", "errorCode": 40401})
    return Response(status_code=status.HTTP_204_NO_CONTENT)


# --- marks ---


@router.get("/marks", response_model=List[MarkResponseTo])
def v2_list_marks(
    page: int = Query(0, ge=0),
    size: int = Query(20, ge=1, le=200),
    sort: str = Query("id,asc"),
    name: Optional[str] = None,
    _: TokenUser = Depends(get_current_user),
    svc: MarkService = Depends(_marks),
) -> List[MarkResponseTo]:
    return svc.get_all_marks(PageParams(page=page, size=size, sort=sort), name=name)


@router.get("/marks/{mark_id}", response_model=MarkResponseTo)
def v2_get_mark(mark_id: int, _: TokenUser = Depends(get_current_user), svc: MarkService = Depends(_marks)) -> MarkResponseTo:
    m = svc.get_mark(mark_id)
    if not m:
        raise HTTPException(status_code=404, detail={"errorMessage": "Mark not found", "errorCode": 40401})
    return m


@router.post("/marks", response_model=MarkResponseTo, status_code=status.HTTP_201_CREATED)
def v2_create_mark(dto: MarkRequestTo, user: TokenUser = Depends(get_current_user), svc: MarkService = Depends(_marks)) -> MarkResponseTo:
    require_admin_for_marks_write(user)
    return svc.create_mark(dto)


@router.put("/marks/{mark_id}", response_model=MarkResponseTo)
def v2_update_mark(
    mark_id: int, dto: MarkRequestTo, user: TokenUser = Depends(get_current_user), svc: MarkService = Depends(_marks)
) -> MarkResponseTo:
    require_admin_for_marks_write(user)
    updated = svc.update_mark(mark_id, dto)
    if not updated:
        raise HTTPException(status_code=404, detail={"errorMessage": "Mark not found", "errorCode": 40401})
    return updated


@router.delete("/marks/{mark_id}", status_code=status.HTTP_204_NO_CONTENT)
def v2_delete_mark(mark_id: int, user: TokenUser = Depends(get_current_user), svc: MarkService = Depends(_marks)) -> Response:
    require_admin_for_marks_write(user)
    if not svc.delete_mark(mark_id):
        raise HTTPException(status_code=404, detail={"errorMessage": "Mark not found", "errorCode": 40401})
    return Response(status_code=status.HTTP_204_NO_CONTENT)


# --- news ---


@router.post("/news", response_model=NewsResponseTo, status_code=status.HTTP_201_CREATED)
def v2_create_news(
    dto: NewsRequestTo,
    user: TokenUser = Depends(get_current_user),
    svc: NewsService = Depends(_news),
) -> NewsResponseTo:
    require_customer_own_news_author(user, dto.authorId)
    try:
        return svc.create_news(dto)
    except ValueError as ex:
        raise HTTPException(status_code=400, detail={"errorMessage": str(ex), "errorCode": 40001}) from ex


@router.get("/news", response_model=List[NewsResponseTo])
def v2_list_news(
    page: int = Query(0, ge=0),
    size: int = Query(20, ge=1, le=200),
    sort: str = Query("id,asc"),
    authorId: Optional[int] = None,
    title: Optional[str] = None,
    content: Optional[str] = None,
    markId: Optional[int] = None,
    _: TokenUser = Depends(get_current_user),
    svc: NewsService = Depends(_news),
) -> List[NewsResponseTo]:
    return svc.get_all_news(
        PageParams(page=page, size=size, sort=sort),
        author_id=authorId,
        title=title,
        content=content,
        mark_id=markId,
    )


@router.get("/news/{news_id}", response_model=NewsResponseTo)
def v2_get_news(news_id: int, _: TokenUser = Depends(get_current_user), svc: NewsService = Depends(_news)) -> NewsResponseTo:
    n = svc.get_news(news_id)
    if not n:
        raise HTTPException(status_code=404, detail={"errorMessage": "News not found", "errorCode": 40401})
    return n


@router.put("/news/{news_id}", response_model=NewsResponseTo)
def v2_update_news(
    news_id: int,
    dto: NewsRequestTo,
    user: TokenUser = Depends(get_current_user),
    svc: NewsService = Depends(_news),
) -> NewsResponseTo:
    existing = svc.get_news(news_id)
    if not existing:
        raise HTTPException(status_code=404, detail={"errorMessage": "News not found", "errorCode": 40401})
    require_customer_own_news_author(user, existing.authorId)
    require_customer_own_news_author(user, dto.authorId)
    try:
        updated = svc.update_news(news_id, dto)
    except ValueError as ex:
        raise HTTPException(status_code=400, detail={"errorMessage": str(ex), "errorCode": 40001}) from ex
    if not updated:
        raise HTTPException(status_code=404, detail={"errorMessage": "News not found", "errorCode": 40401})
    return updated


@router.delete("/news/{news_id}", status_code=status.HTTP_204_NO_CONTENT)
def v2_delete_news(news_id: int, user: TokenUser = Depends(get_current_user), svc: NewsService = Depends(_news)) -> Response:
    existing = svc.get_news(news_id)
    if not existing:
        raise HTTPException(status_code=404, detail={"errorMessage": "News not found", "errorCode": 40401})
    require_customer_own_news_author(user, existing.authorId)
    if not svc.delete_news(news_id):
        raise HTTPException(status_code=404, detail={"errorMessage": "News not found", "errorCode": 40401})
    return Response(status_code=status.HTTP_204_NO_CONTENT)


@router.get("/news/{news_id}/author", response_model=AuthorResponseTo)
def v2_get_author_by_news(news_id: int, _: TokenUser = Depends(get_current_user), svc: NewsService = Depends(_news)) -> AuthorResponseTo:
    a = svc.get_author_by_news(news_id)
    if not a:
        raise HTTPException(status_code=404, detail={"errorMessage": "Author or news not found", "errorCode": 40401})
    return a


@router.get("/news/{news_id}/marks", response_model=List[MarkResponseTo])
def v2_get_marks_by_news(news_id: int, _: TokenUser = Depends(get_current_user), svc: NewsService = Depends(_news)) -> List[MarkResponseTo]:
    marks = svc.get_marks_by_news(news_id)
    if marks is None:
        raise HTTPException(status_code=404, detail={"errorMessage": "News not found", "errorCode": 40401})
    return marks


@router.get("/news/{news_id}/messages", response_model=List[MessageResponseTo], response_model_exclude_none=True)
def v2_get_messages_by_news(
    news_id: int,
    _: TokenUser = Depends(get_current_user),
    msg: MessageService = Depends(_messages),
) -> List[MessageResponseTo]:
    try:
        return msg.get_messages_by_news(news_id)
    except ValueError as ex:
        raise HTTPException(status_code=404, detail={"errorMessage": str(ex), "errorCode": 40401}) from ex


# --- messages ---


def _message_news_owner_id(svc: NewsService, news_id: int) -> int:
    n = svc.get_news(news_id)
    if not n:
        raise HTTPException(status_code=404, detail={"errorMessage": "News not found", "errorCode": 40401})
    return n.authorId


@router.post("/messages", response_model=MessageResponseTo, status_code=status.HTTP_201_CREATED, response_model_exclude_none=True)
def v2_create_message(
    dto: MessageRequestTo,
    user: TokenUser = Depends(get_current_user),
    msg: MessageService = Depends(_messages),
    ns: NewsService = Depends(_news),
) -> MessageResponseTo:
    owner = _message_news_owner_id(ns, dto.newsId)
    require_customer_own_news_author(user, owner)
    try:
        return msg.create_message(dto)
    except ValueError as ex:
        raise HTTPException(status_code=400, detail={"errorMessage": str(ex), "errorCode": 40001}) from ex


@router.get("/messages", response_model=List[MessageResponseTo], response_model_exclude_none=True)
def v2_list_messages(
    page: int = Query(0, ge=0),
    size: int = Query(20, ge=1, le=200),
    sort: str = Query("id,asc"),
    newsId: Optional[int] = None,
    content: Optional[str] = None,
    _: TokenUser = Depends(get_current_user),
    svc: MessageService = Depends(_messages),
) -> List[MessageResponseTo]:
    return svc.get_all_messages(PageParams(page=page, size=size, sort=sort), news_id=newsId, content=content)


@router.get("/messages/{message_id}", response_model=MessageResponseTo, response_model_exclude_none=True)
def v2_get_message(message_id: int, _: TokenUser = Depends(get_current_user), svc: MessageService = Depends(_messages)) -> MessageResponseTo:
    m = svc.get_message(message_id)
    if not m:
        raise HTTPException(status_code=404, detail={"errorMessage": "Message not found", "errorCode": 40401})
    return m


@router.put("/messages/{message_id}", response_model=MessageResponseTo, response_model_exclude_none=True)
def v2_update_message(
    message_id: int,
    dto: MessageRequestTo,
    user: TokenUser = Depends(get_current_user),
    msg: MessageService = Depends(_messages),
    ns: NewsService = Depends(_news),
) -> MessageResponseTo:
    existing = msg.get_message(message_id)
    if not existing:
        raise HTTPException(status_code=404, detail={"errorMessage": "Message not found", "errorCode": 40401})
    owner = _message_news_owner_id(ns, existing.newsId)
    require_customer_own_news_author(user, owner)
    owner2 = _message_news_owner_id(ns, dto.newsId)
    require_customer_own_news_author(user, owner2)
    try:
        updated = msg.update_message(message_id, dto)
    except ValueError as ex:
        raise HTTPException(status_code=400, detail={"errorMessage": str(ex), "errorCode": 40001}) from ex
    if not updated:
        raise HTTPException(status_code=404, detail={"errorMessage": "Message not found", "errorCode": 40401})
    return updated


@router.delete("/messages/{message_id}", status_code=status.HTTP_204_NO_CONTENT)
def v2_delete_message(
    message_id: int,
    user: TokenUser = Depends(get_current_user),
    msg: MessageService = Depends(_messages),
    ns: NewsService = Depends(_news),
) -> Response:
    existing = msg.get_message(message_id)
    if not existing:
        raise HTTPException(status_code=404, detail={"errorMessage": "Message not found", "errorCode": 40401})
    owner = _message_news_owner_id(ns, existing.newsId)
    require_customer_own_news_author(user, owner)
    if not msg.delete_message(message_id):
        raise HTTPException(status_code=404, detail={"errorMessage": "Message not found", "errorCode": 40401})
    return Response(status_code=status.HTTP_204_NO_CONTENT)


# Example protected resource from diagram
@router.get("/protected-resource")
def v2_protected_resource(user: TokenUser = Depends(get_current_user)) -> dict:
    return {"login": user.login, "role": user.role, "authorId": user.author_id}
