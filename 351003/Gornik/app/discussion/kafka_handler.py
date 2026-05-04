import asyncio
import json
import time
import os
import logging

from aiokafka import AIOKafkaProducer, AIOKafkaConsumer
from config.settings import Settings

logger = logging.getLogger(__name__)

settings = Settings()

IN_TOPIC = "InTopic"
OUT_TOPIC = "OutTopic"

STOP_WORDS = ["spam", "bad", "hate", "kill", "violence"]


def moderate(content: str) -> str:
    lower = content.lower()
    for word in STOP_WORDS:
        if word in lower:
            return "DECLINE"
    return "APPROVE"


def _generate_id() -> int:
    ts = int(time.time() * 1_000_000)
    pid = os.getpid() & 0xFFFF
    return ((ts & 0xFFFFFFFFFFFF) << 16) | pid


def _one_or_none(result_set):
    rows = list(result_set)
    return rows[0] if rows else None


def _handle_post(s, data: dict) -> dict:
    comment_id = _generate_id()
    country = data.get("country") or "Unknown"
    content = data["content"]
    tweet_id = data["tweetId"]
    state = moderate(content)

    s.execute(
        """
        BEGIN BATCH
          INSERT INTO tbl_comment (tweetId, id, content, country, state) VALUES (%s, %s, %s, %s, %s);
          INSERT INTO tbl_comment_by_id (id, tweetId, content, country, state) VALUES (%s, %s, %s, %s, %s);
        APPLY BATCH
        """,
        (tweet_id, comment_id, content, country, state,
         comment_id, tweet_id, content, country, state)
    )

    return {
        "id": comment_id,
        "tweetId": tweet_id,
        "content": content,
        "country": country,
        "state": state,
    }


def _handle_put(s, data: dict) -> dict:
    comment_id = data["id"]

    old = _one_or_none(s.execute(
        "SELECT id, tweetId, content, country, state FROM tbl_comment_by_id WHERE id = %s",
        (comment_id,)
    ))
    if not old:
        return {"error": "Comment not found", "status": 404}

    old_tweet_id = old.tweetid
    new_tweet_id = data["tweetId"]
    country = data.get("country") or old.country or "Unknown"
    content = data["content"]
    state = moderate(content)

    if old_tweet_id != new_tweet_id:
        s.execute(
            """
            BEGIN BATCH
              DELETE FROM tbl_comment WHERE tweetId = %s AND id = %s;
              INSERT INTO tbl_comment (tweetId, id, content, country, state) VALUES (%s, %s, %s, %s, %s);
              INSERT INTO tbl_comment_by_id (id, tweetId, content, country, state) VALUES (%s, %s, %s, %s, %s);
            APPLY BATCH
            """,
            (old_tweet_id, comment_id,
             new_tweet_id, comment_id, content, country, state,
             comment_id, new_tweet_id, content, country, state)
        )
    else:
        s.execute(
            """
            BEGIN BATCH
              INSERT INTO tbl_comment (tweetId, id, content, country, state) VALUES (%s, %s, %s, %s, %s);
              INSERT INTO tbl_comment_by_id (id, tweetId, content, country, state) VALUES (%s, %s, %s, %s, %s);
            APPLY BATCH
            """,
            (new_tweet_id, comment_id, content, country, state,
             comment_id, new_tweet_id, content, country, state)
        )

    return {
        "id": comment_id,
        "tweetId": new_tweet_id,
        "content": content,
        "country": country,
        "state": state,
    }


def _handle_delete(s, data: dict) -> dict:
    comment_id = data["id"]

    old = _one_or_none(s.execute(
        "SELECT id, tweetId FROM tbl_comment_by_id WHERE id = %s",
        (comment_id,)
    ))
    if not old:
        return {"error": "Comment not found", "status": 404}

    s.execute(
        """
        BEGIN BATCH
          DELETE FROM tbl_comment WHERE tweetId = %s AND id = %s;
          DELETE FROM tbl_comment_by_id WHERE id = %s;
        APPLY BATCH
        """,
        (old.tweetid, comment_id, comment_id)
    )

    return {"id": comment_id, "tweetId": old.tweetid, "content": "", "country": "", "state": ""}


def _process_message(s, data: dict) -> dict:
    method = data.get("method", "")
    if method == "POST":
        return _handle_post(s, data)
    elif method == "PUT":
        return _handle_put(s, data)
    elif method == "DELETE":
        return _handle_delete(s, data)
    else:
        return {"error": f"Unknown method: {method}", "status": 400}


class DiscussionKafkaHandler:
    def __init__(self):
        self._bootstrap = settings.kafka_bootstrap_servers
        self._producer: AIOKafkaProducer | None = None
        self._consumer: AIOKafkaConsumer | None = None
        self._consumer_task: asyncio.Task | None = None
        self._startup_task: asyncio.Task | None = None
        self._session = None

    async def start(self):
        from database import get_session
        self._session = get_session()
        self._startup_task = asyncio.create_task(self._connect())

    async def _connect(self):
        for attempt in range(60):
            try:
                self._producer = AIOKafkaProducer(
                    bootstrap_servers=self._bootstrap,
                    value_serializer=lambda v: json.dumps(v).encode("utf-8"),
                )
                await self._producer.start()
                logger.info("Kafka producer connected (discussion)")
                break
            except Exception:
                logger.warning("Kafka producer connect attempt %d failed, retrying...", attempt + 1)
                await asyncio.sleep(2)
        else:
            logger.error("Failed to connect Kafka producer after 60 attempts")
            return

        for attempt in range(60):
            try:
                self._consumer = AIOKafkaConsumer(
                    IN_TOPIC,
                    bootstrap_servers=self._bootstrap,
                    value_deserializer=lambda v: json.loads(v.decode("utf-8")),
                    auto_offset_reset="latest",
                    group_id="discussion-group",
                )
                await self._consumer.start()
                logger.info("Kafka consumer connected (discussion)")
                break
            except Exception:
                logger.warning("Kafka consumer connect attempt %d failed, retrying...", attempt + 1)
                await asyncio.sleep(2)
        else:
            logger.error("Failed to connect Kafka consumer after 60 attempts")
            await self._producer.stop()
            self._producer = None
            return

        self._consumer_task = asyncio.create_task(self._consume_loop())
        logger.info("Kafka handler ready (discussion)")

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
        logger.info("Kafka handler stopped (discussion)")

    async def _consume_loop(self):
        try:
            async for msg in self._consumer:
                data = msg.value
                correlation_id = data.get("correlationId")

                try:
                    result = await asyncio.to_thread(_process_message, self._session, data)
                except Exception as e:
                    logger.exception("Error processing Kafka message: %s", e)
                    result = {"error": str(e), "status": 500}

                response = {
                    "correlationId": correlation_id,
                    **result,
                }

                try:
                    await self._producer.send_and_wait(OUT_TOPIC, value=response)
                except Exception as e:
                    logger.exception("Error sending Kafka response: %s", e)
        except asyncio.CancelledError:
            pass


kafka_handler = DiscussionKafkaHandler()
