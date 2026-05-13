from fastapi import APIRouter, status, Query
from src.schemas.dto import IssueRequestTo, IssueResponseTo
from src.dependencies.services import IssueServiceDep
from src.dependencies.auth import CurrentUserDep, verify_permissions
from src.models import Issue
from src.core.exceptions import BaseAppException

router = APIRouter(prefix="/issues")


@router.post("", response_model=IssueResponseTo, status_code=status.HTTP_201_CREATED)
async def create_issue(issue_in: IssueRequestTo, issue_service: IssueServiceDep, current_user: CurrentUserDep):
    return await issue_service.create(issue_in)


@router.get("", response_model=list[IssueResponseTo], status_code=status.HTTP_200_OK)
async def get_issues(
        issue_service: IssueServiceDep,
        current_user: CurrentUserDep,
        label_names: list[str] | None = Query(None)
):
    if label_names:
        return await issue_service.search_issues(label_names=label_names)
    return await issue_service.get_all()


@router.get("/{id}", response_model=IssueResponseTo, status_code=status.HTTP_200_OK)
async def get_issue(id: int, issue_service: IssueServiceDep, current_user: CurrentUserDep):
    return await issue_service.get_by_id(id)


@router.put("/{id}", response_model=IssueResponseTo, status_code=status.HTTP_200_OK)
async def update_issue(id: int, issue_in: IssueRequestTo, issue_service: IssueServiceDep, current_user: CurrentUserDep):
    issue = await Issue.get_or_none(id=id)
    if not issue: raise BaseAppException(404, "40403", "Issue not found")

    verify_permissions(current_user, owner_id=issue.editor_id)  # CUSTOMER меняет только свои issue
    return await issue_service.update(id, issue_in)


@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_issue(id: int, issue_service: IssueServiceDep, current_user: CurrentUserDep):
    issue = await Issue.get_or_none(id=id)
    if not issue: raise BaseAppException(404, "40403", "Issue not found")

    verify_permissions(current_user, owner_id=issue.editor_id)
    await issue_service.delete(id)