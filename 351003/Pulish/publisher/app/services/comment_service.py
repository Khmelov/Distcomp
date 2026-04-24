from sqlalchemy.orm import Session
import httpx
from app.dto.comment import CommentRequestTo, CommentResponseTo
from app.core.exceptions import NotFoundException, AppException
from app.models.topic import Topic
from app.core.config import settings


class CommentService:
    def __init__(self, db: Session):
        self.db = db
        self.client = httpx.Client(base_url=settings.DISCUSSION_SERVICE_URL)

    def _validate_topic(self, topic_id: int):
        topic = self.db.query(Topic).filter(Topic.id == topic_id).first()
        if not topic:
            raise AppException(
                "Invalid association: Topic not found", 40004, 400)

    def create(self, dto: CommentRequestTo) -> CommentResponseTo:
        self._validate_topic(dto.topicId)

        response = self.client.post(
            "/comments", json=dto.model_dump(exclude_none=True))
        if response.status_code == 201:
            return CommentResponseTo(**response.json())
        raise AppException("Failed to create comment",
                           50000, response.status_code)

    def find_all(self):
        response = self.client.get("/comments")
        if response.status_code == 200:
            return [CommentResponseTo(**c) for c in response.json()]
        return []

    def find_by_id(self, id: int):
        response = self.client.get(f"/comments/{id}")
        if response.status_code == 404:
            raise NotFoundException("Comment not found", 40404)
        return CommentResponseTo(**response.json())

    def update(self, dto: CommentRequestTo):
        self._validate_topic(dto.topicId)

        response = self.client.put(
            f"/comments/{dto.id}", json=dto.model_dump(exclude_none=True))
        if response.status_code == 404:
            raise NotFoundException("Comment not found", 40404)
        if response.status_code not in (200, 204):
            raise AppException("Error updating comment",
                               50000, response.status_code)
        return CommentResponseTo(**response.json())

    def delete(self, id: int):
        response = self.client.delete(f"/comments/{id}")
        if response.status_code == 404:
            raise NotFoundException("Comment not found", 40404)
