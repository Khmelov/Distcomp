from fastapi import APIRouter, status, Query

from src.schemas.dto import IssueRequestTo, IssueResponseTo, EditorResponseTo, LabelResponseTo, PostResponseTo
from src.dependencies.services import IssueServiceDep


router = APIRouter(prefix="/issues")


@router.post(path="", response_model=IssueResponseTo, status_code=status.HTTP_201_CREATED)
async def create_issue(issue_in: IssueRequestTo, issue_service: IssueServiceDep):
    return await issue_service.create(issue_in)


@router.get(path="", response_model=list[IssueResponseTo], status_code=status.HTTP_200_OK)
async def get_issues(
    issue_service: IssueServiceDep,
    label_names: list[str] | None = Query(None),
    label_ids: list[int] | None = Query(None),
    editor_login: str | None = Query(None),
    title: str | None = Query(None),
    content: str | None = Query(None),
):
    if any([label_names, label_ids, editor_login, title, content]):
        return await issue_service.search_issues(label_names, label_ids, editor_login, title, content)
    return await issue_service.get_all()


@router.get(path="/{id}", response_model=IssueResponseTo, status_code=status.HTTP_200_OK)
async def get_issue(id: int, issue_service: IssueServiceDep):
    return await issue_service.get_by_id(id)


@router.put(path="/{id}", response_model=IssueResponseTo, status_code=status.HTTP_200_OK)
async def update_issue(id: int, issue_in: IssueRequestTo, issue_service: IssueServiceDep):
    return await issue_service.update(id, issue_in)


@router.delete(path="/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_issue(id: int, issue_service: IssueServiceDep):
    await issue_service.delete(id)


@router.get(path="/{id}/editor", response_model=EditorResponseTo, status_code=status.HTTP_200_OK)
async def get_editor_by_issue(id: int, issue_service: IssueServiceDep):
    return await issue_service.get_editor_by_issue(id)


@router.get(path="/{id}/labels", response_model=list[LabelResponseTo], status_code=status.HTTP_200_OK)
async def get_labels_by_issue(id: int, issue_service: IssueServiceDep):
    return await issue_service.get_labels_by_issue(id)


@router.get(path="/{id}/posts", response_model=list[PostResponseTo], status_code=status.HTTP_200_OK)
async def get_posts_by_issue(id: int, issue_service: IssueServiceDep):
    return await issue_service.get_posts_by_issue(id)
