from models.notice import Notice
from dto.requests import NoticeRequestTo
from dto.responses import NoticeResponseTo
from .base import BaseService


class NoticeService(BaseService[Notice, NoticeRequestTo, NoticeResponseTo]):
    def _to_entity(self, request: NoticeRequestTo) -> Notice:
        return Notice(
            # userId=request.userId,
            topicId=request.topicId,
            # title=request.title,
            content=request.content,
        )

    def _to_response(self, entity: Notice) -> NoticeResponseTo:
        return NoticeResponseTo(
            id=entity.id,
            # userId=entity.userId,
            topicId=entity.topicId,
            # title=entity.title,
            content=entity.content,
            created=entity.created,
            modified=entity.modified,
        )
