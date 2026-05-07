from faststream.kafka.fastapi import KafkaRouter, Logger

from src.config import KafkaConfig
from src.messaging.post_messages import PostReplyMessage
from src.messaging.reply_waiter import post_reply_waiter
from src.messaging.topics import OUT_TOPIC

posts_kafka_router = KafkaRouter(KafkaConfig().bootstrap_servers)


@posts_kafka_router.subscriber(OUT_TOPIC, group_id="distcomp-publisher-out")
async def handle_post_reply(msg: PostReplyMessage, logger: Logger) -> None:
    post_reply_waiter.resolve(msg.correlation_id, msg)
    logger.debug("OutTopic reply correlation_id=%s status=%s", msg.correlation_id, msg.status_code)


def get_posts_kafka_broker():
    return posts_kafka_router.broker
