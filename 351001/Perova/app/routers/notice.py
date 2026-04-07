import json

from fastapi import APIRouter, Request, Response, status
from fastapi.exceptions import RequestValidationError
from pydantic import ValidationError

from app.dto.notice import NoticeRequestTo, NoticeResponseTo
from app.exceptions import EntityNotFoundException
from app.services import notice_service

router = APIRouter(prefix="/api/v1.0/notices", tags=["notices"])


def _parse_id(path_id: str) -> int | None:
    try:
        return int(path_id)
    except ValueError:
        return None


async def _parse_notice_request_body(request: Request, *, drop_string_id: bool = False) -> NoticeRequestTo:
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
        return NoticeRequestTo.model_validate(data)
    except ValidationError as e:
        raise RequestValidationError(e.errors(), body=data) from e


@router.get("", response_model=list[NoticeResponseTo])
def get_notices() -> list[NoticeResponseTo]:
    return notice_service.get_all()


@router.get("/{notice_id}", response_model=NoticeResponseTo)
def get_notice(notice_id: str) -> NoticeResponseTo:
    nid = _parse_id(notice_id)
    if nid is None:
        raise EntityNotFoundException("Notice", 0)
    return notice_service.get_by_id(nid)


@router.post("", response_model=NoticeResponseTo, status_code=status.HTTP_201_CREATED)
async def create_notice(request: Request) -> NoticeResponseTo:
    payload = await _parse_notice_request_body(request)
    return notice_service.create(payload)


@router.put("", response_model=NoticeResponseTo)
async def update_notice(request: Request) -> NoticeResponseTo:
    payload = await _parse_notice_request_body(request)
    return notice_service.update(payload)


@router.put("/{notice_id}", response_model=NoticeResponseTo)
async def update_notice_by_id(notice_id: str, request: Request) -> NoticeResponseTo:
    nid = _parse_id(notice_id)
    if nid is None:
        raise EntityNotFoundException("Notice", 0)
    payload = await _parse_notice_request_body(request)
    return notice_service.update(payload.model_copy(update={"id": nid}))


@router.delete("/{notice_id}")
def delete_notice(notice_id: str) -> Response:
    nid = _parse_id(notice_id)
    if nid is None:
        return Response(status_code=status.HTTP_204_NO_CONTENT)
    notice_service.delete(nid)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
