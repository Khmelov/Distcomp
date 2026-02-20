from app.models.base import BaseEntity
from typing import List


class Topic(BaseEntity):
    def __init__(
        self,
        id: int,
        title: str,
        content: str,
        user_id: int,
        mark_ids: List[int]
    ):
        self.id = id
        self.title = title
        self.content = content
        self.user_id = user_id
        self.mark_ids = mark_ids
