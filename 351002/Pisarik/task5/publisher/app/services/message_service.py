import os
from typing import Any, List, Optional

import httpx
from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.db.orm import NewsOrm
from app.dtos.message_request import MessageRequestTo
from app.dtos.message_response import MessageResponseTo
from app.kafka_rpc import get_kafka_rpc
from app.redis_cache import (
    cache_get_json,
    cache_set_json,
    invalidate_message_id,
    invalidate_message_writes,
    message_id_key,
    message_list_key,
)
from app.repositories.sqlalchemy_repository import PageParams, SqlAlchemyRepository

_HTTP_BASE = os.getenv("DISCUSSION_BASE_URL", "").strip().rstrip("/")
_USE_HTTP = bool(_HTTP_BASE)
_http_client: Optional[httpx.Client] = None


def _http() -> httpx.Client:
    global _http_client
    if _http_client is None:
        _http_client = httpx.Client(timeout=120.0, http2=False)
    return _http_client


class MessageService:
    """Messages in discussion (Cassandra): Kafka by default; Redis cache-aside on publisher."""

    def __init__(self, db: Session) -> None:
        self._db = db
        self._news_repo = SqlAlchemyRepository[NewsOrm](db, NewsOrm)
        self._kafka = None if _USE_HTTP else get_kafka_rpc()

    def _ensure_news_exists(self, news_id: int) -> None:
        if not self._news_repo.get_by_id(news_id):
            raise ValueError(f"News with id {news_id} not found")

    @staticmethod
    def _raise_from_response(r: httpx.Response) -> None:
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

    def _http_request(self, method: str, url: str, **kwargs: Any) -> httpx.Response:
        try:
            return _http().request(method, url, **kwargs)
        except httpx.RequestError as e:
            raise HTTPException(
                status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
                detail={"errorMessage": f"Discussion unavailable: {e}", "errorCode": 50301},
            ) from e

    @staticmethod
    def _raise_from_kafka(resp: dict) -> None:
        if resp.get("ok"):
            return
        err = resp.get("error") or {}
        code = int(err.get("statusCode", status.HTTP_500_INTERNAL_SERVER_ERROR))
        if not isinstance(err, dict) or "errorMessage" not in err:
            err = {"errorMessage": str(err), "errorCode": 50001}
        raise HTTPException(status_code=code, detail=err)

    @staticmethod
    def _unwrap_model(resp: dict) -> MessageResponseTo:
        MessageService._raise_from_kafka(resp)
        data = resp.get("data")
        if not isinstance(data, dict):
            raise HTTPException(
                status_code=status.HTTP_502_BAD_GATEWAY,
                detail={"errorMessage": "Invalid discussion payload", "errorCode": 50201},
            )
        return MessageResponseTo.model_validate(data)

    @staticmethod
    def _unwrap_list(resp: dict) -> List[MessageResponseTo]:
        MessageService._raise_from_kafka(resp)
        data = resp.get("data")
        if not isinstance(data, list):
            raise HTTPException(
                status_code=status.HTTP_502_BAD_GATEWAY,
                detail={"errorMessage": "Discussion returned non-list", "errorCode": 50202},
            )
        try:
            return [MessageResponseTo.model_validate(x) for x in data]
        except Exception as e:  # noqa: BLE001
            raise HTTPException(
                status_code=status.HTTP_502_BAD_GATEWAY,
                detail={"errorMessage": f"Bad message list from discussion: {e}", "errorCode": 50204},
            ) from e

    def _cache_put_message(self, m: MessageResponseTo) -> None:
        cache_set_json(message_id_key(m.id), m.model_dump(mode="json", exclude_none=True))

    def _cache_put_list(self, payload: dict[str, Any], items: List[MessageResponseTo]) -> None:
        data = [x.model_dump(mode="json", exclude_none=True) for x in items]
        cache_set_json(message_list_key(payload), data)

    def create_message(self, dto: MessageRequestTo) -> MessageResponseTo:
        self._ensure_news_exists(dto.newsId)
        if _USE_HTTP:
            r = self._http_request(
                "POST",
                f"{_HTTP_BASE}/api/v1.0/messages",
                json=dto.model_dump(exclude_none=True),
            )
            if r.status_code >= 400:
                self._raise_from_response(r)
            try:
                out = MessageResponseTo.model_validate(r.json())
            except Exception as e:  # noqa: BLE001
                raise HTTPException(
                    status_code=status.HTTP_502_BAD_GATEWAY,
                    detail={"errorMessage": f"Invalid discussion response: {e}", "errorCode": 50201},
                ) from e
        else:
            assert self._kafka is not None
            resp = self._kafka.call(
                "CREATE",
                dto.model_dump(exclude_none=True),
                partition_key=str(dto.newsId),
            )
            out = self._unwrap_model(resp)
        invalidate_message_writes(None)
        self._cache_put_message(out)
        return out

    def get_message(self, message_id: int) -> Optional[MessageResponseTo]:
        ck = message_id_key(message_id)
        cached = cache_get_json(ck)
        if isinstance(cached, dict):
            try:
                return MessageResponseTo.model_validate(cached)
            except Exception:
                pass
        if _USE_HTTP:
            r = self._http_request("GET", f"{_HTTP_BASE}/api/v1.0/messages/{message_id}")
            if r.status_code == 404:
                invalidate_message_id(message_id)
                return None
            if r.status_code >= 400:
                self._raise_from_response(r)
            try:
                out = MessageResponseTo.model_validate(r.json())
            except Exception as e:  # noqa: BLE001
                raise HTTPException(
                    status_code=status.HTTP_502_BAD_GATEWAY,
                    detail={"errorMessage": f"Bad message JSON from discussion: {e}", "errorCode": 50203},
                ) from e
        else:
            assert self._kafka is not None
            resp = self._kafka.call("GET", {"messageId": message_id}, partition_key=str(message_id))
            if not resp.get("ok"):
                err = resp.get("error") or {}
                if int(err.get("statusCode", 500)) == status.HTTP_404_NOT_FOUND:
                    invalidate_message_id(message_id)
                    return None
                self._raise_from_kafka(resp)
            out = self._unwrap_model(resp)
        self._cache_put_message(out)
        return out

    def get_all_messages(
        self,
        page: PageParams,
        news_id: Optional[int] = None,
        content: Optional[str] = None,
    ) -> List[MessageResponseTo]:
        payload: dict[str, Any] = {
            "page": page.page,
            "size": page.size,
            "sort": page.sort,
            "newsId": news_id,
            "content": content or "",
        }
        lk = message_list_key(payload)
        cached = cache_get_json(lk)
        if isinstance(cached, list):
            try:
                return [MessageResponseTo.model_validate(x) for x in cached]
            except Exception:
                pass
        if _USE_HTTP:
            params: dict = {"page": page.page, "size": page.size, "sort": page.sort}
            if news_id is not None:
                params["newsId"] = news_id
            if content:
                params["content"] = content
            r = self._http_request("GET", f"{_HTTP_BASE}/api/v1.0/messages", params=params)
            if r.status_code >= 400:
                self._raise_from_response(r)
            data = r.json()
            if not isinstance(data, list):
                raise HTTPException(
                    status_code=status.HTTP_502_BAD_GATEWAY,
                    detail={"errorMessage": "Discussion returned non-list", "errorCode": 50202},
                )
            try:
                out = [MessageResponseTo.model_validate(x) for x in data]
            except Exception as e:  # noqa: BLE001
                raise HTTPException(
                    status_code=status.HTTP_502_BAD_GATEWAY,
                    detail={"errorMessage": f"Bad message list from discussion: {e}", "errorCode": 50204},
                ) from e
        else:
            assert self._kafka is not None
            kafka_payload: dict[str, Any] = {
                "page": page.page,
                "size": page.size,
                "sort": page.sort,
            }
            if news_id is not None:
                kafka_payload["newsId"] = news_id
            if content:
                kafka_payload["content"] = content
            key = str(news_id if news_id is not None else 0)
            resp = self._kafka.call("LIST", kafka_payload, partition_key=key)
            out = self._unwrap_list(resp)
        self._cache_put_list(payload, out)
        return out

    def get_messages_by_news(self, news_id: int) -> List[MessageResponseTo]:
        self._ensure_news_exists(news_id)
        payload = {"newsId": news_id, "page": 0, "size": 10000, "sort": "id,asc", "content": ""}
        lk = message_list_key(payload)
        cached = cache_get_json(lk)
        if isinstance(cached, list):
            try:
                return [MessageResponseTo.model_validate(x) for x in cached]
            except Exception:
                pass
        if _USE_HTTP:
            r = self._http_request(
                "GET",
                f"{_HTTP_BASE}/api/v1.0/messages",
                params={"newsId": news_id, "page": 0, "size": 10000, "sort": "id,asc"},
            )
            if r.status_code >= 400:
                self._raise_from_response(r)
            data = r.json()
            if not isinstance(data, list):
                raise HTTPException(
                    status_code=status.HTTP_502_BAD_GATEWAY,
                    detail={"errorMessage": "Discussion returned non-list", "errorCode": 50202},
                )
            try:
                out = [MessageResponseTo.model_validate(x) for x in data]
            except Exception as e:  # noqa: BLE001
                raise HTTPException(
                    status_code=status.HTTP_502_BAD_GATEWAY,
                    detail={"errorMessage": f"Bad message list from discussion: {e}", "errorCode": 50204},
                ) from e
        else:
            assert self._kafka is not None
            resp = self._kafka.call(
                "LIST",
                {"newsId": news_id, "page": 0, "size": 10000, "sort": "id,asc"},
                partition_key=str(news_id),
            )
            out = self._unwrap_list(resp)
        self._cache_put_list(payload, out)
        return out

    def update_message(self, message_id: int, dto: MessageRequestTo) -> Optional[MessageResponseTo]:
        self._ensure_news_exists(dto.newsId)
        if _USE_HTTP:
            r = self._http_request(
                "PUT",
                f"{_HTTP_BASE}/api/v1.0/messages/{message_id}",
                json=dto.model_dump(exclude_none=True),
            )
            if r.status_code == 404:
                invalidate_message_id(message_id)
                return None
            if r.status_code >= 400:
                self._raise_from_response(r)
            try:
                out = MessageResponseTo.model_validate(r.json())
            except Exception as e:  # noqa: BLE001
                raise HTTPException(
                    status_code=status.HTTP_502_BAD_GATEWAY,
                    detail={"errorMessage": f"Bad message JSON from discussion: {e}", "errorCode": 50203},
                ) from e
        else:
            assert self._kafka is not None
            resp = self._kafka.call(
                "PUT",
                {"messageId": message_id, "dto": dto.model_dump(exclude_none=True)},
                partition_key=str(dto.newsId),
            )
            if not resp.get("ok"):
                err = resp.get("error") or {}
                if int(err.get("statusCode", 500)) == status.HTTP_404_NOT_FOUND:
                    invalidate_message_id(message_id)
                    return None
                self._raise_from_kafka(resp)
            out = self._unwrap_model(resp)
        invalidate_message_writes(None)
        self._cache_put_message(out)
        return out

    def delete_message(self, message_id: int) -> bool:
        if _USE_HTTP:
            r = self._http_request("DELETE", f"{_HTTP_BASE}/api/v1.0/messages/{message_id}")
            if r.status_code == 404:
                return False
            if r.status_code >= 400:
                self._raise_from_response(r)
            ok = True
        else:
            assert self._kafka is not None
            resp = self._kafka.call("DELETE", {"messageId": message_id}, partition_key=str(message_id))
            if not resp.get("ok"):
                err = resp.get("error") or {}
                if int(err.get("statusCode", 500)) == status.HTTP_404_NOT_FOUND:
                    return False
                self._raise_from_kafka(resp)
            ok = bool(resp.get("data"))
        if ok:
            invalidate_message_writes(message_id)
        return ok
