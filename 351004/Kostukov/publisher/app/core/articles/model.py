from dataclasses import dataclass, field
from datetime import datetime
from typing import List

@dataclass
class Article:
    id: int
    writer_id: int
    title: str
    content: str
    marker_ids: List[int] = field(default_factory=list)
    created: datetime = field(default_factory=datetime.now)
    modified: datetime = field(default_factory=datetime.now)
