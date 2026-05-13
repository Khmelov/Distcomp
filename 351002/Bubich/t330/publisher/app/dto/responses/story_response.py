from dataclasses import dataclass
from datetime import datetime
from typing import Optional

@dataclass
class StoryResponseTo:
    id: int
    writer_id: Optional[int]
    title: str
    content: str
    created: Optional[str]
    modified: Optional[str]