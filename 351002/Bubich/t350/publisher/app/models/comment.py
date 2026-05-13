from dataclasses import dataclass
from typing import Optional
from publisher.app.models.base import BaseEntity

@dataclass
class Comment(BaseEntity):
    story_id: Optional[int] = None
    content: str = ""