from dataclasses import dataclass, field
from datetime import datetime
from typing import List


@dataclass
class Story:
    id: int | None = None
    title: str = ""
    content: str = ""
    creator_id: int | None = None
    marker_ids: List[int] = field(default_factory=list)
    created_at: datetime | None = None
    updated_at: datetime | None = None

