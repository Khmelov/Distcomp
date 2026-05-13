from dataclasses import dataclass
from typing import Optional
from app.dto.base_dto import BaseRequestTo


@dataclass
class StoryRequestTo(BaseRequestTo):
    writer_id: Optional[int] = None
    title: str = ""
    content: str = ""

    def __init__(self, **kwargs):
        # Сначала преобразуем ключи
        super().__init__(**kwargs)