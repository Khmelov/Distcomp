from dataclasses import dataclass
from datetime import datetime
from typing import Optional
from .base import BaseEntity


@dataclass
class Notice(BaseEntity):
    # userId: int
    topicId: int
    # title: str
    content: str
    id: int = None
    created: Optional[datetime] = None
    modified: Optional[datetime] = None
