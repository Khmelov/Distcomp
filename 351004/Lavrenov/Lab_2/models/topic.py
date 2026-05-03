from dataclasses import dataclass, field
from typing import List, Optional
from datetime import datetime
from .base import BaseEntity


@dataclass
class Topic(BaseEntity):
    userId: int
    title: str
    content: str
    markerIds: List[int] = field(default_factory=list)
    id: int = None
    created: Optional[datetime] = None
    modified: Optional[datetime] = None
