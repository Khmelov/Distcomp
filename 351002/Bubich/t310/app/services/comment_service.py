from typing import List
from app.models.comment import Comment
from app.dto.requests.comment_request import CommentRequestTo
from app.dto.responses.comment_response import CommentResponseTo
from app.repository.in_memory_repository import InMemoryRepository
from app.exceptions.custom_exceptions import NotFoundError, ValidationError


class CommentService:
    def __init__(self, repository: InMemoryRepository):
        self.repository = repository

    def create(self, request: CommentRequestTo) -> CommentResponseTo:
        self._validate_request(request)
        comment = Comment(
            story_id=request.story_id,
            content=request.content
        )
        saved_comment = self.repository.save(comment)
        return self._to_response(saved_comment)

    def get_by_id(self, id: int) -> CommentResponseTo:
        comment = self.repository.find_by_id(id)
        if not comment:
            raise NotFoundError(f"Comment with id {id} not found")
        return self._to_response(comment)

    def get_all(self) -> List[CommentResponseTo]:
        comments = self.repository.find_all()
        return [self._to_response(c) for c in comments]

    def update(self, id: int, request: CommentRequestTo) -> CommentResponseTo:
        self._validate_request(request)
        existing_comment = self.repository.find_by_id(id)
        if not existing_comment:
            raise NotFoundError(f"Comment with id {id} not found")

        existing_comment.story_id = request.story_id
        existing_comment.content = request.content
        updated_comment = self.repository.update(existing_comment)
        return self._to_response(updated_comment)

    def delete(self, id: int) -> None:
        if not self.repository.delete_by_id(id):
            raise NotFoundError(f"Comment with id {id} not found")

    def get_by_story_id(self, story_id: int) -> List[CommentResponseTo]:
        all_comments = self.repository.find_all()
        return [self._to_response(c) for c in all_comments if c.story_id == story_id]

    def _validate_request(self, request: CommentRequestTo):
        if not request.content or len(request.content) < 2 or len(request.content) > 2048:
            raise ValidationError("Content must be between 2 and 2048 characters")

    def _to_response(self, comment: Comment) -> CommentResponseTo:
        return CommentResponseTo(
            id=comment.id,
            story_id=comment.story_id,
            content=comment.content
        )