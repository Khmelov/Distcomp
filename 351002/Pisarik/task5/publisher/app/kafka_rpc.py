from __future__ import annotations

import json
import logging
import os
import threading
import time
import uuid
from typing import Any, Dict, Optional

from kafka import KafkaConsumer, KafkaProducer

log = logging.getLogger(__name__)


class KafkaMessageRpc:
    """Synchronous RPC over Kafka: send to InTopic, wait for matching correlationId on OutTopic."""

    def __init__(self) -> None:
        self._bootstrap = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
        self._topic_in = os.getenv("KAFKA_IN_TOPIC", "InTopic")
        self._topic_out = os.getenv("KAFKA_OUT_TOPIC", "OutTopic")
        self._timeout = float(os.getenv("KAFKA_RPC_TIMEOUT_SEC", "15"))
        self._group = os.getenv("KAFKA_PUBLISHER_GROUP_ID", "publisher-rpc")
        self._producer: Optional[KafkaProducer] = None
        self._consumer: Optional[KafkaConsumer] = None
        self._lock = threading.Lock()

    def _producer_client(self) -> KafkaProducer:
        if self._producer is None:
            self._producer = KafkaProducer(
                bootstrap_servers=self._bootstrap,
                value_serializer=lambda v: json.dumps(v, ensure_ascii=False).encode("utf-8"),
                key_serializer=lambda k: k if k is None else (k if isinstance(k, bytes) else str(k).encode("utf-8")),
                acks="all",
                retries=3,
            )
        return self._producer

    def _consumer_client(self) -> KafkaConsumer:
        if self._consumer is None:
            self._consumer = KafkaConsumer(
                self._topic_out,
                bootstrap_servers=self._bootstrap,
                group_id=self._group,
                enable_auto_commit=True,
                auto_offset_reset="latest",
                value_deserializer=lambda b: json.loads(b.decode("utf-8")),
            )
        return self._consumer

    def call(self, op: str, payload: Dict[str, Any], partition_key: str) -> Dict[str, Any]:
        correlation_id = str(uuid.uuid4())
        envelope = {"correlationId": correlation_id, "op": op, "payload": payload}
        key_bytes = partition_key.encode("utf-8")

        with self._lock:
            prod = self._producer_client()
            cons = self._consumer_client()
            cons.subscribe([self._topic_out])
            end_deadline = time.monotonic() + 20.0
            while not cons.assignment() and time.monotonic() < end_deadline:
                cons.poll(timeout_ms=300)
            for tp in cons.assignment():
                cons.seek_to_end(tp)

            prod.send(self._topic_in, key=key_bytes, value=envelope)
            prod.flush()

            deadline = time.monotonic() + self._timeout
            while time.monotonic() < deadline:
                records = cons.poll(timeout_ms=300)
                if not records:
                    continue
                for _tp, batch in records.items():
                    for rec in batch:
                        val = rec.value
                        if isinstance(val, dict) and val.get("correlationId") == correlation_id:
                            return val

        log.warning("Kafka RPC timeout op=%s correlationId=%s", op, correlation_id)
        return {
            "correlationId": correlation_id,
            "ok": False,
            "error": {
                "statusCode": 504,
                "errorMessage": "Discussion (Kafka) response timeout",
                "errorCode": 50401,
            },
        }


_rpc: Optional[KafkaMessageRpc] = None
_rpc_lock = threading.Lock()


def get_kafka_rpc() -> KafkaMessageRpc:
    global _rpc
    with _rpc_lock:
        if _rpc is None:
            _rpc = KafkaMessageRpc()
        return _rpc
