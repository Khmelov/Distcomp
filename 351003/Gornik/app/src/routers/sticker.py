from fastapi import APIRouter, HTTPException
from sqlalchemy import select

from dto import StickerRequestTo, StickerResponseTo
from models import Sticker
from routers.db_router import db_dependency

router = APIRouter(
    prefix="/api/v1.0/stickers",
    tags=["stickers"],
)

@router.get("", response_model=list[StickerResponseTo])
async def get_stickers(db: db_dependency):
    stickers = await db.execute(select(Sticker))
    stickers = stickers.scalars().all()
    return stickers

@router.get("/{id}", response_model=StickerResponseTo)
async def get_sticker(id: int, db: db_dependency):
    sticker = await db.execute(select(Sticker).where(Sticker.id == id))
    sticker = sticker.scalars().first()
    return sticker

@router.post("", response_model=StickerResponseTo, status_code=201)
async def create_sticker(data: StickerRequestTo, db: db_dependency):
    sticker = Sticker(**data.dict())
    db.add(sticker)
    await db.commit()
    await db.refresh(sticker)
    return sticker

@router.put("/{id}", response_model=StickerResponseTo)
async def update_sticker(id: int, data: StickerRequestTo, db: db_dependency):
    sticker = await db.execute(select(Sticker).where(Sticker.id == id))
    sticker = sticker.scalars().first()
    for key, value in data.dict().items():
        setattr(sticker, key, value)
    db.add(sticker)
    await db.commit()
    await db.refresh(sticker)
    return sticker

@router.delete("/{id}", status_code=204)
async def delete_sticker(id: int, db: db_dependency):
    sticker = await db.execute(select(Sticker).where(Sticker.id == id))
    sticker = sticker.scalars().first()
    if not sticker:
        raise HTTPException(status_code=404, detail="Sticker not found")
    await db.delete(sticker)
    await db.commit()