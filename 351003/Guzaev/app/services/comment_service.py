from app.repositories.base_repository import InMemoryRepository
from app.models.comment import Comment
from app.dtos.comment_dto import CommentRequestTo, CommentResponseTo
from app.services.tweet_service import tweet_repository
from app.errors import AppError
from typing import List

comment_repository = InMemoryRepository[Comment]()


class CommentService:
    def create(self, dto: CommentRequestTo) -> CommentResponseTo:
        tweet = tweet_repository.get_by_id(dto.tweet_id)
        if not tweet:
            raise AppError(status_code=403, message="Tweet not found", error_code=40309)

        entity = Comment(
            tweet_id=dto.tweet_id,
            content=dto.content
        )
        saved_entity = comment_repository.create(entity)

        # ИСПРАВЛЕНИЕ ЗДЕСЬ
        return CommentResponseTo(
            id=saved_entity.id,
            tweetId=saved_entity.tweet_id,
            content=saved_entity.content
        )

    def get_all(self) -> List[CommentResponseTo]:
        entities = comment_repository.get_all()
        # ИСПРАВЛЕНИЕ ЗДЕСЬ
        return [
            CommentResponseTo(
                id=e.id,
                tweetId=e.tweet_id,
                content=e.content
            ) for e in entities
        ]

    def get_by_id(self, id: int) -> CommentResponseTo:
        entity = comment_repository.get_by_id(id)
        if not entity:
            raise AppError(status_code=404, message="Comment not found", error_code=40410)
        # ИСПРАВЛЕНИЕ ЗДЕСЬ
        return CommentResponseTo(
            id=entity.id,
            tweetId=entity.tweet_id,
            content=entity.content
        )

    def update(self, id: int, dto: CommentRequestTo) -> CommentResponseTo:
        entity = comment_repository.get_by_id(id)
        if not entity:
            raise AppError(status_code=404, message="Comment not found", error_code=40411)

        tweet = tweet_repository.get_by_id(dto.tweet_id)
        if not tweet:
            raise AppError(status_code=403, message="Tweet not found", error_code=40310)

        updated_entity = Comment(
            id=id,
            tweet_id=dto.tweet_id,
            content=dto.content
        )
        comment_repository.update(id, updated_entity)
        # ИСПРАВЛЕНИЕ ЗДЕСЬ
        return CommentResponseTo(
            id=updated_entity.id,
            tweetId=updated_entity.tweet_id,
            content=updated_entity.content
        )

    def delete(self, id: int) -> None:
        entity = comment_repository.get_by_id(id)
        if not entity:
            raise AppError(status_code=404, message="Comment not found", error_code=40412)
        comment_repository.delete(id)

    def get_by_tweet_id(self, tweet_id: int) -> List[CommentResponseTo]:
        entities = comment_repository.find_by_condition(lambda c: c.tweet_id == tweet_id)
        return [
            CommentResponseTo(
                id=e.id,
                tweetId=e.tweet_id,
                content=e.content
            ) for e in entities
        ]
