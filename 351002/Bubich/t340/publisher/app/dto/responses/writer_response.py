from dataclasses import dataclass
from typing import Optional

@dataclass
class WriterResponseTo:
    id: int
    login: str
    firstname: str
    lastname: str