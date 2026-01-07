from app.models.issue import Issue
from app.schemas.issue import IssueCreate, IssueUpdate
from app.repositories.base_repository import BaseRepository
from typing import Optional
from datetime import datetime

class IssueRepository(BaseRepository[Issue]):
    def __init__(self):
        self._issues = []
        self._id_counter = 1

    def add(self, issue_data: IssueCreate) -> Issue:
        issue = Issue(
            id=self._id_counter,
            userId=issue_data.userId,
            title=issue_data.title,
            content=issue_data.content,
            created=datetime.utcnow(),
            modified=datetime.utcnow(),
        )
        self._issues.append(issue)
        self._id_counter += 1
        return issue

    def get_by_id(self, issue_id: int) -> Optional[Issue]:
        return next((i for i in self._issues if i.id == issue_id), None)

    def list(self) -> list[Issue]:
        return self._issues.copy()

    def update(self, update_data: IssueUpdate) -> Optional[Issue]:
        issue = self.get_by_id(update_data.id)
        if issue:
            issue.title = update_data.title
            issue.content = update_data.content
            issue.userId = update_data.userId
            issue.modified = datetime.utcnow()
            return issue
        return None

    def delete(self, issue_id: int) -> bool:
        for i, issue in enumerate(self._issues):
            if issue.id == issue_id:
                del self._issues[i]
                return True
        return False

    def delete_by_id(self, issue_id: int) -> bool:
        return self.delete(issue_id)

