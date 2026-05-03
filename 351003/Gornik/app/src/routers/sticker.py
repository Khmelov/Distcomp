from fastapi import APIRouter, HTTPException
from sqlalchemy import select

from dto import StickerRequestTo, StickerResponseTo
from models import Sticker
from routers.db_router import db_dependency
from redis_cache import cache_get, cache_set, cache_delete

router = APIRouter(
    prefix="/api/v1.0/stickers",
    tags=["stickers"],
)

CACHE_PREFIX = "sticker"


@router.get("", response_model=list[StickerResponseTo])
async def get_stickers(db: db_dependency):
    cached = await cache_get(f"{CACHE_PREFIX}:all")
    if cached is not None:
        return cached
    stickers = await db.execute(select(Sticker))
    stickers = stickers.scalars().all()
    data = [StickerResponseTo.model_validate(s, from_attributes=True).model_dump() for s in stickers]
    await cache_set(f"{CACHE_PREFIX}:all", data)
    return data


@router.get("/{id}", response_model=StickerResponseTo)
async def get_sticker(id: int, db: db_dependency):
    cached = await cache_get(f"{CACHE_PREFIX}:{id}")
    if cached is not None:
        return cached
    sticker = await db.execute(select(Sticker).where(Sticker.id == id))
    sticker = sticker.scalars().first()
    if sticker:
        data = StickerResponseTo.model_validate(sticker, from_attributes=True).model_dump()
        await cache_set(f"{CACHE_PREFIX}:{id}", data)
        return data
    return sticker


@router.post("", response_model=StickerResponseTo, status_code=201)
async def create_sticker(data: StickerRequestTo, db: db_dependency):
    sticker = Sticker(**data.dict())
    db.add(sticker)
    await db.commit()
    await db.refresh(sticker)

    resp = StickerResponseTo.model_validate(sticker, from_attributes=True).model_dump()
    await cache_set(f"{CACHE_PREFIX}:{sticker.id}", resp)
    await cache_delete(f"{CACHE_PREFIX}:all")
    return resp


@router.put("/{id}", response_model=StickerResponseTo)
async def update_sticker(id: int, data: StickerRequestTo, db: db_dependency):
    sticker = await db.execute(select(Sticker).where(Sticker.id == id))
    sticker = sticker.scalars().first()
    for key, value in data.dict().items():
        setattr(sticker, key, value)
    db.add(sticker)
    await db.commit()
    await db.refresh(sticker)

    resp = StickerResponseTo.model_validate(sticker, from_attributes=True).model_dump()
    await cache_set(f"{CACHE_PREFIX}:{id}", resp)
    await cache_delete(f"{CACHE_PREFIX}:all")
    return resp


@router.delete("/{id}", status_code=204)
async def delete_sticker(id: int, db: db_dependency):
    sticker = await db.execute(select(Sticker).where(Sticker.id == id))
    sticker = sticker.scalars().first()
    if not sticker:
        raise HTTPException(status_code=404, detail="Sticker not found")
    await db.delete(sticker)
    await db.commit()

    await cache_delete(f"{CACHE_PREFIX}:{id}")
    await cache_delete(f"{CACHE_PREFIX}:all")
