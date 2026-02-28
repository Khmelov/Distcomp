from app.models.base import BaseEntity


class Comment(BaseEntity):
    def __init__(self, id: int, content: str, topic_id: int):
        self.id = id
        self.content = content
        self.topic_id = topic_id
