from fastapi import APIRouter, Response, status

from app.dto.sticker import StickerRequestTo, StickerResponseTo
from app.services import sticker_service

router = APIRouter(prefix="/api/v1.0/stickers", tags=["stickers"])


@router.get("", response_model=list[StickerResponseTo])
def get_stickers() -> list[StickerResponseTo]:
    return sticker_service.get_all()


@router.get("/{sticker_id}", response_model=StickerResponseTo)
def get_sticker(sticker_id: int) -> StickerResponseTo:
    return sticker_service.get_by_id(sticker_id)


@router.post("", response_model=StickerResponseTo, status_code=status.HTTP_201_CREATED)
def create_sticker(request: StickerRequestTo) -> StickerResponseTo:
    return sticker_service.create(request)


@router.put("", response_model=StickerResponseTo)
def update_sticker(request: StickerRequestTo) -> StickerResponseTo:
    return sticker_service.update(request)


@router.delete("/{sticker_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_sticker(sticker_id: int) -> Response:
    sticker_service.delete(sticker_id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
