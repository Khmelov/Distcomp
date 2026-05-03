from dataclasses import dataclass

from app.models.base import BaseEntity
from app.models.notice_state import NoticeState


@dataclass
class Notice(BaseEntity):
    issueId: int = 0
    content: str = ""
    state: NoticeState = NoticeState.PENDING
