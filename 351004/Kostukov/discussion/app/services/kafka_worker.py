from __future__ import annotations

import asyncio
import json
from contextlib import suppress
from typing import Any, Dict, Optional

from aiokafka import AIOKafkaConsumer, AIOKafkaProducer

from discussion.app.infrastructure.cassandra.repo import CassandraNoteRepository
from discussion.app.schemas import KafkaNoteRequest, NoteState
from discussion.app.services.moderation import NoteModerationService


class NoteKafkaWorker:
    def __init__(
        self,
        repo: CassandraNoteRepository,
        bootstrap_servers: str = "localhost:9092",
        in_topic: str = "InTopic",
        out_topic: str = "OutTopic",
        group_id: str = "discussion-notes-group",
    ) -> None:
        self.repo = repo
        self.bootstrap_servers = bootstrap_servers
        self.in_topic = in_topic
        self.out_topic = out_topic
        self.group_id = group_id

        self.producer = AIOKafkaProducer(bootstrap_servers=self.bootstrap_servers)
        self.consumer = AIOKafkaConsumer(
            self.in_topic,
            bootstrap_servers=self.bootstrap_servers,
            group_id=self.group_id,
            enable_auto_commit=True,
            auto_offset_reset="earliest",
        )

        self.moderation = NoteModerationService()
        self._task: Optional[asyncio.Task] = None
        self._started = False

    async def start(self) -> None:
        if self._started:
            return
        await self.producer.start()
        await self.consumer.start()
        self._task = asyncio.create_task(self._consume_loop())
        self._started = True

    async def stop(self) -> None:
        if not self._started:
            return

        if self._task is not None:
            self._task.cancel()
            with suppress(asyncio.CancelledError):
                await self._task

        await self.consumer.stop()
        await self.producer.stop()
        self._started = False

    async def _consume_loop(self) -> None:
        try:
            async for message in self.consumer:
                await self._handle_message(message.value)
        except asyncio.CancelledError:
            pass

    async def _handle_message(self, raw_value: bytes) -> None:
        try:
            payload = json.loads(raw_value.decode("utf-8"))
            request = KafkaNoteRequest.model_validate(payload)
            response = await self._process_request(request)
        except Exception as exc:
            correlation_id = None
            try:
                payload = json.loads(raw_value.decode("utf-8"))
                correlation_id = payload.get("correlationId")
            except Exception:
                pass

            response = {
                "correlationId": correlation_id,
                "statusCode": 400,
                "error": str(exc),
            }

        await self._send_response(response)

    async def _send_response(self, response: Dict[str, Any]) -> None:
        correlation_id = response.get("correlationId") or ""
        key = correlation_id.encode("utf-8") if correlation_id else None

        await self.producer.send_and_wait(
            self.out_topic,
            json.dumps(response, ensure_ascii=False, default=str).encode("utf-8"),
            key=key,
        )

    async def _process_request(self, request: KafkaNoteRequest) -> Dict[str, Any]:
        action = request.action.upper()

        if action == "CREATE":
            if request.article_id is None or request.content is None:
                return {
                    "correlationId": request.correlation_id,
                    "statusCode": 422,
                    "error": "articleId and content are required",
                }

            state = self.moderation.moderate(request.content)
            note = self.repo.create(
                {
                    "article_id": request.article_id,
                    "content": request.content,
                    "state": state.value,
                }
            )
            return {
                "correlationId": request.correlation_id,
                "statusCode": 201,
                "note": note,
            }

        if action == "UPDATE":
            if request.note_id is None:
                return {
                    "correlationId": request.correlation_id,
                    "statusCode": 422,
                    "error": "noteId is required",
                }

            update_data: Dict[str, Any] = {}
            if request.article_id is not None:
                update_data["article_id"] = request.article_id
            if request.content is not None:
                update_data["content"] = request.content
                update_data["state"] = self.moderation.moderate(request.content).value

            note = self.repo.update(request.note_id, update_data)
            if note is None:
                return {
                    "correlationId": request.correlation_id,
                    "statusCode": 404,
                    "error": "Note not found",
                }

            return {
                "correlationId": request.correlation_id,
                "statusCode": 200,
                "note": note,
            }

        if action == "GET":
            if request.note_id is None:
                return {
                    "correlationId": request.correlation_id,
                    "statusCode": 422,
                    "error": "noteId is required",
                }

            note = self.repo.get_by_id(request.note_id)
            if note is None:
                return {
                    "correlationId": request.correlation_id,
                    "statusCode": 404,
                    "error": "Note not found",
                }

            return {
                "correlationId": request.correlation_id,
                "statusCode": 200,
                "note": note,
            }

        if action == "LIST":
            notes = self.repo.get_all(skip=request.skip, limit=request.limit)
            return {
                "correlationId": request.correlation_id,
                "statusCode": 200,
                "notes": notes,
            }

        if action == "LIST_BY_ARTICLE":
            if request.article_id is None:
                return {
                    "correlationId": request.correlation_id,
                    "statusCode": 422,
                    "error": "articleId is required",
                }

            notes = self.repo.get_by_article_id(request.article_id)
            return {
                "correlationId": request.correlation_id,
                "statusCode": 200,
                "notes": notes,
            }

        if action == "DELETE":
            if request.note_id is None:
                return {
                    "correlationId": request.correlation_id,
                    "statusCode": 422,
                    "error": "noteId is required",
                }

            deleted = self.repo.delete(request.note_id)
            if not deleted:
                return {
                    "correlationId": request.correlation_id,
                    "statusCode": 404,
                    "error": "Note not found",
                }

            return {
                "correlationId": request.correlation_id,
                "statusCode": 200,
                "ok": True,
            }

        return {
            "correlationId": request.correlation_id,
            "statusCode": 400,
            "error": f"Unsupported action: {request.action}",
        }