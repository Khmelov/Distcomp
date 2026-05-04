from __future__ import annotations

import json
import threading
from typing import Any

from pydantic import ValidationError

from discussion_mod.core.database import get_session
from discussion_mod.core.errors import AppError
from discussion_mod.core.settings import settings
from discussion_mod.schemas.note import NoteRequestTo
from discussion_mod.services.note_service import NoteService


class NoteKafkaWorker:
    def __init__(self) -> None:
        self._stop = threading.Event()
        self._thread: threading.Thread | None = None

    def start(self) -> None:
        if not settings.KAFKA_ENABLED or self._thread is not None:
            return
        self._thread = threading.Thread(target=self._run, daemon=True)
        self._thread.start()

    def stop(self) -> None:
        self._stop.set()

    def _run(self) -> None:
        from kafka import KafkaConsumer, KafkaProducer

        producer = KafkaProducer(
            bootstrap_servers=settings.KAFKA_BOOTSTRAP_SERVERS,
            value_serializer=lambda v: json.dumps(v).encode("utf-8"),
        )
        consumer = KafkaConsumer(
            settings.KAFKA_NOTES_REQUEST_TOPIC,
            bootstrap_servers=settings.KAFKA_BOOTSTRAP_SERVERS,
            auto_offset_reset="earliest",
            group_id=settings.KAFKA_NOTES_DISCUSSION_GROUP_ID,
            enable_auto_commit=True,
            consumer_timeout_ms=1000,
            value_deserializer=lambda m: json.loads(m.decode("utf-8")),
        )
        service = NoteService(get_session())
        try:
            while not self._stop.is_set():
                for msg in consumer:
                    if self._stop.is_set():
                        break
                    payload = msg.value or {}
                    response = self._handle(service, payload)
                    producer.send(settings.KAFKA_NOTES_REPLY_TOPIC, value=response)
                    producer.flush()
        finally:
            consumer.close()
            producer.close()

    @staticmethod
    def _ok(correlation_id: str, data: Any, status_code: int = 200) -> dict[str, Any]:
        return {
            "correlationId": correlation_id,
            "statusCode": status_code,
            "data": data,
        }

    @staticmethod
    def _err(
        correlation_id: str,
        status_code: int,
        error_code: int,
        message: str,
    ) -> dict[str, Any]:
        return {
            "correlationId": correlation_id,
            "statusCode": status_code,
            "error": {"errorCode": error_code, "errorMessage": message},
        }

    def _handle(self, service: NoteService, payload: dict[str, Any]) -> dict[str, Any]:
        correlation_id = str(payload.get("correlationId", ""))
        action = str(payload.get("action", ""))
        data = payload.get("data") or {}
        try:
            if action == "create":
                dto = NoteRequestTo.model_validate(data)
                created = service.create(dto)
                return self._ok(correlation_id, created.model_dump(), 201)
            if action == "get_all":
                return self._ok(
                    correlation_id, [x.model_dump() for x in service.get_all()], 200
                )
            if action == "get":
                found = service.get_one(int(data["id"]))
                return self._ok(correlation_id, found.model_dump(), 200)
            if action == "update":
                dto = NoteRequestTo.model_validate(data)
                updated = service.update(int(data["id"]), dto)
                return self._ok(correlation_id, updated.model_dump(), 200)
            if action == "delete":
                service.delete(int(data["id"]))
                return self._ok(correlation_id, None, 204)
            if action == "by_news":
                return self._ok(
                    correlation_id,
                    [x.model_dump() for x in service.by_news_id(int(data["newsId"]))],
                    200,
                )
            if action == "delete_by_news":
                service.delete_all_for_news(int(data["newsId"]))
                return self._ok(correlation_id, None, 204)
            return self._err(correlation_id, 400, 40001, "Unknown Kafka action")
        except ValidationError as exc:
            return self._err(correlation_id, 400, 40001, str(exc))
        except AppError as exc:
            return self._err(correlation_id, exc.status_code, exc.error_code, exc.message)
        except Exception as exc:
            return self._err(correlation_id, 500, 50001, str(exc))


_worker = NoteKafkaWorker()


def start_kafka_worker() -> None:
    _worker.start()


def stop_kafka_worker() -> None:
    _worker.stop()
