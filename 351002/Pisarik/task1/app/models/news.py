from dataclasses import dataclass, field
from datetime import datetime
from typing import List

@dataclass
class News:
    id: int
    authorId: int
    title: str
    content: str
    created: datetime
    modified: datetime
    markIds: List[int] = field(default_factory=list)
