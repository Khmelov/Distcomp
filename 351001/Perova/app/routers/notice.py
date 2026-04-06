from fastapi import APIRouter, Response, status

from app.dto.notice import NoticeRequestTo, NoticeResponseTo
from app.exceptions import EntityNotFoundException
from app.services import notice_service

router = APIRouter(prefix="/api/v1.0/notices", tags=["notices"])


def _parse_id(path_id: str) -> int | None:
    try:
        return int(path_id)
    except ValueError:
        return None


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
def create_notice(payload: NoticeRequestTo) -> NoticeResponseTo:
    return notice_service.create(payload)


@router.put("", response_model=NoticeResponseTo)
def update_notice(payload: NoticeRequestTo) -> NoticeResponseTo:
    return notice_service.update(payload)


@router.put("/{notice_id}", response_model=NoticeResponseTo)
def update_notice_by_id(notice_id: str, payload: NoticeRequestTo) -> NoticeResponseTo:
    nid = _parse_id(notice_id)
    if nid is None:
        raise EntityNotFoundException("Notice", 0)
    return notice_service.update(payload.model_copy(update={"id": nid}))


@router.delete("/{notice_id}")
def delete_notice(notice_id: str) -> Response:
    nid = _parse_id(notice_id)
    if nid is None:
        return Response(status_code=status.HTTP_204_NO_CONTENT)
    notice_service.delete(nid)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
