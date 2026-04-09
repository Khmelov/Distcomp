from fastapi import APIRouter, Query, Response, status

from ..dto.author_dto import AuthorResponseTo
from ..dto.comment_dto import CommentResponseTo
from ..dto.issue_dto import IssueRequestTo, IssueResponseTo
from ..dto.tag_dto import TagResponseTo
from ..services.issue_service import IssueService

router = APIRouter(prefix="/api/v1.0/issues", tags=["issue"])
service = IssueService()


@router.post("", response_model=IssueResponseTo, status_code=status.HTTP_201_CREATED)
def create_issue(dto: IssueRequestTo):
    return service.create(dto)


@router.get("", response_model=list[IssueResponseTo], status_code=status.HTTP_200_OK)
def get_issues(
    tag_ids: list[int] | None = Query(default=None, alias="tagIds"),
    title: str | None = Query(default=None),
    content: str | None = Query(default=None)
):
    return service.search(tag_ids=tag_ids, title=title, content=content)


@router.get("/{issue_id}", response_model=IssueResponseTo, status_code=status.HTTP_200_OK)
def get_issue(issue_id: int):
    return service.get_by_id(issue_id)


@router.put("/{issue_id}", response_model=IssueResponseTo, status_code=status.HTTP_200_OK)
def update_issue(issue_id: int, dto: IssueRequestTo):
    return service.update(issue_id, dto)


@router.delete("/{issue_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_issue(issue_id: int):
    service.delete(issue_id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)


@router.get("/{issue_id}/author", response_model=AuthorResponseTo, status_code=status.HTTP_200_OK)
def get_author_by_issue(issue_id: int):
    return service.get_author_by_issue_id(issue_id)


@router.get("/{issue_id}/tags", response_model=list[TagResponseTo], status_code=status.HTTP_200_OK)
def get_tags_by_issue(issue_id: int):
    return service.get_tags_by_issue_id(issue_id)


@router.get("/{issue_id}/comments", response_model=list[CommentResponseTo], status_code=status.HTTP_200_OK)
def get_comments_by_issue(issue_id: int):
    return service.get_comments_by_issue_id(issue_id)