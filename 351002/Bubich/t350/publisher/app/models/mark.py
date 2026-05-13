from dataclasses import dataclass
from publisher.app.models.base import BaseEntity

@dataclass
class Mark(BaseEntity):
    name: str = ""