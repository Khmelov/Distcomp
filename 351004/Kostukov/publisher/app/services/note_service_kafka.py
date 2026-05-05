from __future__ import annotations

import asyncio
import json
import uuid
from contextlib import suppress
from typing import Any, Dict, List, Optional

from aiokafka import AIOKafkaConsumer, AIOKafkaProducer
from fastapi import HTTPException
from sqlalchemy.ext.asyncio import AsyncSession

from publisher.app.infrastructure.db.repo import ArticleRepo


class NoteServiceKafka:
    def __init__(
        self,
        bootstrap_servers: str = "localhost:9092",
        in_topic: str = "InTopic",
        out_topic: str = "OutTopic",
        group_id: str = "publisher-notes-group",
        request_timeout: float = 30.0,
    ):
        self.bootstrap_servers = bootstrap_servers
        self.in_topic = in_topic
        self.out_topic = out_topic
        self.group_id = group_id
        self.request_timeout = request_timeout

        self.article_repo = ArticleRepo()

        self.producer: Optional[AIOKafkaProducer] = None
        self.consumer: Optional[AIOKafkaConsumer] = None
        self._consumer_task: Optional[asyncio.Task] = None
        self._pending: Dict[str, asyncio.Future[Dict[str, Any]]] = {}
        self._started = False

    async def start(self) -> None:
        if self._started:
            return

        self.producer = AIOKafkaProducer(
            bootstrap_servers=self.bootstrap_servers
        )
        self.consumer = AIOKafkaConsumer(
            self.out_topic,
            bootstrap_servers=self.bootstrap_servers,
            group_id=self.group_id,
            auto_offset_reset="latest",
            enable_auto_commit=True,
        )

        await self.producer.start()
        await self.consumer.start()

        self._consumer_task = asyncio.create_task(self._consume_outbox())
        self._started = True

    async def stop(self) -> None:
        if not self._started:
            return

        if self._consumer_task is not None:
            self._consumer_task.cancel()
            with suppress(asyncio.CancelledError):
                await self._consumer_task
            self._consumer_task = None

        if self.consumer is not None:
            await self.consumer.stop()
            self.consumer = None

        if self.producer is not None:
            await self.producer.stop()
            self.producer = None

        self._started = False

    async def _consume_outbox(self) -> None:
        if self.consumer is None:
            return

        try:
            async for message in self.consumer:
                try:
                    payload = json.loads(message.value.decode("utf-8"))
                except Exception:
                    continue

                correlation_id = payload.get("correlationId")
                if not correlation_id:
                    continue

                future = self._pending.pop(correlation_id, None)
                if future is not None and not future.done():
                    future.set_result(payload)
        except asyncio.CancelledError:
            pass

    async def _rpc(self, payload: Dict[str, Any], key: str) -> Dict[str, Any]:
        if self.producer is None:
            raise RuntimeError("Kafka producer is not started")

        correlation_id = payload.setdefault("correlationId", str(uuid.uuid4()))
        loop = asyncio.get_running_loop()
        future: asyncio.Future[Dict[str, Any]] = loop.create_future()
        self._pending[correlation_id] = future

        try:
            await self.producer.send_and_wait(
                self.in_topic,
                json.dumps(payload, ensure_ascii=False, default=str).encode("utf-8"),
                key=key.encode("utf-8"),
            )
            response = await asyncio.wait_for(future, timeout=self.request_timeout)
            return response
        except asyncio.TimeoutError:
            raise HTTPException(status_code=504, detail="Kafka response timeout")
        finally:
            self._pending.pop(correlation_id, None)

    async def _ensure_article_exists(self, session: AsyncSession, article_id: int) -> None:
        article = await self.article_repo.get_by_id(session, article_id)
        if not article:
            raise HTTPException(status_code=404, detail="Article not found")

    def _unwrap_response(self, response: Dict[str, Any]) -> Dict[str, Any]:
        status_code = int(response.get("statusCode", 200))
        if status_code >= 400:
            raise HTTPException(
                status_code=status_code,
                detail=response.get("error", "Kafka request failed"),
            )
        return response

    async def create(self, session: AsyncSession, dto: Any) -> Dict[str, Any]:
        payload = dto.model_dump(exclude_none=True, by_alias=True)
        article_id = payload["articleId"]

        await self._ensure_article_exists(session, article_id)

        request = {
            "action": "CREATE",
            "articleId": article_id,
            "content": payload["content"],
            "state": "PENDING",
        }

        response = self._unwrap_response(await self._rpc(request, key=str(article_id)))
        await self.redis.delete_pattern("notes:article:*")
        return response["note"]

    async def get_all(self, skip: int = 0, limit: int = 10) -> List[Dict[str, Any]]:
        request = {
            "action": "LIST",
            "skip": skip,
            "limit": limit,
        }

        response = self._unwrap_response(await self._rpc(request, key="notes-list"))
        return response.get("notes", [])

    async def get_by_id(self, note_id: int) -> Optional[Dict[str, Any]]:
        cache_key = f"note:{note_id}"

        redis = self.redis
        cached = await redis.get(cache_key)
        if cached:
            return json.loads(cached)

        request = {
            "action": "GET",
            "noteId": note_id,
        }

        response = self._unwrap_response(await self._rpc(request, key=str(note_id)))
        note = response.get("note")
        if note:
            await redis.set(cache_key, json.dumps(note))
        return note

    async def update(self, session: AsyncSession, note_id: int, dto: Any) -> Optional[Dict[str, Any]]:
        payload = dto.model_dump(exclude_none=True, by_alias=True)
        article_id = payload["articleId"]

        await self._ensure_article_exists(session, article_id)

        request = {
            "action": "UPDATE",
            "noteId": note_id,
            "articleId": article_id,
            "content": payload["content"],
            "state": "PENDING",
        }

        response = self._unwrap_response(await self._rpc(request, key=str(note_id)))
        await self.redis.delete(f"note:{note_id}")
        await self.redis.delete_pattern("notes:article:*")
        return response.get("note")

    async def delete(self, note_id: int) -> bool:
        request = {
            "action": "DELETE",
            "noteId": note_id,
        }

        response = self._unwrap_response(await self._rpc(request, key=str(note_id)))
        await self.redis.delete(f"note:{note_id}")
        await self.redis.delete_pattern("notes:article:*")
        return bool(response.get("ok", True))

    async def list_by_article_id(self, article_id: int) -> List[Dict[str, Any]]:
        cache_key = f"notes:article:{article_id}"

        redis = self.redis
        cached = await redis.get(cache_key)
        if cached:
            return json.loads(cached)

        request = {
            "action": "LIST_BY_ARTICLE",
            "articleId": article_id,
        }

        response = self._unwrap_response(await self._rpc(request, key=str(article_id)))
        notes = response.get("notes", [])
        await redis.set(cache_key, json.dumps(notes))
        return notes


service = NoteServiceKafka()