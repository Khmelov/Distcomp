import random
from sqlalchemy.orm import Session
from app.dto.comment import CommentRequestTo, CommentResponseTo
from app.core.exceptions import NotFoundException, AppException
from app.models.topic import Topic
from app.kafka import manager as kafka
from app.cache.redis_client import cache_get, cache_set, cache_delete


class CommentService:
    def __init__(self, db: Session):
        self.db = db

    def _validate_topic(self, topic_id: int):
        topic = self.db.query(Topic).filter(Topic.id == topic_id).first()
        if not topic:
            raise AppException(
                "Invalid association: Topic not found", 40004, 400)

    def create(self, dto: CommentRequestTo) -> CommentResponseTo:
        self._validate_topic(dto.topicId)

        comment_id = dto.id if dto.id else random.randint(1, 2_000_000_000)

        payload = {
            "id": comment_id,
            "content": dto.content,
            "topicId": dto.topicId,
            "state": "PENDING",
        }
        kafka.send_fire_and_forget("POST", payload, key=dto.topicId)

        result = CommentResponseTo(
            id=comment_id,
            content=dto.content,
            topicId=dto.topicId,
            state="PENDING",
        )
        cache_set(f"comment:{comment_id}", result.model_dump())
        cache_delete("comments:all")
        return result

    def find_all(self) -> list[CommentResponseTo]:
        cached = cache_get("comments:all")
        if cached is not None:
            return [CommentResponseTo(**c) for c in cached]

        response = kafka.send_and_wait("GET_ALL", {}, key=None)
        if response is None:
            raise AppException(
                "Kafka timeout: could not get comments", 50000, 504)
        status = response.get("status", 200)
        if status != 200:
            raise AppException(response.get("error", "Error"), 50000, status)
        result = [CommentResponseTo(**c) for c in response.get("payload", [])]
        cache_set("comments:all", [r.model_dump() for r in result])
        return result

    def find_by_id(self, id: int) -> CommentResponseTo:
        cached = cache_get(f"comment:{id}")
        if cached is not None:
            return CommentResponseTo(**cached)

        response = kafka.send_and_wait("GET", {"id": id}, key=id)
        if response is None:
            raise AppException(
                "Kafka timeout: could not get comment", 50000, 504)
        status = response.get("status", 200)
        if status == 404:
            raise NotFoundException("Comment not found", 40404)
        if status != 200:
            raise AppException(response.get("error", "Error"), 50000, status)
        result = CommentResponseTo(**response["payload"])
        cache_set(f"comment:{id}", result.model_dump())
        return result

    def update(self, dto: CommentRequestTo) -> CommentResponseTo:
        self._validate_topic(dto.topicId)

        payload = {
            "id": dto.id,
            "content": dto.content,
            "topicId": dto.topicId,
        }
        response = kafka.send_and_wait("PUT", payload, key=dto.topicId)
        if response is None:
            raise AppException(
                "Kafka timeout: could not update comment", 50000, 504)
        status = response.get("status", 200)
        if status == 404:
            raise NotFoundException("Comment not found", 40404)
        if status not in (200, 204):
            raise AppException(response.get("error", "Error"), 50000, status)
        result = CommentResponseTo(**response["payload"])
        cache_set(f"comment:{dto.id}", result.model_dump())
        cache_delete("comments:all")
        return result

    def delete(self, id: int):
        response = kafka.send_and_wait("DELETE", {"id": id}, key=id)
        if response is None:
            raise AppException(
                "Kafka timeout: could not delete comment", 50000, 504)
        status = response.get("status", 204)
        if status == 404:
            raise NotFoundException("Comment not found", 40404)
        if status not in (200, 204):
            raise AppException(response.get("error", "Error"), 50000, status)
        cache_delete(f"comment:{id}")
        cache_delete("comments:all")
