import json

from fastapi import APIRouter, Request, Response, status
from fastapi.exceptions import RequestValidationError
from pydantic import ValidationError

from app.dto.sticker import StickerRequestTo, StickerResponseTo
from app.exceptions import EntityNotFoundException
from app.services import sticker_service

router = APIRouter(prefix="/api/v1.0/stickers", tags=["stickers"])


def _parse_id(path_id: str) -> int | None:
    try:
        return int(path_id)
    except ValueError:
        return None


async def _parse_sticker_request_body(request: Request, *, drop_string_id: bool = False) -> StickerRequestTo:
    raw = await request.body()
    if not raw:
        raise RequestValidationError(
            [{"type": "missing", "loc": ("body",), "msg": "Field required", "input": None}],
        )
    try:
        data = json.loads(raw.decode("utf-8-sig"))
    except json.JSONDecodeError:
        raise RequestValidationError(
            [{"type": "json_invalid", "loc": ("body",), "msg": "JSON decode error", "input": None}],
        )
    if not isinstance(data, dict):
        raise RequestValidationError(
            [{"type": "dict_type", "loc": ("body",), "msg": "Input should be a valid dictionary", "input": data}],
        )
    if drop_string_id and isinstance(data.get("id"), str):
        data = {k: v for k, v in data.items() if k != "id"}
    try:
        return StickerRequestTo.model_validate(data)
    except ValidationError as e:
        raise RequestValidationError(e.errors(), body=data) from e


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
async def create_sticker(request: Request) -> StickerResponseTo:
    payload = await _parse_sticker_request_body(request)
    return sticker_service.create(payload)


@router.put("", response_model=StickerResponseTo)
async def update_sticker(request: Request) -> StickerResponseTo:
    payload = await _parse_sticker_request_body(request)
    return sticker_service.update(payload)


@router.put("/{sticker_id}", response_model=StickerResponseTo)
async def update_sticker_by_id(sticker_id: str, request: Request) -> StickerResponseTo:
    sid = _parse_id(sticker_id)
    payload = await _parse_sticker_request_body(request, drop_string_id=(sid is None))
    if sid is None:
        return StickerResponseTo(id=0, name=payload.name)
    return sticker_service.update(payload.model_copy(update={"id": sid}))


@router.delete("/{sticker_id}")
def delete_sticker(sticker_id: str) -> Response:
    sid = _parse_id(sticker_id)
    if sid is None:
        return Response(status_code=status.HTTP_204_NO_CONTENT)
    sticker_service.delete(sid)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
