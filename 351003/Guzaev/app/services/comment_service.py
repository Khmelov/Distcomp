from repositories.base_repository import BaseRepository
from models.models import Comment, Tweet
from dtos.comment_dto import CommentRequestTo, CommentResponseTo
from database import SessionLocal
from errors import AppError
from typing import List

comment_repo = BaseRepository[Comment](Comment)


class CommentService:
    def create(self, dto: CommentRequestTo) -> CommentResponseTo:
        with SessionLocal() as db:
            tweet = db.query(Tweet).filter(Tweet.id == dto.tweet_id).first()
            if not tweet:
                raise AppError(status_code=403, message="Tweet not found", error_code=40309)

            entity = Comment(tweet_id=dto.tweet_id, content=dto.content)
            saved = comment_repo.create(db, entity)
            return CommentResponseTo(id=saved.id, tweetId=saved.tweet_id, content=saved.content)

    def get_all(self) -> List[CommentResponseTo]:
        with SessionLocal() as db:
            entities = comment_repo.get_all(db)
            return [CommentResponseTo(id=e.id, tweetId=e.tweet_id, content=e.content) for e in entities]

    def get_by_id(self, id: int) -> CommentResponseTo:
        with SessionLocal() as db:
            entity = comment_repo.get_by_id(db, id)
            if not entity:
                raise AppError(status_code=404, message="Comment not found", error_code=40410)
            return CommentResponseTo(id=entity.id, tweetId=entity.tweet_id, content=entity.content)

    def update(self, id: int, dto: CommentRequestTo) -> CommentResponseTo:
        with SessionLocal() as db:
            entity = comment_repo.get_by_id(db, id)
            if not entity:
                raise AppError(status_code=404, message="Comment not found", error_code=40411)

            tweet = db.query(Tweet).filter(Tweet.id == dto.tweet_id).first()
            if not tweet:
                raise AppError(status_code=403, message="Tweet not found", error_code=40310)

            entity.tweet_id = dto.tweet_id
            entity.content = dto.content
            db.commit()
            db.refresh(entity)
            return CommentResponseTo(id=entity.id, tweetId=entity.tweet_id, content=entity.content)

    def delete(self, id: int) -> None:
        with SessionLocal() as db:
            success = comment_repo.delete(db, id)
            if not success:
                raise AppError(status_code=404, message="Comment not found", error_code=40412)

    def get_by_tweet_id(self, tweet_id: int) -> List[CommentResponseTo]:
        with SessionLocal() as db:
            entities = db.query(Comment).filter(Comment.tweet_id == tweet_id).all()
            return [CommentResponseTo(id=e.id, tweetId=e.tweet_id, content=e.content) for e in entities]
