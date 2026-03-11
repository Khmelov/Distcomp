from sqlalchemy.orm import Session
from sqlalchemy.exc import IntegrityError
from app.dto.comment import CommentRequestTo, CommentResponseTo
from app.models.comment import Comment
from app.core.exceptions import NotFoundException, AppException


class CommentService:
    def __init__(self, db: Session):
        self.db = db

    def create(self, dto: CommentRequestTo) -> CommentResponseTo:
        comment = Comment(
            content=dto.content,
            topic_id=dto.topicId
        )
        try:
            self.db.add(comment)
            self.db.commit()
            self.db.refresh(comment)
        except IntegrityError:
            self.db.rollback()
            raise AppException(
                "Invalid association: Topic not found", 40004, 400)

        return self._to_response(comment)

    def find_all(self):
        comments = self.db.query(Comment).all()
        return [self._to_response(c) for c in comments]

    def find_by_id(self, id: int):
        comment = self.db.query(Comment).filter(Comment.id == id).first()
        if not comment:
            raise NotFoundException("Comment not found", 40404)
        return self._to_response(comment)

    def update(self, id: int, dto: CommentRequestTo):
        comment = self.db.query(Comment).filter(Comment.id == id).first()
        if not comment:
            raise NotFoundException("Comment not found", 40404)

        comment.content = dto.content
        comment.topic_id = dto.topicId

        try:
            self.db.commit()
            self.db.refresh(comment)
        except IntegrityError:
            self.db.rollback()
            raise AppException(
                "Invalid association: Topic not found", 40004, 400)

        return self._to_response(comment)

    def delete(self, id: int):
        comment = self.db.query(Comment).filter(Comment.id == id).first()
        if not comment:
            raise NotFoundException("Comment not found", 40404)
        self.db.delete(comment)
        self.db.commit()

    def _to_response(self, comment: Comment) -> CommentResponseTo:
        return CommentResponseTo(
            id=comment.id,
            content=comment.content,
            topicId=comment.topic_id
        )
