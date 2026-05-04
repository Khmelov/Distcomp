import asyncio
import uuid
from time import time_ns
from typing import Annotated

from fastapi import APIRouter, Depends, HTTPException
from fastapi.responses import JSONResponse, Response
from starlette import status

from src.api.dependencies import SessionDep
from src.api.posts_kafka import get_posts_kafka_broker
from src.config import KafkaConfig
from src.database.repositories.tweet import TweetRepository
from src.dto.post import PostRequestTo, PostResponseTo
from src.exceptions import EntityNotFoundException
from src.messaging.partition_key import partition_key_for_command
from src.messaging.post_messages import PostCommandMessage, PostPayload, PostReplyMessage
from src.messaging.post_adapters import post_to_response
from src.messaging.reply_waiter import post_reply_waiter
from src.messaging.topics import IN_TOPIC
from src.models.post import Post
from src.models.post_state import PostState

router = APIRouter(prefix="/posts", tags=["posts"])

KafkaBrokerDep = Annotated[object, Depends(get_posts_kafka_broker)]


def _rpc_timeout() -> float:
    return KafkaConfig().post_rpc_timeout_seconds


async def _wait_reply(correlation_id: str) -> PostReplyMessage:
    fut = post_reply_waiter.register(correlation_id)
    try:
        return await asyncio.wait_for(fut, timeout=_rpc_timeout())
    except asyncio.TimeoutError:
        post_reply_waiter.cancel(correlation_id)
        raise HTTPException(
            status_code=status.HTTP_504_GATEWAY_TIMEOUT,
            detail="Discussion service did not respond in time",
        ) from None


def _reply_to_http(reply: PostReplyMessage) -> JSONResponse | Response:
    if reply.status_code == 204:
        return Response(status_code=status.HTTP_204_NO_CONTENT)
    if reply.status_code >= 400:
        raise HTTPException(
            status_code=reply.status_code,
            detail=reply.error or "Discussion error",
        )
    if reply.posts is not None:
        data = [
            PostResponseTo(
                id=p.id,
                tweet_id=p.tweet_id,
                content=p.content,
                state=p.state,
            ).model_dump(mode="json", by_alias=True)
            for p in reply.posts
        ]
        return JSONResponse(content=data, status_code=reply.status_code)
    if reply.post is not None:
        p = reply.post
        body = PostResponseTo(
            id=p.id,
            tweet_id=p.tweet_id,
            content=p.content,
            state=p.state,
        ).model_dump(mode="json", by_alias=True)
        return JSONResponse(content=body, status_code=reply.status_code)
    return JSONResponse(content={}, status_code=reply.status_code)


@router.get("")
async def get_posts(broker: KafkaBrokerDep):
    cid = str(uuid.uuid4())
    cmd = PostCommandMessage(correlation_id=cid, operation="GET_ALL")
    key = partition_key_for_command(cmd)
    await broker.publish(cmd, IN_TOPIC, key=key)
    reply = await _wait_reply(cid)
    return _reply_to_http(reply)


@router.get("/{post_id}")
async def get_post(post_id: int, broker: KafkaBrokerDep):
    cid = str(uuid.uuid4())
    cmd = PostCommandMessage(correlation_id=cid, operation="GET", post_id=post_id)
    await broker.publish(cmd, IN_TOPIC, key=partition_key_for_command(cmd))
    reply = await _wait_reply(cid)
    return _reply_to_http(reply)


@router.post("", status_code=status.HTTP_201_CREATED, response_model=PostResponseTo)
async def create_post(data: PostRequestTo, session: SessionDep, broker: KafkaBrokerDep):
    tweet = await TweetRepository(session).get_by_id(data.tweet_id)
    if tweet is None:
        raise EntityNotFoundException("Tweet", data.tweet_id)
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
async def update_post(
    post_id: int,
    data: PostRequestTo,
    session: SessionDep,
    broker: KafkaBrokerDep,
):
    tweet = await TweetRepository(session).get_by_id(data.tweet_id)
    if tweet is None:
        raise EntityNotFoundException("Tweet", data.tweet_id)
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
    return _reply_to_http(reply)


@router.delete("/{post_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_post(post_id: int, broker: KafkaBrokerDep):
    cid = str(uuid.uuid4())
    cmd = PostCommandMessage(correlation_id=cid, operation="DELETE", post_id=post_id)
    await broker.publish(cmd, IN_TOPIC, key=partition_key_for_command(cmd))
    reply = await _wait_reply(cid)
    return _reply_to_http(reply)
