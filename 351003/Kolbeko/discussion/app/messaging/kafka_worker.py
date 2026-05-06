import asyncio
import contextlib
import json
import os
from typing import Any, Dict, Optional

from aiokafka import AIOKafkaConsumer, AIOKafkaProducer

from discussion.app.core.exceptions import AppException
from discussion.app.services.notice_service import NoticeService


class KafkaWorker:
    def __init__(self) -> None:
        self.bootstrap_servers = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
        self.in_topic = os.getenv("KAFKA_IN_TOPIC", "InTopic")
        self.out_topic = os.getenv("KAFKA_OUT_TOPIC", "OutTopic")
        self.group_id = os.getenv("KAFKA_DISCUSSION_GROUP", "discussion")

        self._producer: Optional[AIOKafkaProducer] = None
        self._consumer: Optional[AIOKafkaConsumer] = None
        self._task: Optional[asyncio.Task] = None
        self._service = NoticeService()

    async def start(self) -> None:
        if self._task:
            return
        self._producer = AIOKafkaProducer(
            bootstrap_servers=self.bootstrap_servers,
            value_serializer=lambda v: json.dumps(v).encode("utf-8"),
        )
        self._consumer = AIOKafkaConsumer(
            self.in_topic,
            bootstrap_servers=self.bootstrap_servers,
            group_id=self.group_id,
            value_deserializer=lambda v: json.loads(v.decode("utf-8")),
            enable_auto_commit=True,
        )
        await self._producer.start()
        await self._consumer.start()
        self._task = asyncio.create_task(self._run())

    async def stop(self) -> None:
        if self._task:
            self._task.cancel()
            with contextlib.suppress(Exception):
                await self._task
            self._task = None
        if self._consumer:
            await self._consumer.stop()
            self._consumer = None
        if self._producer:
            await self._producer.stop()
            self._producer = None

    async def _reply(self, correlation_id: str, ok: bool, data: Any = None, error: Optional[Dict[str, Any]] = None) -> None:
        assert self._producer is not None
        msg = {"correlation_id": correlation_id, "ok": ok}
        if ok:
            msg["data"] = data
        else:
            msg["error"] = error or {"status": 500, "message": "Unknown error", "errorCode": "50000"}
        await self._producer.send_and_wait(self.out_topic, value=msg)

    async def _run(self) -> None:
        assert self._consumer is not None
        async for msg in self._consumer:
            payload = msg.value
            correlation_id = payload.get("correlation_id")
            op = payload.get("operation")
            data = payload.get("data") or {}
            if not correlation_id or not op:
                continue

            try:
                if op == "CREATE":
                    created = await self._service.create(data)
                    await self._reply(
                        correlation_id,
                        ok=True,
                        data={
                            "id": created["id"],
                            "tweetId": created["tweet_id"],
                            "content": created["content"],
                            "state": created["state"],
                        },
                    )
                elif op == "GET_ALL":
                    page = int(data.get("page", 1))
                    res = await self._service.get_all(page)
                    await self._reply(
                        correlation_id,
                        ok=True,
                        data=[
                            {"id": n["id"], "tweetId": n["tweet_id"], "content": n["content"], "state": n["state"]}
                            for n in res
                        ],
                    )
                elif op == "GET_BY_ID":
                    nid = int(data["id"])
                    n = await self._service.get_by_id(nid)
                    await self._reply(
                        correlation_id,
                        ok=True,
                        data={"id": n["id"], "tweetId": n["tweet_id"], "content": n["content"], "state": n["state"]},
                    )
                elif op == "UPDATE":
                    nid = int(data["id"])
                    n = await self._service.update(nid, data)
                    await self._reply(
                        correlation_id,
                        ok=True,
                        data={"id": n["id"], "tweetId": n["tweet_id"], "content": n["content"], "state": n["state"]},
                    )
                elif op == "DELETE":
                    nid = int(data["id"])
                    await self._service.delete(nid)
                    await self._reply(correlation_id, ok=True, data=None)
                else:
                    await self._reply(correlation_id, ok=False, error={"status": 400, "message": "Unknown operation", "errorCode": "40001"})
            except AppException as e:
                await self._reply(correlation_id, ok=False, error={"status": e.status_code, "message": e.message, "errorCode": e.error_code})
            except Exception as e:
                await self._reply(correlation_id, ok=False, error={"status": 500, "message": str(e), "errorCode": "50001"})


_worker = KafkaWorker()


async def start_kafka_worker() -> None:
    await _worker.start()


async def stop_kafka_worker() -> None:
    await _worker.stop()

