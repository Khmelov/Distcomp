from dataclasses import dataclass

from app.models.base import BaseEntity


@dataclass
class Notice(BaseEntity):
    issueId: int = 0
    content: str = ""
