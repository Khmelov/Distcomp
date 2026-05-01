from contextlib import asynccontextmanager
from typing import Any, AsyncGenerator

from cassandra.cluster import Cluster
from faststream.kafka.fastapi import KafkaRouter, Logger

from src.config import CassandraConfig, KafkaConfig
from src.discussion.cassandra_holder import (
    clear_cassandra_session,
    get_cassandra_session,
    set_cassandra_session,
)
from src.discussion.moderation import moderate_content
from src.discussion.repositories.post import CassandraPostRepository
from src.discussion.schema import ensure_keyspace, ensure_tables
from src.messaging.post_messages import PostCommandMessage, PostReplyMessage
from src.messaging.post_adapters import post_to_payload
from src.messaging.topics import IN_TOPIC, OUT_TOPIC
from src.models.post import Post


@asynccontextmanager
async def _cassandra_lifespan(app: Any) -> AsyncGenerator[None, None]:
    cfg = CassandraConfig()
    cluster = Cluster([cfg.host], port=cfg.port)
    session = cluster.connect()
    ensure_keyspace(session, cfg.keyspace)
    ensure_tables(session)
    set_cassandra_session(session)
    app.state.cassandra_session = session
    app.state.cassandra_cluster = cluster
    try:
        yield
    finally:
        clear_cassandra_session()
        cluster.shutdown()


discussion_kafka_router = KafkaRouter(
    KafkaConfig().bootstrap_servers,
    lifespan=_cassandra_lifespan,
)


def _reply(
    cmd: PostCommandMessage,
    *,
    status_code: int = 200,
    post: Post | None = None,
    posts: list[Post] | None = None,
    error: str | None = None,
) -> PostReplyMessage:
    assert cmd.correlation_id is not None
    return PostReplyMessage(
        correlation_id=cmd.correlation_id,
        status_code=status_code,
        post=post_to_payload(post) if post is not None else None,
        posts=[post_to_payload(p) for p in posts] if posts is not None else None,
        error=error,
    )


@discussion_kafka_router.subscriber(IN_TOPIC, group_id="distcomp-discussion-in")
async def handle_post_command(msg: PostCommandMessage, logger: Logger) -> None:
    repo = CassandraPostRepository(get_cassandra_session())
    broker = discussion_kafka_router.broker

    if msg.operation == "CREATE":
        if msg.post is None:
            return
        final_state = moderate_content(msg.post.content)
        entity = Post(
            tweet_id=msg.post.tweet_id,
            content=msg.post.content,
            state=final_state,
        )
        entity.id = msg.post.id
        await repo.create(entity)
        logger.info("Post CREATE id=%s state=%s", entity.id, final_state)
        return

    if msg.correlation_id is None:
        logger.warning("RPC command without correlation_id: %s", msg.operation)
        return

    if msg.operation == "GET_ALL":
        posts = await repo.get_all()
        await broker.publish(
            _reply(msg, posts=posts),
            OUT_TOPIC,
            key=msg.correlation_id.encode(),
        )
        return

    if msg.operation == "GET":
        if msg.post_id is None:
            await broker.publish(
                _reply(msg, status_code=400, error="post_id required"),
                OUT_TOPIC,
                key=msg.correlation_id.encode(),
            )
            return
        post = await repo.get_by_id(msg.post_id)
        if post is None:
            await broker.publish(
                _reply(msg, status_code=404, error=f"Post {msg.post_id} not found"),
                OUT_TOPIC,
                key=msg.correlation_id.encode(),
            )
            return
        await broker.publish(
            _reply(msg, post=post),
            OUT_TOPIC,
            key=msg.correlation_id.encode(),
        )
        return

    if msg.operation == "UPDATE":
        if msg.post_id is None or msg.tweet_id is None or msg.content is None:
            await broker.publish(
                _reply(msg, status_code=400, error="post_id, tweetId, content required"),
                OUT_TOPIC,
                key=msg.correlation_id.encode(),
            )
            return
        new_state = moderate_content(msg.content)
        entity = Post(tweet_id=msg.tweet_id, content=msg.content, state=new_state)
        entity.id = msg.post_id
        updated = await repo.update(entity)
        if updated is None:
            await broker.publish(
                _reply(msg, status_code=404, error=f"Post {msg.post_id} not found"),
                OUT_TOPIC,
                key=msg.correlation_id.encode(),
            )
            return
        await broker.publish(
            _reply(msg, post=updated),
            OUT_TOPIC,
            key=msg.correlation_id.encode(),
        )
        return

    if msg.operation == "DELETE":
        if msg.post_id is None:
            await broker.publish(
                _reply(msg, status_code=400, error="post_id required"),
                OUT_TOPIC,
                key=msg.correlation_id.encode(),
            )
            return
        ok = await repo.delete(msg.post_id)
        if not ok:
            await broker.publish(
                _reply(msg, status_code=404, error=f"Post {msg.post_id} not found"),
                OUT_TOPIC,
                key=msg.correlation_id.encode(),
            )
            return
        await broker.publish(
            _reply(msg, status_code=204),
            OUT_TOPIC,
            key=msg.correlation_id.encode(),
        )
        return
