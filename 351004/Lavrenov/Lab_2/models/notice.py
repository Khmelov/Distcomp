from dataclasses import dataclass
from datetime import datetime
from typing import Optional
from .base import BaseEntity


@dataclass
class Notice(BaseEntity):
    topicId: int
    content: str
    id: int = None
    created: Optional[datetime] = None
    modified: Optional[datetime] = None
