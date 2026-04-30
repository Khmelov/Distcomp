from dataclasses import dataclass
from .base import BaseEntity


@dataclass
class Marker(BaseEntity):
    name: str
    id: int = None
