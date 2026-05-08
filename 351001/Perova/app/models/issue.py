from dataclasses import dataclass, field
from datetime import datetime

from app.models.base import BaseEntity


@dataclass
class Issue(BaseEntity):
    userId: int = 0
    title: str = ""
    content: str = ""
    created: datetime | None = None
    modified: datetime | None = None
    stickerIds: list[int] = field(default_factory=list)
