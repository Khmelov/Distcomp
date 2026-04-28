from fastapi import APIRouter, Query, Response, status

from app.dto.issue import IssueRequestTo, IssueResponseTo
from app.dto.notice import NoticeResponseTo
from app.dto.sticker import StickerResponseTo
from app.dto.user import UserResponseTo
from app.services import issue_service, notice_service

router = APIRouter(prefix="/api/v1.0/issues", tags=["issues"])


@router.get("", response_model=list[IssueResponseTo])
def get_issues() -> list[IssueResponseTo]:
    return issue_service.get_all()


@router.post("", response_model=IssueResponseTo, status_code=status.HTTP_201_CREATED)
def create_issue(request: IssueRequestTo) -> IssueResponseTo:
    return issue_service.create(request)


@router.put("", response_model=IssueResponseTo)
def update_issue(request: IssueRequestTo) -> IssueResponseTo:
    return issue_service.update(request)


@router.delete("/{issue_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_issue(issue_id: int) -> Response:
    issue_service.delete(issue_id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)


@router.get("/{issue_id}/user", response_model=UserResponseTo)
def get_issue_user(issue_id: int) -> UserResponseTo:
    return issue_service.get_user_by_issue_id(issue_id)


@router.get("/{issue_id}/stickers", response_model=list[StickerResponseTo])
def get_issue_stickers(issue_id: int) -> list[StickerResponseTo]:
    return [StickerResponseTo.model_validate(s) for s in issue_service.get_stickers_by_issue_id(issue_id)]


@router.get("/{issue_id}/notices", response_model=list[NoticeResponseTo])
def get_issue_notices(issue_id: int) -> list[NoticeResponseTo]:
    return notice_service.get_by_issue_id(issue_id)


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
def get_issue(issue_id: int) -> IssueResponseTo:
    return issue_service.get_by_id(issue_id)
