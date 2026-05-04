from __future__ import annotations

import json
import threading
import uuid
from typing import Any, Optional

from src.core.settings import settings


class KafkaUnavailableError(RuntimeError):
    pass


class NoteKafkaClient:
    def __init__(self) -> None:
        self._producer: Optional[Any] = None
        self._consumer: Optional[Any] = None
        self._pending: dict[str, dict[str, Any]] = {}
        self._lock = threading.Lock()
        self._started = False

    def _ensure_started(self) -> None:
        if self._started:
            return
        with self._lock:
            if self._started:
                return
            try:
                from kafka import KafkaConsumer, KafkaProducer

                self._producer = KafkaProducer(
                    bootstrap_servers=settings.KAFKA_BOOTSTRAP_SERVERS,
                    value_serializer=lambda v: json.dumps(v).encode("utf-8"),
                )
                self._consumer = KafkaConsumer(
                    settings.KAFKA_NOTES_REPLY_TOPIC,
                    bootstrap_servers=settings.KAFKA_BOOTSTRAP_SERVERS,
                    auto_offset_reset="latest",
                    group_id=settings.KAFKA_NOTES_PUBLISHER_GROUP_ID,
                    enable_auto_commit=True,
                    value_deserializer=lambda m: json.loads(m.decode("utf-8")),
                )
            except Exception as exc:
                self._producer = None
                self._consumer = None
                raise KafkaUnavailableError(str(exc)) from exc

            t = threading.Thread(target=self._consume_replies, daemon=True)
            t.start()
            self._started = True

    def _consume_replies(self) -> None:
        assert self._consumer is not None
        for msg in self._consumer:
            payload = msg.value or {}
            cid = payload.get("correlationId")
            if not cid:
                continue
            with self._lock:
                pending = self._pending.get(cid)
            if not pending:
                continue
            pending["payload"] = payload
            pending["event"].set()

    def request(self, action: str, data: Optional[dict[str, Any]] = None) -> dict[str, Any]:
        self._ensure_started()
        if self._producer is None:
            raise KafkaUnavailableError("Kafka producer is not initialized")

        correlation_id = str(uuid.uuid4())
        event = threading.Event()
        pending: dict[str, Any] = {"event": event, "payload": None}
        with self._lock:
            self._pending[correlation_id] = pending

        message = {
            "action": action,
            "correlationId": correlation_id,
            "data": data or {},
        }
        try:
            self._producer.send(settings.KAFKA_NOTES_REQUEST_TOPIC, value=message)
            self._producer.flush()
        except Exception as exc:
            with self._lock:
                self._pending.pop(correlation_id, None)
            raise KafkaUnavailableError(str(exc)) from exc

        ok = event.wait(timeout=settings.KAFKA_REQUEST_TIMEOUT_SECONDS)
        with self._lock:
            entry = self._pending.pop(correlation_id, None)
        if not ok or entry is None or entry.get("payload") is None:
            raise TimeoutError("Kafka request timeout")
        return entry["payload"]

    def close(self) -> None:
        with self._lock:
            pending = list(self._pending.values())
            self._pending.clear()
        for item in pending:
            item["event"].set()
        if self._consumer is not None:
            self._consumer.close()
            self._consumer = None
        if self._producer is not None:
            self._producer.close()
            self._producer = None
        self._started = False


_client: Optional[NoteKafkaClient] = None


def get_note_kafka_client() -> NoteKafkaClient:
    global _client
    if _client is None:
        _client = NoteKafkaClient()
    return _client


def close_note_kafka_client() -> None:
    global _client
    if _client is not None:
        _client.close()
        _client = None
