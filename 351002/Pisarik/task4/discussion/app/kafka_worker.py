from __future__ import annotations

import json
import logging
import os
import threading
import time
from typing import Any, Dict, Optional

from kafka import KafkaConsumer, KafkaProducer

from app.dtos.message_request import MessageRequestTo
from app.services.message_service import MessageService, PageParams

log = logging.getLogger(__name__)

BOOTSTRAP = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
TOPIC_IN = os.getenv("KAFKA_IN_TOPIC", "InTopic")
TOPIC_OUT = os.getenv("KAFKA_OUT_TOPIC", "OutTopic")
GROUP = os.getenv("KAFKA_DISCUSSION_GROUP_ID", "discussion-in-gateway")


def _reply(producer: KafkaProducer, correlation_id: str, body: Dict[str, Any]) -> None:
    producer.send(
        TOPIC_OUT,
        key=correlation_id.encode("utf-8"),
        value=json.dumps(body, ensure_ascii=False).encode("utf-8"),
    )
    producer.flush()


def _handle_envelope(env: Dict[str, Any], producer: KafkaProducer) -> None:
    correlation_id = env.get("correlationId") or ""
    op = (env.get("op") or "").upper()
    payload: Dict[str, Any] = env.get("payload") or {}
    svc = MessageService()

    def ok(data: Any = None) -> None:
        _reply(producer, correlation_id, {"correlationId": correlation_id, "ok": True, "data": data})

    def err(status_code: int, error_message: str, error_code: int) -> None:
        _reply(
            producer,
            correlation_id,
            {
                "correlationId": correlation_id,
                "ok": False,
                "error": {"statusCode": status_code, "errorMessage": error_message, "errorCode": error_code},
            },
        )

    try:
        if op == "CREATE":
            dto = MessageRequestTo.model_validate(payload)
            m = svc.create_message(dto)
            ok(m.model_dump(exclude_none=True))
        elif op == "GET":
            mid = int(payload["messageId"])
            m = svc.get_message(mid)
            if not m:
                err(404, "Message not found", 40401)
            else:
                ok(m.model_dump(exclude_none=True))
        elif op == "LIST":
            pp = PageParams(
                page=int(payload.get("page", 0)),
                size=int(payload.get("size", 20)),
                sort=str(payload.get("sort", "id,asc")),
            )
            items = svc.get_all_messages(
                pp,
                news_id=payload.get("newsId"),
                content=payload.get("content"),
                country_override=payload.get("country"),
            )
            ok([x.model_dump(exclude_none=True) for x in items])
        elif op == "PUT":
            mid = int(payload["messageId"])
            dto = MessageRequestTo.model_validate(payload.get("dto") or payload)
            updated = svc.update_message(mid, dto)
            if not updated:
                err(404, "Message not found", 40401)
            else:
                ok(updated.model_dump(exclude_none=True))
        elif op == "DELETE":
            mid = int(payload["messageId"])
            deleted = svc.delete_message(mid)
            if not deleted:
                err(404, "Message not found", 40401)
            else:
                ok(True)
        else:
            err(400, f"Unknown op: {op}", 40001)
    except Exception as e:  # noqa: BLE001
        log.exception("Kafka handler error op=%s", op)
        err(500, f"{type(e).__name__}: {e}", 50002)


def _run_session(stop_event: threading.Event) -> None:
    consumer: Optional[KafkaConsumer] = None
    producer: Optional[KafkaProducer] = None
    try:
        consumer = KafkaConsumer(
            TOPIC_IN,
            bootstrap_servers=BOOTSTRAP,
            group_id=GROUP,
            enable_auto_commit=True,
            auto_offset_reset="earliest",
            value_deserializer=lambda b: json.loads(b.decode("utf-8")),
        )
        producer = KafkaProducer(
            bootstrap_servers=BOOTSTRAP,
            acks="all",
            retries=3,
        )
        log.info("Kafka consumer connected topic_in=%s topic_out=%s", TOPIC_IN, TOPIC_OUT)
        while not stop_event.is_set():
            batch = consumer.poll(timeout_ms=1000)
            if not batch:
                continue
            for _tp, records in batch.items():
                for rec in records:
                    if rec.value is None:
                        continue
                    if not isinstance(rec.value, dict):
                        continue
                    _handle_envelope(rec.value, producer)
    finally:
        if consumer is not None:
            try:
                consumer.close()
            except Exception:  # noqa: BLE001
                log.debug("consumer.close failed", exc_info=True)
        if producer is not None:
            try:
                producer.close()
            except Exception:  # noqa: BLE001
                log.debug("producer.close failed", exc_info=True)


def consume_loop(stop_event: threading.Event) -> None:
    backoff = 2.0
    while not stop_event.is_set():
        try:
            _run_session(stop_event)
        except Exception:  # noqa: BLE001
            log.exception("Kafka session ended; retrying in %.0fs", backoff)
        if stop_event.is_set():
            break
        time.sleep(backoff)
