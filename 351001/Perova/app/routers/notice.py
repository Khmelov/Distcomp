from fastapi import APIRouter, Response, status

from app.dto.notice import NoticeRequestTo, NoticeResponseTo
from app.services import notice_service

router = APIRouter(prefix="/api/v1.0/notices", tags=["notices"])


@router.get("", response_model=list[NoticeResponseTo])
def get_notices() -> list[NoticeResponseTo]:
    return notice_service.get_all()


@router.get("/{notice_id}", response_model=NoticeResponseTo)
def get_notice(notice_id: int) -> NoticeResponseTo:
    return notice_service.get_by_id(notice_id)


@router.post("", response_model=NoticeResponseTo, status_code=status.HTTP_201_CREATED)
def create_notice(request: NoticeRequestTo) -> NoticeResponseTo:
    return notice_service.create(request)


@router.put("", response_model=NoticeResponseTo)
def update_notice(request: NoticeRequestTo) -> NoticeResponseTo:
    return notice_service.update(request)


@router.delete("/{notice_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_notice(notice_id: int) -> Response:
    notice_service.delete(notice_id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
