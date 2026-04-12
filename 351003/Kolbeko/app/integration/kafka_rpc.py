import asyncio
import contextlib
import json
import os
import uuid
from dataclasses import dataclass
from typing import Any, Dict, Optional

from aiokafka import AIOKafkaConsumer, AIOKafkaProducer


@dataclass(frozen=True)
class KafkaRpcConfig:
    bootstrap_servers: str = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
    in_topic: str = os.getenv("KAFKA_IN_TOPIC", "InTopic")
    out_topic: str = os.getenv("KAFKA_OUT_TOPIC", "OutTopic")
    consumer_group: str = os.getenv("KAFKA_PUBLISHER_GROUP", "publisher")
    request_timeout_s: float = float(os.getenv("KAFKA_RPC_TIMEOUT_S", "1.0"))


class KafkaRpcClient:
    """
    Lightweight request/response over Kafka.
    - publisher produces requests to InTopic
    - discussion replies to OutTopic with same correlation_id
    """

    def __init__(self, cfg: Optional[KafkaRpcConfig] = None):
        self.cfg = cfg or KafkaRpcConfig()
        self._producer: Optional[AIOKafkaProducer] = None
        self._consumer: Optional[AIOKafkaConsumer] = None
        self._pending: Dict[str, asyncio.Future] = {}
        self._consume_task: Optional[asyncio.Task] = None
        self._started = False

    async def start(self) -> None:
        if self._started:
            return

        self._producer = AIOKafkaProducer(
            bootstrap_servers=self.cfg.bootstrap_servers,
            value_serializer=lambda v: json.dumps(v).encode("utf-8"),
            key_serializer=lambda v: str(v).encode("utf-8") if v is not None else None,
        )
        self._consumer = AIOKafkaConsumer(
            self.cfg.out_topic,
            bootstrap_servers=self.cfg.bootstrap_servers,
            group_id=self.cfg.consumer_group,
            value_deserializer=lambda v: json.loads(v.decode("utf-8")),
        )
        await self._producer.start()
        await self._consumer.start()
        self._consume_task = asyncio.create_task(self._consume())
        self._started = True

    async def stop(self) -> None:
        if not self._started:
            return
        if self._consume_task:
            self._consume_task.cancel()
            with contextlib.suppress(Exception):
                await self._consume_task
        if self._consumer:
            await self._consumer.stop()
        if self._producer:
            await self._producer.stop()
        self._started = False

    async def _consume(self) -> None:
        assert self._consumer is not None
        async for msg in self._consumer:
            payload = msg.value
            correlation_id = payload.get("correlation_id")
            if not correlation_id:
                continue
            fut = self._pending.pop(correlation_id, None)
            if fut and not fut.done():
                fut.set_result(payload)

    async def call(self, operation: str, data: Dict[str, Any], key: Any = None) -> Dict[str, Any]:
        await self.start()
        assert self._producer is not None

        correlation_id = uuid.uuid4().hex
        loop = asyncio.get_running_loop()
        fut: asyncio.Future = loop.create_future()
        self._pending[correlation_id] = fut

        msg = {
            "correlation_id": correlation_id,
            "operation": operation,
            "data": data,
        }

        await self._producer.send_and_wait(self.cfg.in_topic, value=msg, key=key)

        try:
            resp = await asyncio.wait_for(fut, timeout=self.cfg.request_timeout_s)
        except asyncio.TimeoutError:
            self._pending.pop(correlation_id, None)
            return {"ok": False, "error": {"status": 504, "message": "Discussion timeout", "errorCode": "50401"}}

        return resp

