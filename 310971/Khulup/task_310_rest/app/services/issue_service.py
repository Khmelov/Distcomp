from app.repositories.issue_repository import IssueRepository
from app.schemas.issue import IssueCreate, IssueUpdate
from app.models.issue import Issue
from typing import Optional

class IssueService:
    def __init__(self, repo: IssueRepository):
        self.repo = repo

    def create_issue(self, data: IssueCreate) -> Issue:
        return self.repo.add(data)

    def get_issue(self, issue_id: int) -> Optional[Issue]:
        return self.repo.get_by_id(issue_id)

    def list_issues(self) -> list[Issue]:
        return self.repo.list()

    def update_issue(self, data: IssueUpdate) -> Optional[Issue]:
        return self.repo.update(data)

    def delete_issue(self, issue_id: int) -> bool:
        return self.repo.delete_by_id(issue_id)
