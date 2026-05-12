from dataclasses import dataclass
from datetime import datetime

@dataclass
class Tweet:
    id: int
    title: str
    content: str
    creator_id: int
    created_at: datetime | None = None
    updated_at: datetime | None = None
