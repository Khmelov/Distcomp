from dataclasses import dataclass, field
from datetime import datetime


@dataclass
class Issue:
    id: int
    author_id: int
    title: str
    content: str
    created: datetime
    modified: datetime
    tag_ids: list[int] = field(default_factory=list)