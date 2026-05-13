from dataclasses import dataclass
from datetime import datetime
from typing import Optional
from app.models.base import BaseEntity

@dataclass
class Story(BaseEntity):
    writer_id: Optional[int] = None
    title: str = ""
    content: str = ""
    created: Optional[datetime] = None
    modified: Optional[datetime] = None