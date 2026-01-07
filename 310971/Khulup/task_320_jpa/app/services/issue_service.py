from app.repositories.issue_repository import IssueRepository
from app.schemas.issue import IssueCreate, IssueUpdate
from app.models.issue import Issue
from app.core.db import SessionLocal
from typing import Optional
from psycopg2.errors import UniqueViolation

class IssueService:
    def __init__(self, repo: IssueRepository):
        self.repo = repo

    def create_issue(self, data: IssueCreate) -> Issue:
        from app.repositories.user_repository import UserRepository
        
        user_repo = UserRepository(self.repo.session)
        if not user_repo.get_by_id(data.userId):
            raise ValueError("User with this id does not exist")

        try:
            issue = self.repo.add(data)
            self._create_default_markers_for_issue(issue.id, data.userId)
            return issue
        except UniqueViolation as e:
            raise ValueError("Issue with this title already exists")
        except ValueError as e:
            if "already exists" in str(e):
                raise ValueError("Issue with this title already exists")
            raise

    def _create_default_markers_for_issue(self, issue_id: int, user_id: int):
        from app.repositories.marker_repository import MarkerRepository
        from app.schemas.marker import MarkerCreate
        
        marker_repo = MarkerRepository()
        marker_names = [f"red{user_id}", f"green{user_id}", f"blue{user_id}"]
        
        for marker_name in marker_names:
            try:
                marker_data = MarkerCreate(name=marker_name)
                marker = marker_repo.add(marker_data)
                self.repo.add_marker_to_issue(issue_id, marker.id)
            except Exception as e:
                pass

    def get_issue(self, issue_id: int) -> Optional[Issue]:
        return self.repo.get_by_id(issue_id)

    def list_issues(
        self,
        userId: int | None = None,
        title: str | None = None,
        content: str | None = None,
        limit: int = 50,
        offset: int = 0,
        sort_by: str | None = None,
        sort_dir: str = "desc",
    ) -> list[Issue]:
        return self.repo.list(
            userId=userId,
            title=title,
            content=content,
            limit=limit,
            offset=offset,
            sort_by=sort_by,
            sort_dir=sort_dir,
        )

    def update_issue(self, data: IssueUpdate) -> Optional[Issue]:
        return self.repo.update(data)

    def delete_issue(self, issue_id: int) -> bool:
        self._delete_markers_for_issue(issue_id)
        return self.repo.delete_by_id(issue_id)

    def _delete_markers_for_issue(self, issue_id: int):
        from app.repositories.marker_repository import MarkerRepository
        
        marker_repo = MarkerRepository()
        with SessionLocal() as s:
            from app.models.entities import IssueMarkerEntity, MarkerEntity
            from sqlalchemy import select
            
            stmt = select(MarkerEntity).join(IssueMarkerEntity).where(
                IssueMarkerEntity.issue_id == issue_id
            )
            markers = s.execute(stmt).scalars().all()
            
            for marker in markers:
                try:
                    marker_repo.delete(marker.id)
                except Exception:
                    pass

    def add_marker_to_issue(self, issue_id: int, marker_id: int) -> bool:
        return self.repo.add_marker_to_issue(issue_id, marker_id)

