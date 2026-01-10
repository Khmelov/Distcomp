from app.models.issue import Issue
from app.schemas.issue import IssueCreate, IssueUpdate
from app.repositories.base_repository import BaseRepository
from app.models.entities import IssueEntity, IssueMarkerEntity, MarkerEntity
from app.core.db import SessionLocal
from typing import Optional
from datetime import datetime
from sqlalchemy.orm import Session
from sqlalchemy.exc import IntegrityError
from psycopg2.errors import UniqueViolation

class IssueRepository(BaseRepository[Issue]):
    def __init__(self, session: Optional[Session] = None):
        self.session = session or SessionLocal()

    def add(self, issue_data: IssueCreate) -> Issue:
        issue_entity = IssueEntity(
            user_id=issue_data.userId,
            title=issue_data.title,
            content=issue_data.content,
            created=datetime.utcnow(),
            modified=datetime.utcnow(),
        )
        self.session.add(issue_entity)
        try:
            self.session.commit()
            self.session.refresh(issue_entity)
            return Issue.model_validate(issue_entity)
        except IntegrityError as e:
            self.session.rollback()
            if isinstance(e.orig, UniqueViolation):
                raise e.orig
            raise e

    def get_by_id(self, issue_id: int) -> Optional[Issue]:
        issue_entity = self.session.query(IssueEntity).filter(IssueEntity.id == issue_id).first()
        return Issue.model_validate(issue_entity) if issue_entity else None

    def list(self, userId: int | None = None, title: str | None = None, content: str | None = None,
             limit: int = 50, offset: int = 0, sort_by: str | None = None, sort_dir: str = "asc") -> list[Issue]:
        query = self.session.query(IssueEntity)
        
        if userId:
            query = query.filter(IssueEntity.user_id == userId)
        if title:
            query = query.filter(IssueEntity.title.contains(title))
        if content:
            query = query.filter(IssueEntity.content.contains(content))
        
        if sort_by:
            if sort_by == "userId":
                sort_col = IssueEntity.user_id
            else:
                sort_col = getattr(IssueEntity, sort_by, IssueEntity.id)
            if sort_dir == "desc":
                query = query.order_by(sort_col.desc())
            else:
                query = query.order_by(sort_col.asc())
        else:
            query = query.order_by(IssueEntity.id.desc())
        
        query = query.offset(offset).limit(limit)
        issues = query.all()
        return [Issue.model_validate(i) for i in issues]

    def update(self, update_data: IssueUpdate) -> Optional[Issue]:
        issue_entity = self.session.query(IssueEntity).filter(IssueEntity.id == update_data.id).first()
        if issue_entity:
            issue_entity.title = update_data.title
            issue_entity.content = update_data.content
            issue_entity.user_id = update_data.userId
            issue_entity.modified = datetime.utcnow()
            self.session.commit()
            self.session.refresh(issue_entity)
            return Issue.model_validate(issue_entity)
        return None

    def delete(self, issue_id: int) -> bool:
        issue_entity = self.session.query(IssueEntity).filter(IssueEntity.id == issue_id).first()
        if issue_entity:
            self.session.delete(issue_entity)
            self.session.commit()
            return True
        return False

    def delete_by_id(self, issue_id: int) -> bool:
        return self.delete(issue_id)

    def add_marker_to_issue(self, issue_id: int, marker_id: int) -> bool:
        try:
            marker_assoc = IssueMarkerEntity(issue_id=issue_id, marker_id=marker_id)
            self.session.add(marker_assoc)
            self.session.commit()
            return True
        except:
            self.session.rollback()
            return False

