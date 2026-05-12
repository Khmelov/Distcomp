import uuid
from time import time_ns
from typing import Annotated

from fastapi import APIRouter, Depends, Request
from fastapi.responses import JSONResponse
from starlette import status

from src.api.dependencies import RedisCacheDep, SessionDep
from src.api.posts_kafka import get_posts_kafka_broker
from src.api.v1.posts import _post_body_dict, _reply_to_http, _wait_reply
from src.api.v2.deps import CurrentUserDep, ensure_tweet_write
from src.cache import keys as cache_keys
from src.config import RedisConfig
from src.database.repositories.tweet import TweetRepository
from src.dto.post import PostRequestTo, PostResponseTo
from src.exceptions import EntityNotFoundException
from src.messaging.partition_key import partition_key_for_command
from src.messaging.post_messages import PostCommandMessage, PostPayload
from src.messaging.post_adapters import post_to_response
from src.messaging.topics import IN_TOPIC
from src.models.post import Post
from src.models.post_state import PostState
from src.models.user_role import UserRole

router = APIRouter(prefix="/posts", tags=["posts-v2"])

KafkaBrokerDep = Annotated[object, Depends(get_posts_kafka_broker)]


def _redis_ttl(request: Request) -> int:
    return getattr(request.app.state, "redis_ttl_seconds", RedisConfig().default_ttl_seconds)


async def _get_post_reply_clean(post_id: int, broker: object):
    cid = str(uuid.uuid4())
    cmd = PostCommandMessage(correlation_id=cid, operation="GET", post_id=post_id)
    await broker.publish(cmd, IN_TOPIC, key=partition_key_for_command(cmd))
    return await _wait_reply(cid)


async def _assert_post_write(session: SessionDep, broker: object, user, post_id: int) -> None:
    reply = await _get_post_reply_clean(post_id, broker)
    if reply.status_code != 200 or reply.post is None:
        raise EntityNotFoundException("Post", post_id)
    tweet = await TweetRepository(session).get_by_id(reply.post.tweet_id)
    if tweet is None:
        raise EntityNotFoundException("Tweet", reply.post.tweet_id)
    ensure_tweet_write(user, tweet.editor_id)


@router.get("")
async def get_posts(request: Request, broker: KafkaBrokerDep, cache: RedisCacheDep, user: CurrentUserDep):
    ttl = _redis_ttl(request)
    cached = await cache.get_json(cache_keys.posts_all())
    if cached is not None:
        return JSONResponse(content=cached)
    cid = str(uuid.uuid4())
    cmd = PostCommandMessage(correlation_id=cid, operation="GET_ALL")
    key = partition_key_for_command(cmd)
    await broker.publish(cmd, IN_TOPIC, key=key)
    reply = await _wait_reply(cid)
    if reply.status_code == 200 and reply.posts is not None:
        data = [_post_body_dict(p) for p in reply.posts]
        await cache.set_json(cache_keys.posts_all(), data, ttl_seconds=ttl)
    return _reply_to_http(reply)


@router.get("/{post_id}")
async def get_post(
    post_id: int,
    request: Request,
    broker: KafkaBrokerDep,
    cache: RedisCacheDep,
    user: CurrentUserDep,
):
    ttl = _redis_ttl(request)
    ck = cache_keys.post(post_id)
    cached = await cache.get_json(ck)
    if cached is not None:
        return JSONResponse(content=cached)
    cid = str(uuid.uuid4())
    cmd = PostCommandMessage(correlation_id=cid, operation="GET", post_id=post_id)
    await broker.publish(cmd, IN_TOPIC, key=partition_key_for_command(cmd))
    reply = await _wait_reply(cid)
    if reply.status_code == 200 and reply.post is not None:
        await cache.set_json(ck, _post_body_dict(reply.post), ttl_seconds=ttl)
    return _reply_to_http(reply)


@router.post("", status_code=status.HTTP_201_CREATED, response_model=PostResponseTo)
async def create_post_v2(
    data: PostRequestTo,
    session: SessionDep,
    broker: KafkaBrokerDep,
    cache: RedisCacheDep,
    user: CurrentUserDep,
):
    tweet = await TweetRepository(session).get_by_id(data.tweet_id)
    if tweet is None:
        raise EntityNotFoundException("Tweet", data.tweet_id)
    ensure_tweet_write(user, tweet.editor_id)
    await cache.delete(cache_keys.posts_all())
    post_id = time_ns()
    payload = PostPayload(
        id=post_id,
        tweet_id=data.tweet_id,
        content=data.content,
        state=PostState.PENDING,
    )
    cmd = PostCommandMessage(operation="CREATE", post=payload)
    await broker.publish(cmd, IN_TOPIC, key=str(data.tweet_id).encode())
    pending = Post(tweet_id=data.tweet_id, content=data.content, state=PostState.PENDING)
    pending.id = post_id
    return post_to_response(pending)


@router.put("/{post_id}")
async def update_post_v2(
    post_id: int,
    data: PostRequestTo,
    request: Request,
    session: SessionDep,
    broker: KafkaBrokerDep,
    cache: RedisCacheDep,
    user: CurrentUserDep,
):
    tweet = await TweetRepository(session).get_by_id(data.tweet_id)
    if tweet is None:
        raise EntityNotFoundException("Tweet", data.tweet_id)
    if user.role != UserRole.ADMIN:
        await _assert_post_write(session, broker, user, post_id)
    ensure_tweet_write(user, tweet.editor_id)
    ttl = _redis_ttl(request)
    cid = str(uuid.uuid4())
    cmd = PostCommandMessage(
        correlation_id=cid,
        operation="UPDATE",
        post_id=post_id,
        tweet_id=data.tweet_id,
        content=data.content,
    )
    await broker.publish(cmd, IN_TOPIC, key=partition_key_for_command(cmd))
    reply = await _wait_reply(cid)
    http = _reply_to_http(reply)
    if reply.status_code == 200 and reply.post is not None:
        await cache.delete(cache_keys.posts_all())
        await cache.set_json(
            cache_keys.post(post_id),
            _post_body_dict(reply.post),
            ttl_seconds=ttl,
        )
    return http


@router.delete("/{post_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_post_v2(
    post_id: int,
    session: SessionDep,
    broker: KafkaBrokerDep,
    cache: RedisCacheDep,
    user: CurrentUserDep,
):
    if user.role != UserRole.ADMIN:
        await _assert_post_write(session, broker, user, post_id)
    cid = str(uuid.uuid4())
    cmd = PostCommandMessage(correlation_id=cid, operation="DELETE", post_id=post_id)
    await broker.publish(cmd, IN_TOPIC, key=partition_key_for_command(cmd))
    reply = await _wait_reply(cid)
    http = _reply_to_http(reply)
    if reply.status_code == 204:
        await cache.delete(cache_keys.posts_all(), cache_keys.post(post_id))
    return http
