import asyncio
import os

import httpx
from fastapi import APIRouter, HTTPException

from dto import CommentRequestTo, CommentResponseTo
from kafka_handler import kafka_handler
from redis_cache import cache_get, cache_set, cache_delete

router = APIRouter(
    prefix="/api/v1.0/comments",
    tags=["comments"],
)

DISCUSSION_BASE = os.getenv("DISCUSSION_URL", "http://localhost:24130")
DISCUSSION_URL = f"{DISCUSSION_BASE}/api/v1.0/comments"

CACHE_PREFIX = "comment"


def _discussion_url(path: str = "") -> str:
    return f"{DISCUSSION_URL}{path}"


@router.get("", response_model=list[CommentResponseTo])
async def get_comments():
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


@router.get("/{id}", response_model=CommentResponseTo)
async def get_comment(id: int):
    cached = await cache_get(f"{CACHE_PREFIX}:{id}")
    if cached is not None:
        return cached
    async with httpx.AsyncClient() as client:
        resp = await client.get(_discussion_url(f"/{id}"))
    if resp.status_code != 200:
        raise HTTPException(status_code=resp.status_code, detail=resp.text)
    data = resp.json()
    await cache_set(f"{CACHE_PREFIX}:{id}", data)
    return data


@router.post("", response_model=CommentResponseTo, status_code=201)
async def create_comment(data: CommentRequestTo):
    message = {
        "id": None,
        "tweetId": data.tweetId,
        "content": data.content,
        "country": data.country or "Unknown",
    }
    try:
        result = await kafka_handler.send_and_wait("POST", message)
    except asyncio.TimeoutError:
        raise HTTPException(status_code=408, detail="Kafka response timeout")
    except RuntimeError as e:
        raise HTTPException(status_code=503, detail=str(e))

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


@router.put("/{id}", response_model=CommentResponseTo)
async def update_comment(id: int, data: CommentRequestTo):
    message = {
        "id": id,
        "tweetId": data.tweetId,
        "content": data.content,
        "country": data.country or "Unknown",
    }
    try:
        result = await kafka_handler.send_and_wait("PUT", message)
    except asyncio.TimeoutError:
        raise HTTPException(status_code=408, detail="Kafka response timeout")
    except RuntimeError as e:
        raise HTTPException(status_code=503, detail=str(e))

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
    await cache_set(f"{CACHE_PREFIX}:{id}", resp_dict)
    await cache_delete(f"{CACHE_PREFIX}:all")
    return resp_dict


@router.delete("/{id}", status_code=204)
async def delete_comment(id: int):
    message = {
        "id": id,
        "tweetId": 0,
        "content": "",
        "country": "",
    }
    try:
        result = await kafka_handler.send_and_wait("DELETE", message)
    except asyncio.TimeoutError:
        raise HTTPException(status_code=408, detail="Kafka response timeout")
    except RuntimeError as e:
        raise HTTPException(status_code=503, detail=str(e))

    if result.get("error"):
        raise HTTPException(status_code=result.get("status", 404), detail=result["error"])

    await cache_delete(f"{CACHE_PREFIX}:{id}")
    await cache_delete(f"{CACHE_PREFIX}:all")
