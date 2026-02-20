from app.models.base import BaseEntity


class Mark(BaseEntity):
    def __init__(self, id: int, name: str):
        self.id = id
        self.name = name
