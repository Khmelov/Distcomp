import os
from typing import List, Optional

import httpx
from sqlalchemy.orm import Session

from app.db.orm import NewsOrm
from app.dtos.message_request import MessageRequestTo
from app.dtos.message_response import MessageResponseTo
from app.repositories.sqlalchemy_repository import PageParams, SqlAlchemyRepository

_DISCUSSION_BASE = os.getenv("DISCUSSION_BASE_URL", "http://localhost:24130").rstrip("/")
_http = httpx.Client(timeout=120.0, http2=False)


class MessageService:
    """Delegates Message storage to discussion service (Cassandra)."""

    def __init__(self, db: Session) -> None:
        self._db = db
        self._news_repo = SqlAlchemyRepository[NewsOrm](db, NewsOrm)

    def _ensure_news_exists(self, news_id: int) -> None:
        if not self._news_repo.get_by_id(news_id):
            raise ValueError(f"News with id {news_id} not found")

    @staticmethod
    def _raise_from_response(r: httpx.Response) -> None:
        from fastapi import HTTPException

        try:
            body = r.json()
            if isinstance(body, dict):
                detail = body.get("detail", body)
                if isinstance(detail, dict) and "errorMessage" in detail and "errorCode" in detail:
                    raise HTTPException(status_code=r.status_code, detail=detail)
        except HTTPException:
            raise
        except Exception:
            pass
        raise HTTPException(
            status_code=r.status_code,
            detail={"errorMessage": r.text or "Discussion error", "errorCode": 50001},
        )

    @staticmethod
    def _request(method: str, url: str, **kwargs) -> httpx.Response:
        from fastapi import HTTPException, status

        try:
            r = _http.request(method, url, **kwargs)
            return r
        except httpx.RequestError as e:
            raise HTTPException(
                status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
                detail={"errorMessage": f"Discussion unavailable: {e}", "errorCode": 50301},
            ) from e

    def create_message(self, dto: MessageRequestTo) -> MessageResponseTo:
        self._ensure_news_exists(dto.newsId)
        r = self._request(
            "POST",
            f"{_DISCUSSION_BASE}/api/v1.0/messages",
            json=dto.model_dump(exclude_none=True),
        )
        if r.status_code >= 400:
            self._raise_from_response(r)
        try:
            return MessageResponseTo.model_validate(r.json())
        except Exception as e:
            from fastapi import HTTPException, status

            raise HTTPException(
                status_code=status.HTTP_502_BAD_GATEWAY,
                detail={"errorMessage": f"Invalid discussion response: {e}", "errorCode": 50201},
            ) from e

    def get_message(self, message_id: int) -> Optional[MessageResponseTo]:
        r = self._request("GET", f"{_DISCUSSION_BASE}/api/v1.0/messages/{message_id}")
        if r.status_code == 404:
            return None
        if r.status_code >= 400:
            self._raise_from_response(r)
        try:
            return MessageResponseTo.model_validate(r.json())
        except Exception as e:
            from fastapi import HTTPException, status

            raise HTTPException(
                status_code=status.HTTP_502_BAD_GATEWAY,
                detail={"errorMessage": f"Bad message JSON from discussion: {e}", "errorCode": 50203},
            ) from e

    def get_all_messages(
        self,
        page: PageParams,
        news_id: Optional[int] = None,
        content: Optional[str] = None,
    ) -> List[MessageResponseTo]:
        params: dict = {"page": page.page, "size": page.size, "sort": page.sort}
        if news_id is not None:
            params["newsId"] = news_id
        if content:
            params["content"] = content
        r = self._request("GET", f"{_DISCUSSION_BASE}/api/v1.0/messages", params=params)
        if r.status_code >= 400:
            self._raise_from_response(r)
        data = r.json()
        if not isinstance(data, list):
            from fastapi import HTTPException, status

            raise HTTPException(
                status_code=status.HTTP_502_BAD_GATEWAY,
                detail={"errorMessage": "Discussion returned non-list", "errorCode": 50202},
            )
        try:
            return [MessageResponseTo.model_validate(x) for x in data]
        except Exception as e:
            from fastapi import HTTPException, status

            raise HTTPException(
                status_code=status.HTTP_502_BAD_GATEWAY,
                detail={"errorMessage": f"Bad message list from discussion: {e}", "errorCode": 50204},
            ) from e

    def get_messages_by_news(self, news_id: int) -> List[MessageResponseTo]:
        self._ensure_news_exists(news_id)
        r = self._request(
            "GET",
            f"{_DISCUSSION_BASE}/api/v1.0/messages",
            params={"newsId": news_id, "page": 0, "size": 10000, "sort": "id,asc"},
        )
        if r.status_code >= 400:
            self._raise_from_response(r)
        data = r.json()
        if not isinstance(data, list):
            from fastapi import HTTPException, status

            raise HTTPException(
                status_code=status.HTTP_502_BAD_GATEWAY,
                detail={"errorMessage": "Discussion returned non-list", "errorCode": 50202},
            )
        try:
            return [MessageResponseTo.model_validate(x) for x in data]
        except Exception as e:
            from fastapi import HTTPException, status

            raise HTTPException(
                status_code=status.HTTP_502_BAD_GATEWAY,
                detail={"errorMessage": f"Bad message list from discussion: {e}", "errorCode": 50204},
            ) from e

    def update_message(self, message_id: int, dto: MessageRequestTo) -> Optional[MessageResponseTo]:
        self._ensure_news_exists(dto.newsId)
        r = self._request(
            "PUT",
            f"{_DISCUSSION_BASE}/api/v1.0/messages/{message_id}",
            json=dto.model_dump(exclude_none=True),
        )
        if r.status_code == 404:
            return None
        if r.status_code >= 400:
            self._raise_from_response(r)
        try:
            return MessageResponseTo.model_validate(r.json())
        except Exception as e:
            from fastapi import HTTPException, status

            raise HTTPException(
                status_code=status.HTTP_502_BAD_GATEWAY,
                detail={"errorMessage": f"Bad message JSON from discussion: {e}", "errorCode": 50203},
            ) from e

    def delete_message(self, message_id: int) -> bool:
        r = self._request("DELETE", f"{_DISCUSSION_BASE}/api/v1.0/messages/{message_id}")
        if r.status_code == 404:
            return False
        if r.status_code >= 400:
            self._raise_from_response(r)
        return True
