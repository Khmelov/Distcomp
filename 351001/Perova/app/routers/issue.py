import json

from fastapi import APIRouter, Query, Request, Response, status
from fastapi.exceptions import RequestValidationError
from pydantic import ValidationError

from app.dto.issue import IssueRequestTo, IssueResponseTo
from app.dto.notice import NoticeResponseTo
from app.dto.sticker import StickerResponseTo
from app.dto.user import UserResponseTo
from app.exceptions import EntityNotFoundException
from app.services import issue_service, notice_service

router = APIRouter(prefix="/api/v1.0/issues", tags=["issues"])


def _parse_id(path_id: str) -> int | None:
    try:
        return int(path_id)
    except ValueError:
        return None


async def _parse_issue_request_body(request: Request, *, drop_string_id: bool = False) -> IssueRequestTo:
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
        return IssueRequestTo.model_validate(data)
    except ValidationError as e:
        raise RequestValidationError(e.errors(), body=data) from e


@router.get("", response_model=list[IssueResponseTo])
def get_issues() -> list[IssueResponseTo]:
    return issue_service.get_all()


@router.post("", response_model=IssueResponseTo, status_code=status.HTTP_201_CREATED)
async def create_issue(request: Request) -> IssueResponseTo:
    payload = await _parse_issue_request_body(request)
    return issue_service.create(payload)


@router.put("", response_model=IssueResponseTo)
async def update_issue(request: Request) -> IssueResponseTo:
    payload = await _parse_issue_request_body(request)
    return issue_service.update(payload)


@router.put("/{issue_id}", response_model=IssueResponseTo)
async def update_issue_by_id(issue_id: str, request: Request) -> IssueResponseTo:
    oid = _parse_id(issue_id)
    if oid is None:
        raise EntityNotFoundException("Issue", 0)
    payload = await _parse_issue_request_body(request)
    return issue_service.update(payload.model_copy(update={"id": oid}))


@router.delete("/{issue_id}")
def delete_issue(issue_id: str) -> Response:
    oid = _parse_id(issue_id)
    if oid is None:
        return Response(status_code=status.HTTP_204_NO_CONTENT)
    issue_service.delete(oid)
    return Response(status_code=status.HTTP_204_NO_CONTENT)


@router.get("/{issue_id}/user", response_model=UserResponseTo)
def get_issue_user(issue_id: str) -> UserResponseTo:
    oid = _parse_id(issue_id)
    if oid is None:
        raise EntityNotFoundException("Issue", 0)
    return issue_service.get_user_by_issue_id(oid)


@router.get("/{issue_id}/stickers", response_model=list[StickerResponseTo])
def get_issue_stickers(issue_id: str) -> list[StickerResponseTo]:
    oid = _parse_id(issue_id)
    if oid is None:
        raise EntityNotFoundException("Issue", 0)
    return [StickerResponseTo.model_validate(s) for s in issue_service.get_stickers_by_issue_id(oid)]


@router.get("/{issue_id}/notices", response_model=list[NoticeResponseTo])
def get_issue_notices(issue_id: str) -> list[NoticeResponseTo]:
    oid = _parse_id(issue_id)
    if oid is None:
        raise EntityNotFoundException("Issue", 0)
    return notice_service.get_by_issue_id(oid)


@router.get("/search/by-params", response_model=list[IssueResponseTo])
def search_issues(
    stickerName: list[str] | None = Query(default=None),
    stickerId: list[int] | None = Query(default=None),
    userLogin: str | None = Query(default=None),
    title: str | None = Query(default=None),
    content: str | None = Query(default=None),
) -> list[IssueResponseTo]:
    return issue_service.search(stickerName, stickerId, userLogin, title, content)


@router.get("/{issue_id}", response_model=IssueResponseTo)
def get_issue(issue_id: str) -> IssueResponseTo:
    oid = _parse_id(issue_id)
    if oid is None:
        raise EntityNotFoundException("Issue", 0)
    return issue_service.get_by_id(oid)
