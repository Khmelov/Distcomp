from dataclasses import dataclass
from datetime import datetime

@dataclass
class Topic:
    id: int
    title: str
    content: str
    author_id: int
    created_at: datetime | None = None
    updated_at: datetime | None = None
