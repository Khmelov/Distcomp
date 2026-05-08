import redis.asyncio as aioredis
from faststream.kafka.fastapi import KafkaRouter, Logger

from src.cache.redis_cache import RedisCache
from src.config import KafkaConfig, RedisConfig
from src.messaging.post_messages import PostReplyMessage
from src.messaging.reply_waiter import post_reply_waiter
from src.messaging.topics import OUT_TOPIC

posts_kafka_router = KafkaRouter(KafkaConfig().bootstrap_servers)


@posts_kafka_router.after_startup
async def _init_redis_cache(app) -> dict:
    cfg = RedisConfig()
    client = aioredis.from_url(cfg.url(), decode_responses=True)
    await client.ping()
    app.state.redis_client = client
    app.state.redis_cache = RedisCache(client)
    app.state.redis_ttl_seconds = cfg.default_ttl_seconds
    return {}


@posts_kafka_router.on_broker_shutdown
async def _close_redis_cache(app) -> None:
    client = getattr(app.state, "redis_client", None)
    if client is not None:
        await client.aclose()


@posts_kafka_router.subscriber(OUT_TOPIC, group_id="distcomp-publisher-out")
async def handle_post_reply(msg: PostReplyMessage, logger: Logger) -> None:
    post_reply_waiter.resolve(msg.correlation_id, msg)
    logger.debug("OutTopic reply correlation_id=%s status=%s", msg.correlation_id, msg.status_code)


def get_posts_kafka_broker():
    return posts_kafka_router.broker
