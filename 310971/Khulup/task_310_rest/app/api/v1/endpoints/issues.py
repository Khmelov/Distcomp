from fastapi import APIRouter, HTTPException
from app.schemas.issue import IssueCreate, IssueRead, IssueUpdate
from app.services.issue_service import IssueService
from app.repositories.issue_repository import IssueRepository

router = APIRouter()

repo = IssueRepository()
service = IssueService(repo)

@router.post("", response_model=IssueRead, status_code=201)
def create_issue(data: IssueCreate):
    return service.create_issue(data)

@router.get("/{issue_id}", response_model=IssueRead)
def get_issue(issue_id: int):
    issue = service.get_issue(issue_id)
    if not issue:
        raise HTTPException(status_code=404, detail="Issue not found")
    return issue

@router.get("", response_model=list[IssueRead])
def list_issues():
    return service.list_issues()

@router.put("", response_model=IssueRead)
def update_issue(data: IssueUpdate):
    updated = service.update_issue(data)
    if not updated:
        raise HTTPException(status_code=404, detail="Issue not found")
    return updated

@router.delete("/{issue_id}", status_code=204)
def delete_issue(issue_id: int):
    success = service.delete_issue(issue_id)
    if not success:
        raise HTTPException(status_code=404, detail="Issue not found")
