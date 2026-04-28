from dataclasses import dataclass, field
from datetime import datetime

@dataclass
class Note:
    id: int
    article_id: int
    content: str
    created_at: datetime = field(default_factory=datetime.now)