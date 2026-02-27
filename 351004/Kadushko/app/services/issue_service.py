from typing import List
from datetime import datetime
from sqlalchemy.orm import Session
from app.models import Issue, Editor
from app.repository import BaseRepository
from app.schemas.issue import IssueCreate, IssueUpdate, IssueResponse
from app.core.exceptions import EntityNotFoundException, EntityAlreadyExistsException


class IssueService:
    def __init__(self, db: Session):
        self.repo = BaseRepository(Issue, db)
        self.editor_repo = BaseRepository(Editor, db)

    def get_all(
        self,
        page: int = 0,
        size: int = 10,
        sort_by: str = "id",
        sort_order: str = "asc"
    ) -> List[IssueResponse]:
        issues = self.repo.get_all(page=page, size=size, sort_by=sort_by, sort_order=sort_order)
        return [IssueResponse.model_validate(i) for i in issues]

    def get_by_id(self, issue_id: int) -> IssueResponse:
        issue = self.repo.get_by_id(issue_id)
        if not issue:
            raise EntityNotFoundException("Issue", issue_id)
        return IssueResponse.model_validate(issue)

    def create(self, data: IssueCreate) -> IssueResponse:
        editor = self.editor_repo.get_by_id(data.editor_id)
        if not editor:
            raise EntityNotFoundException("Editor", data.editor_id)
        existing = self.repo.get_by_field("title", data.title)
        if existing:
            raise EntityAlreadyExistsException("Issue", "title", data.title)
        now = datetime.utcnow()
        issue = Issue(
            editor_id=data.editor_id,
            title=data.title,
            content=data.content,
            created=now,
            modified=now
        )
        created = self.repo.create(issue)
        return IssueResponse.model_validate(created)

    def update(self, data: IssueUpdate) -> IssueResponse:
        issue = self.repo.get_by_id(data.id)
        if not issue:
            raise EntityNotFoundException("Issue", data.id)
        editor = self.editor_repo.get_by_id(data.editor_id)
        if not editor:
            raise EntityNotFoundException("Editor", data.editor_id)
        existing = self.repo.get_by_field("title", data.title)
        if existing and existing.id != data.id:
            raise EntityAlreadyExistsException("Issue", "title", data.title)
        issue.editor_id = data.editor_id
        issue.title = data.title
        issue.content = data.content
        issue.modified = datetime.utcnow()
        updated = self.repo.update(issue)
        return IssueResponse.model_validate(updated)

    def delete(self, issue_id: int) -> None:
        issue = self.repo.get_by_id(issue_id)
        if not issue:
            raise EntityNotFoundException("Issue", issue_id)
        self.repo.delete(issue)
