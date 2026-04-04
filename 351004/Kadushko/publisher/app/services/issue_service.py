from typing import List
from datetime import datetime
from sqlalchemy.orm import Session
from app.models import Issue, Editor, Marker
from app.repository import BaseRepository
from app.schemas.issue import IssueCreate, IssueUpdate, IssueResponse
from app.schemas.marker import MarkerResponse
from app.core.exceptions import EntityNotFoundException, EntityAlreadyExistsException


class IssueService:
    def __init__(self, db: Session):
        self.repo = BaseRepository(Issue, db)
        self.editor_repo = BaseRepository(Editor, db)
        self.marker_repo = BaseRepository(Marker, db)

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
        self.repo.db.add(issue)
        self.repo.db.flush()  # get issue.id without committing

        if data.markers:
            for marker_name in data.markers:
                marker = self.marker_repo.get_by_field("name", marker_name)
                if not marker:
                    marker = Marker(name=marker_name)
                    self.repo.db.add(marker)
                    self.repo.db.flush()
                if marker not in issue.markers:
                    issue.markers.append(marker)

        self.repo.db.commit()
        self.repo.db.refresh(issue)
        return IssueResponse.model_validate(issue)

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
        # collect markers that are only linked to this issue
        orphan_markers = [m for m in issue.markers if len(m.issues) == 1]
        self.repo.delete(issue)
        for marker in orphan_markers:
            self.repo.db.delete(marker)
        self.repo.db.commit()

    def get_markers(self, issue_id: int) -> List[MarkerResponse]:
        issue = self.repo.get_by_id(issue_id)
        if not issue:
            raise EntityNotFoundException("Issue", issue_id)
        return [MarkerResponse.model_validate(m) for m in issue.markers]

    def add_marker(self, issue_id: int, marker_id: int) -> MarkerResponse:
        issue = self.repo.get_by_id(issue_id)
        if not issue:
            raise EntityNotFoundException("Issue", issue_id)
        marker = self.marker_repo.get_by_id(marker_id)
        if not marker:
            raise EntityNotFoundException("Marker", marker_id)
        if marker not in issue.markers:
            issue.markers.append(marker)
            self.repo.db.commit()
        return MarkerResponse.model_validate(marker)

    def remove_marker(self, issue_id: int, marker_id: int) -> None:
        issue = self.repo.get_by_id(issue_id)
        if not issue:
            raise EntityNotFoundException("Issue", issue_id)
        marker = self.marker_repo.get_by_id(marker_id)
        if not marker:
            raise EntityNotFoundException("Marker", marker_id)
        if marker in issue.markers:
            issue.markers.remove(marker)
            self.repo.db.commit()