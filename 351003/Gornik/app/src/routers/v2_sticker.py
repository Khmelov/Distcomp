from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import select

from auth import current_user_dependency, require_role
from dto import StickerRequestTo, StickerResponseTo
from models import Sticker, Writer
from routers.db_router import db_dependency
from redis_cache import cache_get, cache_set, cache_delete

router = APIRouter(
    prefix="/api/v2.0/stickers",
    tags=["v2-stickers"],
)

CACHE_PREFIX = "v2:sticker"


@router.get("", response_model=list[StickerResponseTo])
async def get_stickers(db: db_dependency, _user: current_user_dependency = None):
    cached = await cache_get(f"{CACHE_PREFIX}:all")
    if cached is not None:
        return cached
    stickers = await db.execute(select(Sticker))
    stickers = stickers.scalars().all()
    data = [StickerResponseTo.model_validate(s, from_attributes=True).model_dump() for s in stickers]
    await cache_set(f"{CACHE_PREFIX}:all", data)
    return data


@router.get("/{sticker_id}", response_model=StickerResponseTo)
async def get_sticker(sticker_id: int, db: db_dependency, _user: current_user_dependency = None):
    cached = await cache_get(f"{CACHE_PREFIX}:{sticker_id}")
    if cached is not None:
        return cached
    result = await db.execute(select(Sticker).where(Sticker.id == sticker_id))
    sticker = result.scalars().first()
    if not sticker:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Sticker not found", "errorCode": 40407},
        )
    data = StickerResponseTo.model_validate(sticker, from_attributes=True).model_dump()
    await cache_set(f"{CACHE_PREFIX}:{sticker_id}", data)
    return data


@router.post("", response_model=StickerResponseTo, status_code=201)
async def create_sticker(data: StickerRequestTo, db: db_dependency, _user: current_user_dependency = None):
    sticker = Sticker(**data.dict())
    db.add(sticker)
    await db.commit()
    await db.refresh(sticker)

    resp = StickerResponseTo.model_validate(sticker, from_attributes=True).model_dump()
    await cache_set(f"{CACHE_PREFIX}:{sticker.id}", resp)
    await cache_delete(f"{CACHE_PREFIX}:all")
    return resp


@router.put("/{sticker_id}", response_model=StickerResponseTo)
async def update_sticker(
    sticker_id: int,
    data: StickerRequestTo,
    db: db_dependency,
    _admin: Writer = Depends(require_role("ADMIN")),
):
    result = await db.execute(select(Sticker).where(Sticker.id == sticker_id))
    sticker = result.scalars().first()
    if not sticker:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Sticker not found", "errorCode": 40408},
        )
    for key, value in data.dict().items():
        setattr(sticker, key, value)
    db.add(sticker)
    await db.commit()
    await db.refresh(sticker)

    resp = StickerResponseTo.model_validate(sticker, from_attributes=True).model_dump()
    await cache_set(f"{CACHE_PREFIX}:{sticker_id}", resp)
    await cache_delete(f"{CACHE_PREFIX}:all")
    return resp


@router.delete("/{sticker_id}", status_code=204)
async def delete_sticker(
    sticker_id: int,
    db: db_dependency,
    _admin: Writer = Depends(require_role("ADMIN")),
):
    result = await db.execute(select(Sticker).where(Sticker.id == sticker_id))
    sticker = result.scalars().first()
    if not sticker:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Sticker not found", "errorCode": 40409},
        )
    await db.delete(sticker)
    await db.commit()

    await cache_delete(f"{CACHE_PREFIX}:{sticker_id}")
    await cache_delete(f"{CACHE_PREFIX}:all")
