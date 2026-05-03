from models.notice import Notice
from dto.requests import NoticeRequestTo
from dto.responses import NoticeResponseTo
from .base import BaseService
from repository.interface import IRepository


class NoticeService(BaseService[Notice, NoticeRequestTo, NoticeResponseTo]):
    def __init__(self, notice_repo: IRepository[Notice], topic_repo: IRepository):
        super().__init__(notice_repo)
        self._topic_repo = topic_repo

    def create(self, request: NoticeRequestTo) -> NoticeResponseTo:
        if not self._topic_repo.get(request.topicId):
            raise ValueError("Topic not found")
        return super().create(request)

    def _to_entity(self, request: NoticeRequestTo) -> Notice:
        return Notice(
            topicId=request.topicId,
            content=request.content,
        )

    def _to_response(self, entity: Notice) -> NoticeResponseTo:
        return NoticeResponseTo(
            id=entity.id,
            topicId=entity.topicId,
            content=entity.content,
            created=entity.created,
            modified=entity.modified,
        )
