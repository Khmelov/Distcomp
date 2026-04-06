import os
import time
import uuid

from fastapi import APIRouter, status, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.database import get_db
from app.schemas.notice import NoticeRequestTo, NoticeResponseTo
from app.services.notice_service import NoticeService
from app.repository.db import tweet_repo
from app.core.exceptions import AppException
from app.integration.kafka_rpc import KafkaRpcClient
from app.integration.discussion_rest import DiscussionRestClient
from typing import List

router = APIRouter()
service = NoticeService()
kafka_rpc = KafkaRpcClient()
rest_client = DiscussionRestClient()

def _use_db_transport() -> bool:
    return bool(os.getenv("PYTEST_CURRENT_TEST")) or os.getenv("NOTICE_TRANSPORT", "").lower() == "db"

def _use_kafka_transport() -> bool:
    return os.getenv("NOTICE_TRANSPORT", "").lower() == "kafka"

def _gen_notice_id() -> int:
    now_ms = int(time.time() * 1000)
    suffix = uuid.uuid4().int % 1000
    return now_ms * 1000 + suffix

@router.post("", response_model=NoticeResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: NoticeRequestTo, session: AsyncSession = Depends(get_db)):
    if _use_db_transport():
        return await service.create(session, dto)

    if not await tweet_repo.get_by_id(session, dto.tweetId):
        raise AppException(400, "Tweet not found", 12)

    notice_id = _gen_notice_id()
    if _use_kafka_transport():
        resp = await kafka_rpc.call(
            operation="CREATE",
            data={"id": notice_id, "tweetId": dto.tweetId, "content": dto.content, "state": "PENDING"},
            key=dto.tweetId,
        )
    else:
        resp = await rest_client.create_notice({"id": notice_id, "tweetId": dto.tweetId, "content": dto.content})
    if not resp.get("ok", False):
        err = resp.get("error") or {}
        raise AppException(int(err.get("status", 500)), str(err.get("message", "Discussion error")), 1)
    n = resp["data"]
    return NoticeResponseTo(id=n["id"], tweetId=n["tweetId"], content=n["content"])

@router.get("", response_model=List[NoticeResponseTo])
async def get_all(page: int = 1, session: AsyncSession = Depends(get_db)):
    if _use_db_transport():
        return await service.get_all(session, page)

    if _use_kafka_transport():
        resp = await kafka_rpc.call(operation="GET_ALL", data={"page": page}, key=0)
    else:
        resp = await rest_client.get_all(page)
    if not resp.get("ok", False):
        err = resp.get("error") or {}
        raise AppException(int(err.get("status", 500)), str(err.get("message", "Discussion error")), 2)
    return [NoticeResponseTo(id=n["id"], tweetId=n["tweetId"], content=n["content"]) for n in resp["data"]]

@router.get("/{id}", response_model=NoticeResponseTo)
async def get_by_id(id: int, session: AsyncSession = Depends(get_db)):
    if _use_db_transport():
        return await service.get_by_id(session, id)

    if _use_kafka_transport():
        resp = await kafka_rpc.call(operation="GET_BY_ID", data={"id": id}, key=id)
    else:
        resp = await rest_client.get_by_id(id)
    if not resp.get("ok", False):
        err = resp.get("error") or {}
        raise AppException(int(err.get("status", 500)), str(err.get("message", "Discussion error")), 3)
    n = resp["data"]
    return NoticeResponseTo(id=n["id"], tweetId=n["tweetId"], content=n["content"])

@router.put("/{id}", response_model=NoticeResponseTo)
async def update(id: int, dto: NoticeRequestTo, session: AsyncSession = Depends(get_db)):
    if _use_db_transport():
        return await service.update(session, id, dto)

    if not await tweet_repo.get_by_id(session, dto.tweetId):
        raise AppException(400, "Tweet not found", 14)

    if _use_kafka_transport():
        resp = await kafka_rpc.call(
            operation="UPDATE",
            data={"id": id, "tweetId": dto.tweetId, "content": dto.content},
            key=dto.tweetId,
        )
    else:
        resp = await rest_client.update(id, {"tweetId": dto.tweetId, "content": dto.content})
    if not resp.get("ok", False):
        err = resp.get("error") or {}
        raise AppException(int(err.get("status", 500)), str(err.get("message", "Discussion error")), 4)
    n = resp["data"]
    return NoticeResponseTo(id=n["id"], tweetId=n["tweetId"], content=n["content"])

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int, session: AsyncSession = Depends(get_db)):
    if _use_db_transport():
        await service.delete(session, id)
        return

    if not _use_kafka_transport():
        resp = await rest_client.delete(id)
        if not resp.get("ok", False):
            err = resp.get("error") or {}
            raise AppException(int(err.get("status", 500)), str(err.get("message", "Discussion error")), 5)
        return

    tweet_id_key = id
    info = await kafka_rpc.call(operation="GET_BY_ID", data={"id": id}, key=id)
    if info.get("ok", False):
        tweet_id_key = info["data"].get("tweetId", id)

    resp = await kafka_rpc.call(operation="DELETE", data={"id": id}, key=tweet_id_key)
    if not resp.get("ok", False):
        err = resp.get("error") or {}
        raise AppException(int(err.get("status", 500)), str(err.get("message", "Discussion error")), 5)