from dataclasses import dataclass, field
from datetime import datetime

@dataclass
class Marker:
    id: int
    name: str
    created_at: datetime = field(default_factory=datetime.now)
