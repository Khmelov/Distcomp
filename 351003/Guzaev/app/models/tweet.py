from dataclasses import dataclass
from datetime import datetime
from typing import Optional, List


@dataclass
class Tweet:
    id: int = 0
    writer_id: int = 0 # 1:M
    title: str = ""
    content: str = ""
    created: Optional[datetime] = None
    modified: Optional[datetime] = None
    marker_ids: List[int] = None  # M:M связь

    def __post_init__(self):
        if self.marker_ids is None:
            self.marker_ids = []
        if self.created is None:
            self.created = datetime.now()
        if self.modified is None:
            self.modified = datetime.now()
