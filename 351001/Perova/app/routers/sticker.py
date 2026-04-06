from fastapi import APIRouter, Response, status

from app.dto.sticker import StickerRequestTo, StickerResponseTo
from app.exceptions import EntityNotFoundException
from app.services import sticker_service

router = APIRouter(prefix="/api/v1.0/stickers", tags=["stickers"])


def _parse_id(path_id: str) -> int | None:
    try:
        return int(path_id)
    except ValueError:
        return None


@router.get("", response_model=list[StickerResponseTo])
def get_stickers() -> list[StickerResponseTo]:
    return sticker_service.get_all()


@router.get("/{sticker_id}", response_model=StickerResponseTo)
def get_sticker(sticker_id: str) -> StickerResponseTo:
    sid = _parse_id(sticker_id)
    if sid is None:
        raise EntityNotFoundException("Sticker", 0)
    return sticker_service.get_by_id(sid)


@router.post("", response_model=StickerResponseTo, status_code=status.HTTP_201_CREATED)
def create_sticker(payload: StickerRequestTo) -> StickerResponseTo:
    return sticker_service.create(payload)


@router.put("", response_model=StickerResponseTo)
def update_sticker(payload: StickerRequestTo) -> StickerResponseTo:
    return sticker_service.update(payload)


@router.put("/{sticker_id}", response_model=StickerResponseTo)
def update_sticker_by_id(sticker_id: str, payload: StickerRequestTo) -> StickerResponseTo:
    sid = _parse_id(sticker_id)
    if sid is None:
        raise EntityNotFoundException("Sticker", 0)
    return sticker_service.update(payload.model_copy(update={"id": sid}))


@router.delete("/{sticker_id}")
def delete_sticker(sticker_id: str) -> Response:
    sid = _parse_id(sticker_id)
    if sid is None:
        return Response(status_code=status.HTTP_204_NO_CONTENT)
    sticker_service.delete(sid)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
