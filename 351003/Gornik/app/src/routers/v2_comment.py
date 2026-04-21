import asyncio
import os

import httpx
from fastapi import APIRouter, Depends, HTTPException

from auth import current_user_dependency, require_role
from dto import CommentRequestTo, CommentResponseTo
from kafka_handler import kafka_handler
from models import Writer
from redis_cache import cache_get, cache_set, cache_delete

router = APIRouter(
    prefix="/api/v2.0/comments",
    tags=["v2-comments"],
)

DISCUSSION_BASE = os.getenv("DISCUSSION_URL", "http://localhost:24130")
DISCUSSION_URL = f"{DISCUSSION_BASE}/api/v1.0/comments"

CACHE_PREFIX = "v2:comment"


def _discussion_url(path: str = "") -> str:
    return f"{DISCUSSION_URL}{path}"


@router.get("", response_model=list[CommentResponseTo])
async def get_comments(_user: current_user_dependency = None):
    cached = await cache_get(f"{CACHE_PREFIX}:all")
    if cached is not None:
        return cached
    async with httpx.AsyncClient() as client:
        resp = await client.get(_discussion_url())
    if resp.status_code != 200:
        raise HTTPException(status_code=resp.status_code, detail=resp.text)
    data = resp.json()
    await cache_set(f"{CACHE_PREFIX}:all", data)
    return data


@router.get("/{comment_id}", response_model=CommentResponseTo)
async def get_comment(comment_id: int, _user: current_user_dependency = None):
    cached = await cache_get(f"{CACHE_PREFIX}:{comment_id}")
    if cached is not None:
        return cached
    async with httpx.AsyncClient() as client:
        resp = await client.get(_discussion_url(f"/{comment_id}"))
    if resp.status_code != 200:
        raise HTTPException(status_code=resp.status_code, detail=resp.text)
    data = resp.json()
    await cache_set(f"{CACHE_PREFIX}:{comment_id}", data)
    return data


@router.post("", response_model=CommentResponseTo, status_code=201)
async def create_comment(data: CommentRequestTo, _user: current_user_dependency = None):
    message = {
        "id": None,
        "tweetId": data.tweetId,
        "content": data.content,
        "country": data.country or "Unknown",
    }
    try:
        result = await kafka_handler.send_and_wait("POST", message)
    except asyncio.TimeoutError:
        raise HTTPException(status_code=408, detail={"errorMessage": "Kafka response timeout", "errorCode": 40801})
    except RuntimeError as e:
        raise HTTPException(status_code=503, detail={"errorMessage": str(e), "errorCode": 50301})

    if result.get("error"):
        raise HTTPException(status_code=result.get("status", 500), detail=result["error"])

    resp = CommentResponseTo(
        id=result["id"],
        tweetId=result["tweetId"],
        content=result["content"],
        country=result.get("country", "Unknown"),
        state=result.get("state", "PENDING"),
    )
    resp_dict = resp.model_dump()
    await cache_set(f"{CACHE_PREFIX}:{resp.id}", resp_dict)
    await cache_delete(f"{CACHE_PREFIX}:all")
    return resp_dict


@router.put("/{comment_id}", response_model=CommentResponseTo)
async def update_comment(
    comment_id: int,
    data: CommentRequestTo,
    _admin: Writer = Depends(require_role("ADMIN")),
):
    message = {
        "id": comment_id,
        "tweetId": data.tweetId,
        "content": data.content,
        "country": data.country or "Unknown",
    }
    try:
        result = await kafka_handler.send_and_wait("PUT", message)
    except asyncio.TimeoutError:
        raise HTTPException(status_code=408, detail={"errorMessage": "Kafka response timeout", "errorCode": 40802})
    except RuntimeError as e:
        raise HTTPException(status_code=503, detail={"errorMessage": str(e), "errorCode": 50302})

    if result.get("error"):
        raise HTTPException(status_code=result.get("status", 404), detail=result["error"])

    resp = CommentResponseTo(
        id=result["id"],
        tweetId=result["tweetId"],
        content=result["content"],
        country=result.get("country", "Unknown"),
        state=result.get("state", "PENDING"),
    )
    resp_dict = resp.model_dump()
    await cache_set(f"{CACHE_PREFIX}:{comment_id}", resp_dict)
    await cache_delete(f"{CACHE_PREFIX}:all")
    return resp_dict


@router.delete("/{comment_id}", status_code=204)
async def delete_comment(
    comment_id: int,
    _admin: Writer = Depends(require_role("ADMIN")),
):
    message = {
        "id": comment_id,
        "tweetId": 0,
        "content": "",
        "country": "",
    }
    try:
        result = await kafka_handler.send_and_wait("DELETE", message)
    except asyncio.TimeoutError:
        raise HTTPException(status_code=408, detail={"errorMessage": "Kafka response timeout", "errorCode": 40803})
    except RuntimeError as e:
        raise HTTPException(status_code=503, detail={"errorMessage": str(e), "errorCode": 50303})

    if result.get("error"):
        raise HTTPException(status_code=result.get("status", 404), detail=result["error"])

    await cache_delete(f"{CACHE_PREFIX}:{comment_id}")
    await cache_delete(f"{CACHE_PREFIX}:all")
