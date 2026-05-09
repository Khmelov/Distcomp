from typing import List
from sqlalchemy.orm import Session
from app.models import Comment, Issue
from app.repository import BaseRepository
from app.schemas.comment import CommentCreate, CommentUpdate, CommentResponse
from app.core.exceptions import EntityNotFoundException


class CommentService:
    def __init__(self, db: Session):
        self.repo = BaseRepository(Comment, db)
        self.issue_repo = BaseRepository(Issue, db)

    def get_all(
        self,
        page: int = 0,
        size: int = 10,
        sort_by: str = "id",
        sort_order: str = "asc"
    ) -> List[CommentResponse]:
        comments = self.repo.get_all(page=page, size=size, sort_by=sort_by, sort_order=sort_order)
        return [CommentResponse.model_validate(c) for c in comments]

    def get_by_id(self, comment_id: int) -> CommentResponse:
        comment = self.repo.get_by_id(comment_id)
        if not comment:
            raise EntityNotFoundException("Comment", comment_id)
        return CommentResponse.model_validate(comment)

    def create(self, data: CommentCreate) -> CommentResponse:
        issue = self.issue_repo.get_by_id(data.issue_id)
        if not issue:
            raise EntityNotFoundException("Issue", data.issue_id)
        comment = Comment(issue_id=data.issue_id, content=data.content)
        created = self.repo.create(comment)
        return CommentResponse.model_validate(created)

    def update(self, data: CommentUpdate) -> CommentResponse:
        comment = self.repo.get_by_id(data.id)
        if not comment:
            raise EntityNotFoundException("Comment", data.id)
        issue = self.issue_repo.get_by_id(data.issue_id)
        if not issue:
            raise EntityNotFoundException("Issue", data.issue_id)
        comment.issue_id = data.issue_id
        comment.content = data.content
        updated = self.repo.update(comment)
        return CommentResponse.model_validate(updated)

    def delete(self, comment_id: int) -> None:
        comment = self.repo.get_by_id(comment_id)
        if not comment:
            raise EntityNotFoundException("Comment", comment_id)
        self.repo.delete(comment)
