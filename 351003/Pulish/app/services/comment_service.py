from app.dto.comment import CommentRequestTo, CommentResponseTo
from app.models.comment import Comment
from app.repository.inmemory import InMemoryRepository
from app.core.exceptions import NotFoundException


class CommentService:
    def __init__(self):
        self.repo = InMemoryRepository()

    def create(self, dto: CommentRequestTo) -> CommentResponseTo:
        comment = Comment(
            id=0,
            content=dto.content,
            topic_id=dto.topicId
        )
        saved = self.repo.create(comment)
        return self._to_response(saved)

    def find_all(self):
        return [self._to_response(c) for c in self.repo.find_all()]

    def find_by_id(self, id: int):
        comment = self.repo.find_by_id(id)
        if not comment:
            raise NotFoundException("Comment not found", 40404)
        return self._to_response(comment)

    def update(self, id: int, dto: CommentRequestTo):
        comment = self.repo.find_by_id(id)
        if not comment:
            raise NotFoundException("Comment not found", 40404)
        comment.content = dto.content
        comment.topic_id = dto.topicId
        return self._to_response(comment)

    def delete(self, id: int):
        comment = self.repo.find_by_id(id)
        if not comment:
            raise NotFoundException("Comment not found", 40401)
        self.repo.delete(id)

    def _to_response(self, comment: Comment) -> CommentResponseTo:
        return CommentResponseTo(
            id=comment.id,
            content=comment.content,
            topicId=comment.topic_id
        )
