from fastapi import APIRouter, status, Body
from app.schemas.sticker import StickerRequestTo, StickerResponseTo
from app.services.sticker_service import StickerService
from typing import List

router = APIRouter()
service = StickerService()

@router.post("", response_model=StickerResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: StickerRequestTo = Body(...)):
    return service.create(dto)

@router.get("", response_model=List[StickerResponseTo])
async def get_all():
    return service.get_all()

@router.get("/{id}", response_model=StickerResponseTo)
async def get_by_id(id: int):
    return service.get_by_id(id)

@router.put("/{id}", response_model=StickerResponseTo)
async def update(id: int, dto: StickerRequestTo = Body(...)):
    return service.update(id, dto)

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int):
    service.delete(id)
