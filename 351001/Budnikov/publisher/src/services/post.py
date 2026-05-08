import os
import json
import uuid
import time
import asyncio
from aiokafka import AIOKafkaProducer, AIOKafkaConsumer
from pydantic import TypeAdapter

from src.schemas.dto import PostRequestTo, PostResponseTo
from src.core.exceptions import BaseAppException
from src.models import Issue
from src.core.cache import get_cache, set_cache, invalidate_cache_by_prefix


KAFKA_BOOTSTRAP_SERVERS = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")

kafka_producer = None
kafka_consumer = None
response_futures: dict[str, asyncio.Future] = {}


async def init_kafka():
    global kafka_producer, kafka_consumer
    kafka_producer = AIOKafkaProducer(bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS)
    kafka_consumer = AIOKafkaConsumer(
        "OutTopic",
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        group_id="publisher_group",
        auto_offset_reset="earliest"
    )

    # Повторные попытки подключения к Kafka
    retries = 10
    for i in range(retries):
        try:
            await kafka_producer.start()
            await kafka_consumer.start()
            print("Publisher: Successfully connected to Kafka!")
            break
        except Exception as e:
            print(f"Waiting for Kafka to be ready... ({i + 1}/{retries})")
            await asyncio.sleep(5)
    else:
        print("Publisher: Failed to connect to Kafka.")
        return

    asyncio.create_task(consume_kafka_responses())


async def stop_kafka():
    if kafka_producer:
        try:
            await kafka_producer.stop()
        except Exception:
            pass
    if kafka_consumer:
        try:
            await kafka_consumer.stop()
        except Exception:
            pass


async def consume_kafka_responses():
    try:
        async for msg in kafka_consumer:
            data = json.loads(msg.value.decode('utf-8'))
            req_id = data.get("request_id")
            if req_id and req_id in response_futures:
                future = response_futures[req_id]
                if not future.done():
                    future.set_result(data)
    except Exception as e:
        print(f"Publisher Consumer Error: {e}")


class PostService:
    cache_prefix = "post"  # Обязательно добавляем префикс для Redis!

    async def _send_and_wait(self, action: str, data: dict, issue_id: int = None, timeout: float = 1.0):
        req_id = str(uuid.uuid4())
        future = asyncio.get_event_loop().create_future()
        response_futures[req_id] = future

        payload = {
            "action": action,
            "request_id": req_id,
            "data": data
        }

        key = str(issue_id).encode('utf-8') if issue_id else None
        await kafka_producer.send_and_wait("InTopic", json.dumps(payload).encode('utf-8'), key=key)

        try:
            result = await asyncio.wait_for(future, timeout=timeout)
            if result.get("error"):
                err = result["error"]
                raise BaseAppException(err.get("status_code", 400), "40000", err.get("detail", "Error"))
            return result.get("result")
        except asyncio.TimeoutError:
            raise BaseAppException(504, "50400", "Timeout waiting for discussion service")
        finally:
            response_futures.pop(req_id, None)

    async def get_all(self, issue_id: int = None) -> list[PostResponseTo]:
        cache_key = f"{self.cache_prefix}:all:{issue_id}"
        cached = await get_cache(cache_key)
        if cached:
            adapter = TypeAdapter(list[PostResponseTo])
            return adapter.validate_json(cached)

        data = {"issue_id": issue_id} if issue_id else {}
        result = await self._send_and_wait("GET_ALL", data)
        posts = [PostResponseTo(**p) for p in result] if result else []

        adapter = TypeAdapter(list[PostResponseTo])
        await set_cache(cache_key, adapter.dump_json(posts), ex=3600)
        return posts

    async def get_by_id(self, obj_id: int) -> PostResponseTo:
        cache_key = f"{self.cache_prefix}:{obj_id}"
        cached = await get_cache(cache_key)
        if cached:
            return PostResponseTo.model_validate_json(cached)

        result = await self._send_and_wait("GET_ONE", {"id": obj_id})
        post = PostResponseTo(**result)

        await set_cache(cache_key, post.model_dump_json(), ex=3600)
        return post

    async def create(self, create_dto: PostRequestTo) -> PostResponseTo:
        issue_id = getattr(create_dto, "issue_id", getattr(create_dto, "issueId", None))

        issue_exists = await Issue.filter(id=issue_id).exists()
        if not issue_exists:
            raise BaseAppException(400, "40004", f"Issue with id {issue_id} not found")

        post_id = int(time.time() * 1000)
        payload = {"id": post_id, "issue_id": issue_id, "content": create_dto.content}

        msg = {"action": "CREATE", "request_id": str(uuid.uuid4()), "data": payload}
        key = str(issue_id).encode('utf-8')
        await kafka_producer.send_and_wait("InTopic", json.dumps(msg).encode('utf-8'), key=key)

        result = PostResponseTo(id=post_id, content=create_dto.content, issueId=issue_id, state="PENDING")
        await invalidate_cache_by_prefix(self.cache_prefix)
        return result

    async def update(self, obj_id: int, update_dto: PostRequestTo) -> PostResponseTo:
        issue_id = getattr(update_dto, "issue_id", getattr(update_dto, "issueId", None))
        result_data = await self._send_and_wait("UPDATE", {"id": obj_id, "content": update_dto.content},
                                                issue_id=issue_id)

        result = PostResponseTo(**result_data)
        await invalidate_cache_by_prefix(self.cache_prefix)
        return result

    async def delete(self, obj_id: int) -> None:
        await self._send_and_wait("DELETE", {"id": obj_id})
        await invalidate_cache_by_prefix(self.cache_prefix)