from dataclasses import dataclass, field
from datetime import datetime

@dataclass
class Writer:
    id: int
    login: str
    password: str
    firstname: str
    lastname: str
    created_at: datetime = field(default_factory = datetime.now)

