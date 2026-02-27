from fastapi import APIRouter, status, Body, Depends, HTTPException
from sqlalchemy.orm import Session
from database import get_db
from app.schemas.sticker import StickerRequestTo, StickerResponseTo
from app.services.sticker_service import StickerService
from typing import List

router = APIRouter()
service = StickerService()

@router.post("", response_model=StickerResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: StickerRequestTo = Body(...), db: Session = Depends(get_db)):
    return service.create(db, dto)

@router.get("", response_model=List[StickerResponseTo])
async def get_all(db: Session = Depends(get_db)):
    return service.get_all(db)

@router.get("/{id}", response_model=StickerResponseTo)
async def get_by_id(id: int, db: Session = Depends(get_db)):
    res = service.get_by_id(db, id)
    if not res: raise HTTPException(404, "Sticker not found")
    return res

@router.put("/{id}", response_model=StickerResponseTo)
async def update(id: int, dto: StickerRequestTo = Body(...), db: Session = Depends(get_db)):
    res = service.update(db, id, dto)
    if not res: raise HTTPException(404, "Sticker not found")
    return res

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int, db: Session = Depends(get_db)):
    if not service.delete(db, id):
        raise HTTPException(404, "Sticker not found")