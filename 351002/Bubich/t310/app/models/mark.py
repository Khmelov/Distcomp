from dataclasses import dataclass
from app.models.base import BaseEntity

@dataclass
class Mark(BaseEntity):
    name: str = ""