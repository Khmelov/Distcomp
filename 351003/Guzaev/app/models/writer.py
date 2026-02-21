from dataclasses import dataclass, field
from typing import Optional

@dataclass
class Writer:
    id: int = 0
    login: str = ""
    password: str = ""
    firstname: Optional[str] = None
    lastname: Optional[str] = None
