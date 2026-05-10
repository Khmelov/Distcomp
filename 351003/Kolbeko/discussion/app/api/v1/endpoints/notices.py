from fastapi import APIRouter, status
from typing import List

from discussion.app.schemas.notice import NoticeRequestTo, NoticeResponseTo
from discussion.app.services.notice_service import NoticeService
from discussion.app.core.exceptions import AppException


router = APIRouter()
service = NoticeService()


@router.post("", response_model=NoticeResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: NoticeRequestTo):
    if dto.id is None:
        raise AppException(400, "id is required", 1)
    created = await service.create(dto.model_dump())
    return NoticeResponseTo(
        id=created["id"],
        tweetId=created["tweet_id"],
        content=created["content"],
        state=created["state"],
    )


@router.get("", response_model=List[NoticeResponseTo])
async def get_all(page: int = 1):
    res = await service.get_all(page)
    return [NoticeResponseTo(id=n["id"], tweetId=n["tweet_id"], content=n["content"], state=n["state"]) for n in res]


@router.get("/{id}", response_model=NoticeResponseTo)
async def get_by_id(id: int):
    n = await service.get_by_id(id)
    return NoticeResponseTo(id=n["id"], tweetId=n["tweet_id"], content=n["content"], state=n["state"])


@router.put("/{id}", response_model=NoticeResponseTo)
async def update(id: int, dto: NoticeRequestTo):
    n = await service.update(id, dto.model_dump())
    return NoticeResponseTo(id=n["id"], tweetId=n["tweet_id"], content=n["content"], state=n["state"])


@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int):
    await service.delete(id)

