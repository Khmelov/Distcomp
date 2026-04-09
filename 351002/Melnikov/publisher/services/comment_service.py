from ..common.errors import NotFoundError
from ..common.validation import validate_length
from ..dto.comment_dto import CommentRequestTo, CommentResponseTo
from ..models.comment import Comment
from ..repositories.registry import comment_repository, issue_repository


class CommentService:
    def create(self, dto: CommentRequestTo) -> CommentResponseTo:
        issue = issue_repository.get_by_id(dto.issue_id)
        if not issue:
            raise NotFoundError("Issue not found for comment", 12)

        content = validate_length("content", dto.content, 2, 2048, 31)

        comment = Comment(
            id=0,
            issue_id=dto.issue_id,
            content=content
        )
        created = comment_repository.create(comment)
        return self._to_response(created)

    def get_all(self) -> list[CommentResponseTo]:
        return [self._to_response(comment) for comment in comment_repository.get_all()]

    def get_by_id(self, comment_id: int) -> CommentResponseTo:
        comment = comment_repository.get_by_id(comment_id)
        if not comment:
            raise NotFoundError("Comment not found", 4)
        return self._to_response(comment)

    def update(self, comment_id: int, dto: CommentRequestTo) -> CommentResponseTo:
        comment = comment_repository.get_by_id(comment_id)
        if not comment:
            raise NotFoundError("Comment not found", 4)

        issue = issue_repository.get_by_id(dto.issue_id)
        if not issue:
            raise NotFoundError("Issue not found for comment", 12)

        content = validate_length("content", dto.content, 2, 2048, 31)

        comment.issue_id = dto.issue_id
        comment.content = content

        updated = comment_repository.update(comment)
        return self._to_response(updated)

    def delete(self, comment_id: int) -> None:
        deleted = comment_repository.delete(comment_id)
        if not deleted:
            raise NotFoundError("Comment not found", 4)

    def get_by_issue_id(self, issue_id: int) -> list[CommentResponseTo]:
        return [
            self._to_response(comment)
            for comment in comment_repository.get_all()
            if comment.issue_id == issue_id
        ]

    @staticmethod
    def _to_response(comment: Comment) -> CommentResponseTo:
        return CommentResponseTo(
            id=comment.id,
            issueId=comment.issue_id,
            content=comment.content
        )