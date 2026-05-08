import asyncio
import json
import uuid
import logging

from aiokafka import AIOKafkaProducer, AIOKafkaConsumer
from config.settings import get_settings

logger = logging.getLogger(__name__)

IN_TOPIC = "InTopic"
OUT_TOPIC = "OutTopic"


class KafkaHandler:
    def __init__(self):
        settings = get_settings()
        self._bootstrap = settings.kafka_bootstrap_servers
        self._producer: AIOKafkaProducer | None = None
        self._consumer: AIOKafkaConsumer | None = None
        self._pending: dict[str, asyncio.Future] = {}
        self._consumer_task: asyncio.Task | None = None
        self._ready = False
        self._startup_task: asyncio.Task | None = None

    async def start(self):
        self._startup_task = asyncio.create_task(self._connect())

    async def _connect(self):
        for attempt in range(60):
            try:
                self._producer = AIOKafkaProducer(
                    bootstrap_servers=self._bootstrap,
                    value_serializer=lambda v: json.dumps(v).encode("utf-8"),
                    key_serializer=lambda k: k.encode("utf-8") if k else None,
                )
                await self._producer.start()
                logger.info("Kafka producer connected")
                break
            except Exception:
                logger.warning("Kafka producer connect attempt %d failed, retrying...", attempt + 1)
                await asyncio.sleep(2)
        else:
            logger.error("Failed to connect Kafka producer after 60 attempts")
            return

        instance_id = uuid.uuid4().hex[:8]
        for attempt in range(60):
            try:
                self._consumer = AIOKafkaConsumer(
                    OUT_TOPIC,
                    bootstrap_servers=self._bootstrap,
                    value_deserializer=lambda v: json.loads(v.decode("utf-8")),
                    auto_offset_reset="latest",
                    group_id=f"publisher-{instance_id}",
                )
                await self._consumer.start()
                logger.info("Kafka consumer connected")
                break
            except Exception:
                logger.warning("Kafka consumer connect attempt %d failed, retrying...", attempt + 1)
                await asyncio.sleep(2)
        else:
            logger.error("Failed to connect Kafka consumer after 60 attempts")
            await self._producer.stop()
            self._producer = None
            return

        self._ready = True
        self._consumer_task = asyncio.create_task(self._consume_loop())
        logger.info("Kafka handler ready (publisher)")

    async def stop(self):
        if self._startup_task:
            self._startup_task.cancel()
            try:
                await self._startup_task
            except asyncio.CancelledError:
                pass
        if self._consumer_task:
            self._consumer_task.cancel()
            try:
                await self._consumer_task
            except asyncio.CancelledError:
                pass
        if self._consumer:
            await self._consumer.stop()
        if self._producer:
            await self._producer.stop()
        logger.info("Kafka handler stopped (publisher)")

    async def _consume_loop(self):
        try:
            async for msg in self._consumer:
                data = msg.value
                correlation_id = data.get("correlationId")
                if correlation_id and correlation_id in self._pending:
                    future = self._pending.pop(correlation_id)
                    if not future.done():
                        future.set_result(data)
        except asyncio.CancelledError:
            pass

    async def send_and_wait(self, method: str, data: dict, timeout: float = 5.0) -> dict:
        if not self._ready:
            for _ in range(50):
                if self._ready:
                    break
                await asyncio.sleep(0.1)
            if not self._ready:
                raise RuntimeError("Kafka not ready")

        correlation_id = str(uuid.uuid4())
        message = {
            "correlationId": correlation_id,
            "method": method,
            **data,
        }

        key = str(data.get("tweetId", ""))

        loop = asyncio.get_running_loop()
        future = loop.create_future()
        self._pending[correlation_id] = future

        await self._producer.send_and_wait(IN_TOPIC, value=message, key=key)

        try:
            result = await asyncio.wait_for(future, timeout=timeout)
            return result
        except asyncio.TimeoutError:
            self._pending.pop(correlation_id, None)
            raise


kafka_handler = KafkaHandler()
